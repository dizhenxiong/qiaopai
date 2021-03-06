/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2008 Oracle.  All rights reserved.
 *
 * $Id: SecondaryCursor.java,v 1.45 2008/06/10 02:52:08 cwl Exp $
 */

package com.sleepycat.je;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.sleepycat.je.dbi.CursorImpl;
import com.sleepycat.je.dbi.GetMode;
import com.sleepycat.je.dbi.CursorImpl.SearchMode;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.txn.Locker;
import com.sleepycat.je.utilint.DatabaseUtil;

/**
 * A database cursor for a secondary database. Cursors are not thread safe and
 * the application is responsible for coordinating any multithreaded access to
 * a single cursor object.
 *
 * <p>Secondary cursors are returned by {@link SecondaryDatabase#openCursor
 * SecondaryDatabase.openCursor} and {@link
 * SecondaryDatabase#openSecondaryCursor
 * SecondaryDatabase.openSecondaryCursor}.  The distinguishing characteristics
 * of a secondary cursor are:</p>
 *
 * <ul> <li>Direct calls to <code>put()</code> methods on a secondary cursor
 * are prohibited.
 *
 * <li>The {@link #delete} method of a secondary cursor will delete the primary
 * record and as well as all its associated secondary records.
 *
 * <li>Calls to all get methods will return the data from the associated
 * primary database.
 *
 * <li>Additional get method signatures are provided to return the primary key
 * in an additional pKey parameter.
 *
 * <li>Calls to {@link #dup} will return a {@link com.sleepycat.je.SecondaryCursor}.
 *
 * <li>The {@link #dupSecondary} method is provided to return a {@link
 * com.sleepycat.je.SecondaryCursor} that doesn't require casting.  </ul>
 *
 * <p>To obtain a secondary cursor with default attributes:</p>
 *
 * <blockquote><pre>
 *     SecondaryCursor cursor = myDb.openSecondaryCursor(txn, null);
 * </pre></blockquote>
 *
 * <p>To customize the attributes of a cursor, use a CursorConfig object.</p>
 *
 * <blockquote><pre>
 *     CursorConfig config = new CursorConfig();
 *     config.setDirtyRead(true);
 *     SecondaryCursor cursor = myDb.openSecondaryCursor(txn, config);
 * </pre></blockquote>
 */
public class SecondaryCursor extends Cursor {

    private SecondaryDatabase secondaryDb;
    private Database primaryDb;

    /**
     * Cursor constructor. Not public. To get a cursor, the user should call
     * SecondaryDatabase.cursor();
     */
    SecondaryCursor(SecondaryDatabase dbHandle,
                    Transaction txn,
                    CursorConfig cursorConfig)
        throws DatabaseException {

        super(dbHandle, txn, cursorConfig);
        secondaryDb = dbHandle;
        primaryDb = dbHandle.getPrimaryDatabase();
    }

    /**
     * Copy constructor.
     */
    private SecondaryCursor(SecondaryCursor cursor, boolean samePosition)
        throws DatabaseException {

        super(cursor, samePosition);
        secondaryDb = cursor.secondaryDb;
        primaryDb = cursor.primaryDb;
    }

    /**
     * Returns the primary {@link com.sleepycat.je.Database Database}
     * associated with this cursor.
     *
     * <p>Calling this method is the equivalent of the following
     * expression:</p>
     *
     * <blockquote><pre>
     *         ((SecondaryDatabase) this.getDatabase()).getPrimaryDatabase()
     * </pre></blockquote>
     *
     * @return The primary {@link com.sleepycat.je.Database Database}
     * associated with this cursor.
     */
    public Database getPrimaryDatabase() {
	return primaryDb;
    }

    /**
     * Returns a new <code>SecondaryCursor</code> for the same transaction as
     * the original cursor.
     */
    @Override
    public Cursor dup(boolean samePosition)
        throws DatabaseException {

        checkState(true);
        return new SecondaryCursor(this, samePosition);
    }

    /**
     * Returns a new copy of the cursor as a <code>SecondaryCursor</code>.
     *
     * <p>Calling this method is the equivalent of calling {@link #dup} and
     * casting the result to {@link com.sleepycat.je.SecondaryCursor}.</p>
     *
     * @see #dup
     */
    public SecondaryCursor dupSecondary(boolean samePosition)
        throws DatabaseException {

        return (SecondaryCursor) dup(samePosition);
    }

    /**
     * Delete the key/data pair to which the cursor refers from the primary
     * database and all secondary indices.
     *
     * <p>This method behaves as if {@link Database#delete} were called for the
     * primary database, using the primary key obtained via the secondary key
     * parameter.</p>
     *
     * The cursor position is unchanged after a delete, and subsequent calls to
     * cursor functions expecting the cursor to refer to an existing key will
     * fail.
     */
    @Override
    public OperationStatus delete()
        throws DatabaseException {

        checkState(true);
        checkUpdatesAllowed("delete");
        trace(Level.FINEST, "SecondaryCursor.delete: ", null);

        /* Read the primary key (the data of a secondary). */
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry pKey = new DatabaseEntry();
        OperationStatus status = getCurrentInternal(key, pKey,
                                                    LockMode.RMW);

        /* Delete the primary and all secondaries (including this one). */
        if (status == OperationStatus.SUCCESS) {
            status =
		primaryDb.deleteInternal(cursorImpl.getLocker(), pKey, null);
            if (status != OperationStatus.SUCCESS) {
                SecondaryDatabase secDb = (SecondaryDatabase) getDatabase();
                throw secDb.secondaryCorruptException();
            }
        }
        return status;
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus put(DatabaseEntry key, DatabaseEntry data)
        throws DatabaseException {

        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putNoOverwrite(DatabaseEntry key,
                                          DatabaseEntry data)
        throws DatabaseException {

        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putNoDupData(DatabaseEntry key, DatabaseEntry data)
        throws DatabaseException {

        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putCurrent(DatabaseEntry data)
        throws DatabaseException {

        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * Returns the key/data pair to which the cursor refers.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEMPTY
     * OperationStatus.KEYEMPTY} if the key/pair at the cursor position has been
     * deleted; otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getCurrent(DatabaseEntry key,
                                      DatabaseEntry data,
                                      LockMode lockMode)
        throws DatabaseException {

        return getCurrent(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Returns the key/data pair to which the cursor refers.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEMPTY
     * OperationStatus.KEYEMPTY} if the key/pair at the cursor position has been
     * deleted; otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getCurrent(DatabaseEntry key,
                                      DatabaseEntry pKey,
                                      DatabaseEntry data,
                                      LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getCurrent: ", lockMode);

        return getCurrentInternal(key, pKey, data, lockMode);
    }

    /**
     * Move the cursor to the first key/data pair of the database, and return
     * that pair.  If the first key has duplicate values, the first data item
     * in the set of duplicates is returned.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getFirst(DatabaseEntry key,
                                    DatabaseEntry data,
                                    LockMode lockMode)
        throws DatabaseException {

        return getFirst(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the first key/data pair of the database, and return
     * that pair.  If the first key has duplicate values, the first data item
     * in the set of duplicates is returned.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getFirst(DatabaseEntry key,
                                    DatabaseEntry pKey,
                                    DatabaseEntry data,
                                    LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getFirst: ", lockMode);

        return position(key, pKey, data, lockMode, true);
    }

    /**
     * Move the cursor to the last key/data pair of the database, and return
     * that pair.  If the last key has duplicate values, the last data item in
     * the set of duplicates is returned.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getLast(DatabaseEntry key,
                                   DatabaseEntry data,
                                   LockMode lockMode)
        throws DatabaseException {

        return getLast(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the last key/data pair of the database, and return
     * that pair.  If the last key has duplicate values, the last data item in
     * the set of duplicates is returned.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getLast(DatabaseEntry key,
                                   DatabaseEntry pKey,
                                   DatabaseEntry data,
                                   LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getLast: ", lockMode);

        return position(key, pKey, data, lockMode, false);
    }

    /**
     * Move the cursor to the next key/data pair and return that pair.  If the
     * matching key has duplicate values, the first data item in the set of
     * duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the first
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the next key/data pair of the database, and that pair
     * is returned.  In the presence of duplicate key values, the value of the
     * key may not change.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getNext(DatabaseEntry key,
                                   DatabaseEntry data,
                                   LockMode lockMode)
        throws DatabaseException {

        return getNext(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the next key/data pair and return that pair.  If the
     * matching key has duplicate values, the first data item in the set of
     * duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the first
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the next key/data pair of the database, and that pair
     * is returned.  In the presence of duplicate key values, the value of the
     * key may not change.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getNext(DatabaseEntry key,
                                   DatabaseEntry pKey,
                                   DatabaseEntry data,
                                   LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getNext: ", lockMode);

        if (cursorImpl.isNotInitialized()) {
            return position(key, pKey, data, lockMode, true);
        } else {
            return retrieveNext(key, pKey, data, lockMode, GetMode.NEXT);
        }
    }

    /**
     * If the next key/data pair of the database is a duplicate data record for
     * the current key/data pair, move the cursor to the next key/data pair of
     * the database and return that pair.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getNextDup(DatabaseEntry key,
                                      DatabaseEntry data,
                                      LockMode lockMode)
        throws DatabaseException {

        return getNextDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * If the next key/data pair of the database is a duplicate data record for
     * the current key/data pair, move the cursor to the next key/data pair of
     * the database and return that pair.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getNextDup(DatabaseEntry key,
                                      DatabaseEntry pKey,
                                      DatabaseEntry data,
                                      LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getNextDup: ", lockMode);

        return retrieveNext(key, pKey, data, lockMode, GetMode.NEXT_DUP);
    }

    /**
     * Move the cursor to the next non-duplicate key/data pair and return that
     * pair.  If the matching key has duplicate values, the first data item in
     * the set of duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the first
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the next non-duplicate key of the database, and that
     * key/data pair is returned.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getNextNoDup(DatabaseEntry key,
                                        DatabaseEntry data,
                                        LockMode lockMode)
        throws DatabaseException {

        return getNextNoDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the next non-duplicate key/data pair and return that
     * pair.  If the matching key has duplicate values, the first data item in
     * the set of duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the first
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the next non-duplicate key of the database, and that
     * key/data pair is returned.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getNextNoDup(DatabaseEntry key,
                                        DatabaseEntry pKey,
                                        DatabaseEntry data,
                                        LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getNextNoDup: ", null, null,
              lockMode);

        if (cursorImpl.isNotInitialized()) {
            return position(key, pKey, data, lockMode, true);
        } else {
            return retrieveNext(key, pKey, data, lockMode,
                                GetMode.NEXT_NODUP);
        }
    }

    /**
     * Move the cursor to the previous key/data pair and return that pair. If
     * the matching key has duplicate values, the last data item in the set of
     * duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the last
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the previous key/data pair of the database, and that
     * pair is returned. In the presence of duplicate key values, the value of
     * the key may not change.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getPrev(DatabaseEntry key,
                                   DatabaseEntry data,
                                   LockMode lockMode)
        throws DatabaseException {

        return getPrev(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the previous key/data pair and return that pair. If
     * the matching key has duplicate values, the last data item in the set of
     * duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the last
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the previous key/data pair of the database, and that
     * pair is returned. In the presence of duplicate key values, the value of
     * the key may not change.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getPrev(DatabaseEntry key,
                                   DatabaseEntry pKey,
                                   DatabaseEntry data,
                                   LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getPrev: ", lockMode);

        if (cursorImpl.isNotInitialized()) {
            return position(key, pKey, data, lockMode, false);
        } else {
            return retrieveNext(key, pKey, data, lockMode, GetMode.PREV);
        }
    }

    /**
     * If the previous key/data pair of the database is a duplicate data record
     * for the current key/data pair, move the cursor to the previous key/data
     * pair of the database and return that pair.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getPrevDup(DatabaseEntry key,
                                      DatabaseEntry data,
                                      LockMode lockMode)
        throws DatabaseException {

        return getPrevDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * If the previous key/data pair of the database is a duplicate data record
     * for the current key/data pair, move the cursor to the previous key/data
     * pair of the database and return that pair.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getPrevDup(DatabaseEntry key,
                                      DatabaseEntry pKey,
                                      DatabaseEntry data,
                                      LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getPrevDup: ", lockMode);

        return retrieveNext(key, pKey, data, lockMode, GetMode.PREV_DUP);
    }

    /**
     * Move the cursor to the previous non-duplicate key/data pair and return
     * that pair.  If the matching key has duplicate values, the last data item
     * in the set of duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the last
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the previous non-duplicate key of the database, and
     * that key/data pair is returned.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getPrevNoDup(DatabaseEntry key,
                                        DatabaseEntry data,
                                        LockMode lockMode)
        throws DatabaseException {

        return getPrevNoDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the previous non-duplicate key/data pair and return
     * that pair.  If the matching key has duplicate values, the last data item
     * in the set of duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the last
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the previous non-duplicate key of the database, and
     * that key/data pair is returned.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getPrevNoDup(DatabaseEntry key,
                                        DatabaseEntry pKey,
                                        DatabaseEntry data,
                                        LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getPrevNoDup: ", lockMode);

        if (cursorImpl.isNotInitialized()) {
            return position(key, pKey, data, lockMode, false);
        } else {
            return retrieveNext(key, pKey, data, lockMode,
                                GetMode.PREV_NODUP);
        }
    }

    /**
     * Move the cursor to the given key of the database, and return the datum
     * associated with the given key.  If the matching key has duplicate
     * values, the first data item in the set of duplicates is returned.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getSearchKey(DatabaseEntry key,
                                        DatabaseEntry data,
                                        LockMode lockMode)
        throws DatabaseException {

        return getSearchKey(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the given key of the database, and return the datum
     * associated with the given key.  If the matching key has duplicate
     * values, the first data item in the set of duplicates is returned.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getSearchKey(DatabaseEntry key,
                                        DatabaseEntry pKey,
                                        DatabaseEntry data,
                                        LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchKey: ", key, null,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.SET);
    }

    /**
     * Move the cursor to the closest matching key of the database, and return
     * the data item associated with the matching key.  If the matching key has
     * duplicate values, the first data item in the set of duplicates is
     * returned.
     *
     * <p>The returned key/data pair is for the smallest key greater than or
     * equal to the specified key (as determined by the key comparison
     * function), permitting partial key matches and range searches.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key used as input and returned as output.  It
     * must be initialized with a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    @Override
    public OperationStatus getSearchKeyRange(DatabaseEntry key,
                                             DatabaseEntry data,
                                             LockMode lockMode)
        throws DatabaseException {

        return getSearchKeyRange(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the closest matching key of the database, and return
     * the data item associated with the matching key.  If the matching key has
     * duplicate values, the first data item in the set of duplicates is
     * returned.
     *
     * <p>The returned key/data pair is for the smallest key greater than or
     * equal to the specified key (as determined by the key comparison
     * function), permitting partial key matches and range searches.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key used as input and returned as output.  It
     * must be initialized with a non-null byte array by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getSearchKeyRange(DatabaseEntry key,
                                             DatabaseEntry pKey,
                                             DatabaseEntry data,
                                             LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchKeyRange: ", key, data,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.SET_RANGE);
    }

    /**
     * This operation is not allowed with this method signature. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method with the <code>pKey</code> parameter should be
     * used instead.
     */
    @Override
    public OperationStatus getSearchBoth(DatabaseEntry key,
                                         DatabaseEntry data,
                                         LockMode lockMode)
        throws DatabaseException {

        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * Move the cursor to the specified secondary and primary key, where both
     * the primary and secondary key items must match.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param pKey the primary key used as input.  It must be initialized with a
     * non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getSearchBoth(DatabaseEntry key,
                                         DatabaseEntry pKey,
                                         DatabaseEntry data,
                                         LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", true);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchBoth: ", key, data,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.BOTH);
    }

    /**
     * This operation is not allowed with this method signature. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method with the <code>pKey</code> parameter should be
     * used instead.
     */
    @Override
    public OperationStatus getSearchBothRange(DatabaseEntry key,
                                              DatabaseEntry data,
                                              LockMode lockMode)
        throws DatabaseException {

        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * Move the cursor to the specified secondary key and closest matching
     * primary key of the database.
     *
     * <p>In the case of any database supporting sorted duplicate sets, the
     * returned key/data pair is for the smallest primary key greater than or
     * equal to the specified primary key (as determined by the key comparison
     * function), permitting partial matches and range searches in duplicate
     * data sets.</p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @throws NullPointerException if a DatabaseEntry parameter is null or does
     * not contain a required non-null byte array.
     *
     * @throws DeadlockException if the operation was selected to resolve a
     * deadlock.
     *
     * @throws IllegalArgumentException if an invalid parameter was specified.
     *
     * @throws DatabaseException if a failure occurs.
     *
     * @param key the secondary key used as input and returned as output.  It
     * must be initialized with a non-null byte array by the caller.
     *
     * @param pKey the primary key used as input and returned as output.  It
     * must be initialized with a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     */
    public OperationStatus getSearchBothRange(DatabaseEntry key,
                                              DatabaseEntry pKey,
                                              DatabaseEntry data,
                                              LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", true);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchBothRange: ", key, data,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.BOTH_RANGE);
    }

    /**
     * Returns the current key and data.
     */
    private OperationStatus getCurrentInternal(DatabaseEntry key,
                                               DatabaseEntry pKey,
                                               DatabaseEntry data,
                                               LockMode lockMode)
        throws DatabaseException {

        OperationStatus status = getCurrentInternal(key, pKey, lockMode);
        if (status == OperationStatus.SUCCESS) {

            /*
             * May return KEYEMPTY if read-uncommitted and the primary was
             * deleted.
             */
            status = readPrimaryAfterGet(key, pKey, data, lockMode);
        }
        return status;
    }

    /**
     * Calls search() and retrieves primary data.
     */
    OperationStatus search(DatabaseEntry key,
                           DatabaseEntry pKey,
                           DatabaseEntry data,
                           LockMode lockMode,
                           SearchMode searchMode)
        throws DatabaseException {

        /*
         * Perform retries to account for deletions during a read-uncommitted.
         */
        while (true) {
            OperationStatus status = search(key, pKey, lockMode, searchMode);
            if (status != OperationStatus.SUCCESS) {
                return status;
            }
            status = readPrimaryAfterGet(key, pKey, data, lockMode);
            if (status == OperationStatus.SUCCESS) {
                return status;
            }
        }
    }

    /**
     * Calls position() and retrieves primary data.
     */
    OperationStatus position(DatabaseEntry key,
                             DatabaseEntry pKey,
                             DatabaseEntry data,
                             LockMode lockMode,
                             boolean first)
        throws DatabaseException {

        /*
         * Perform retries to account for deletions during a read-uncommitted.
         */
        while (true) {
            OperationStatus status = position(key, pKey, lockMode, first);
            if (status != OperationStatus.SUCCESS) {
                return status;
            }
            status = readPrimaryAfterGet(key, pKey, data, lockMode);
            if (status == OperationStatus.SUCCESS) {
                return status;
            }
        }
    }

    /**
     * Calls retrieveNext() and retrieves primary data.
     */
    OperationStatus retrieveNext(DatabaseEntry key,
                                 DatabaseEntry pKey,
                                 DatabaseEntry data,
                                 LockMode lockMode,
                                 GetMode getMode)
        throws DatabaseException {

        /*
         * Perform retries to account for deletions during a read-uncommitted.
         */
        while (true) {
            OperationStatus status = retrieveNext(key, pKey, lockMode,
                                                  getMode);
            if (status != OperationStatus.SUCCESS) {
                return status;
            }
            status = readPrimaryAfterGet(key, pKey, data, lockMode);
            if (status == OperationStatus.SUCCESS) {
                return status;
            }
        }
    }

    /**
     * Reads the primary data for a primary key that was read via a secondary.
     * When SUCCESS is returned by this method, the caller should return
     * SUCCESS.  When KEYEMPTY is returned, the caller should treat this as a
     * deleted record and either retry the operation (in the case of position,
     * search, and retrieveNext) or return KEYEMPTY (in the case of
     * getCurrent).  KEYEMPTY is only returned when read-uncommitted is used.
     *
     * @return SUCCESS if the primary was read succesfully, or KEYEMPTY if
     * using read-uncommitted and the primary has been deleted, or KEYEMPTY if
     * using read-uncommitted and the primary has been updated and no longer
     * contains the secondary key.
     *
     * @throws DatabaseException to indicate a corrupt secondary reference if
     * the primary record is not found and read-uncommitted is not used (or
     * read-uncommitted is used, but we cannot verify that a valid deletion has
     * occured).
     */
    private OperationStatus readPrimaryAfterGet(DatabaseEntry key,
                                                DatabaseEntry pKey,
                                                DatabaseEntry data,
                                                LockMode lockMode)
        throws DatabaseException {

        /*
         * There is no need to read the primary if no data and no locking
         * (read-uncommitted) are requested by the caller.  However, if partial
         * data is requested along with read-uncommitted, then we must read all
         * data in order to call the key creator below. [#14966]
         */
        DatabaseEntry copyToPartialEntry = null;
        boolean readUncommitted = isReadUncommittedMode(lockMode);
        if (readUncommitted && data.getPartial()) {
            if (data.getPartialLength() == 0) {
                /* No need to read the primary. */
                data.setData(LogUtils.ZERO_LENGTH_BYTE_ARRAY);
                return OperationStatus.SUCCESS;
            } else {
                /* Read all data and then copy the requested partial data. */
                copyToPartialEntry = data;
                data = new DatabaseEntry();
            }
        }

        Locker locker = cursorImpl.getLocker();
        Cursor cursor = null;
        try {

            /*
             * Do not release non-transactional locks when reading the primary
             * cursor.  They are held until all locks for this operation are
             * released by the secondary cursor.  [#15573]
             */
	    cursor = new Cursor(primaryDb, locker, null,
                                true /*retainNonTxnLocks*/);
            OperationStatus status =
                cursor.search(pKey, data, lockMode, SearchMode.SET);
            if (status != OperationStatus.SUCCESS) {

                /*
                 * If using read-uncommitted and the primary is not found,
                 * check to see if the secondary key also has been deleted.  If
                 * so, the primary was deleted in between reading the secondary
                 * and the primary.  It is not corrupt, so we return KEYEMPTY.
                 */
                if (readUncommitted) {
                    status = getCurrentInternal(key, pKey, lockMode);
                    if (status == OperationStatus.KEYEMPTY) {
                        return status;
                    }
                }

                /* Secondary reference is corrupt. */
                SecondaryDatabase secDb = (SecondaryDatabase) getDatabase();
                throw secDb.secondaryCorruptException();
            }

            /*
             * If using read-uncommitted and the primary was found, check to
             * see if primary was updated so that it no longer contains the
             * secondary key.  If it has been, return KEYEMPTY.
             */
            if (readUncommitted) {
                SecondaryConfig conf =
                    secondaryDb.getPrivateSecondaryConfig();

                /*
                 * If the secondary key is immutable, or the key creators are
                 * null (the database is read only), then we can skip this
                 * check.
                 */
                if (conf.getImmutableSecondaryKey()) {
                    /* Do nothing. */
                } else if (conf.getKeyCreator() != null) {

                    /*
                     * Check that the key we're using is equal to the key
                     * returned by the key creator.
                     */
                    DatabaseEntry secKey = new DatabaseEntry();
                    if (!conf.getKeyCreator().createSecondaryKey
                            (secondaryDb, pKey, data, secKey) ||
                        !secKey.equals(key)) {
                        return OperationStatus.KEYEMPTY;
                    }
                } else if (conf.getMultiKeyCreator() != null) {

                    /*
                     * Check that the key we're using is in the set returned by
                     * the key creator.
                     */
                    Set<DatabaseEntry> results = new HashSet<DatabaseEntry>();
                    conf.getMultiKeyCreator().createSecondaryKeys
                        (secondaryDb, pKey, data, results);
                    if (!results.contains(key)) {
                        return OperationStatus.KEYEMPTY;
                    }
                }
            }

            /*
             * When a partial entry was requested but we read all the data,
             * copy the requested partial data to the caller's entry. [#14966]
             */
            if (copyToPartialEntry != null) {
                CursorImpl.setDbt(copyToPartialEntry, data.getData());
            }
            return OperationStatus.SUCCESS;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Note that this flavor of checkArgs doesn't require that the dbt data is
     * set.
     */
    private void checkArgsNoValRequired(DatabaseEntry key,
                                        DatabaseEntry pKey,
                                        DatabaseEntry data) {
        DatabaseUtil.checkForNullDbt(key, "key", false);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
    }
}
