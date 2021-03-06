/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000,2008 Oracle.  All rights reserved.
 *
 * $Id: StoredSortedEntrySet.java,v 1.27 2008/05/27 15:30:34 mark Exp $
 */

package com.sleepycat.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

/**
 * The SortedSet returned by Map.entrySet().  This class may not be
 * instantiated directly.  Contrary to what is stated by {@link java.util.Map#entrySet}
 * this class does support the {@link #add} and {@link #addAll} methods.
 *
 * <p>The {@link java.util.Map.Entry#setValue} method of the Map.Entry objects
 * that are returned by this class and its iterators behaves just as the {@link
 * StoredIterator#set} method does.</p>
 *
 * <p>In addition to the standard SortedSet methods, this class provides the
 * following methods for stored sorted sets only.  Note that the use of these
 * methods is not compatible with the standard Java collections interface.</p>
 * <ul>
 * <li>{@link #headSet(java.util.Map.Entry, boolean)}</li>
 * <li>{@link #tailSet(java.util.Map.Entry, boolean)}</li>
 * <li>{@link #subSet(java.util.Map.Entry, boolean, java.util.Map.Entry, boolean)}</li>
 * </ul>
 *
 * @author Mark Hayes
 */
public class StoredSortedEntrySet<K,V>
    extends StoredEntrySet<K,V>
    implements SortedSet<Map.Entry<K,V>> {

    StoredSortedEntrySet(DataView mapView) {

        super(mapView);
    }

    /**
     * Returns null since comparators are not supported.  The natural ordering
     * of a stored collection is data byte order, whether the data classes
     * implement the {@link Comparable} interface or not.
     * This method does not conform to the {@link java.util.SortedSet#comparator}
     * interface.
     *
     * @return null.
     */
    public Comparator<? super Map.Entry<K,V>> comparator() {

        return null;
    }

    /**
     * Returns the first (lowest) element currently in this sorted set.
     * This method conforms to the {@link java.util.SortedSet#first} interface.
     *
     * @return the first element.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public Map.Entry<K,V> first() {

        return getFirstOrLast(true);
    }

    /**
     * Returns the last (highest) element currently in this sorted set.
     * This method conforms to the {@link java.util.SortedSet#last} interface.
     *
     * @return the last element.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public Map.Entry<K,V> last() {

        return getFirstOrLast(false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly less than toMapEntry.
     * This method conforms to the {@link java.util.SortedSet#headSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param toMapEntry the upper bound.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public SortedSet<Map.Entry<K,V>> headSet(Map.Entry<K,V> toMapEntry) {

        return subSet(null, false, toMapEntry, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly less than toMapEntry, optionally including toMapEntry.
     * This method does not exist in the standard {@link java.util.SortedSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param toMapEntry is the upper bound.
     *
     * @param toInclusive is true to include toMapEntry.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public SortedSet<Map.Entry<K,V>> headSet(Map.Entry<K,V> toMapEntry,
                                             boolean toInclusive) {

        return subSet(null, false, toMapEntry, toInclusive);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * greater than or equal to fromMapEntry.
     * This method conforms to the {@link java.util.SortedSet#tailSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public SortedSet<Map.Entry<K,V>> tailSet(Map.Entry<K,V> fromMapEntry) {

        return subSet(fromMapEntry, true, null, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly greater than fromMapEntry, optionally including fromMapEntry.
     * This method does not exist in the standard {@link java.util.SortedSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @param fromInclusive is true to include fromMapEntry.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public SortedSet<Map.Entry<K,V>> tailSet(Map.Entry<K,V> fromMapEntry,
                                             boolean fromInclusive) {

        return subSet(fromMapEntry, fromInclusive, null, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements range
     * from fromMapEntry, inclusive, to toMapEntry, exclusive.
     * This method conforms to the {@link java.util.SortedSet#subSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @param toMapEntry is the upper bound.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public SortedSet<Map.Entry<K,V>> subSet(Map.Entry<K,V> fromMapEntry,
                                            Map.Entry<K,V> toMapEntry) {

        return subSet(fromMapEntry, true, toMapEntry, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly greater than fromMapEntry and strictly less than toMapEntry,
     * optionally including fromMapEntry and toMapEntry.
     * This method does not exist in the standard {@link java.util.SortedSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @param fromInclusive is true to include fromMapEntry.
     *
     * @param toMapEntry is the upper bound.
     *
     * @param toInclusive is true to include toMapEntry.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a {@link
     * com.sleepycat.je.DatabaseException} is thrown.
     */
    public SortedSet<Map.Entry<K,V>> subSet(Map.Entry<K,V> fromMapEntry,
                                            boolean fromInclusive,
                                            Map.Entry<K,V> toMapEntry,
                                            boolean toInclusive) {

        Object fromKey = (fromMapEntry != null) ? fromMapEntry.getKey() : null;
        Object toKey = (toMapEntry != null) ? toMapEntry.getKey() : null;
        try {
            return new StoredSortedEntrySet<K,V>(
               view.subView(fromKey, fromInclusive, toKey, toInclusive, null));
        } catch (Exception e) {
            throw StoredContainer.convertException(e);
        }
    }
}
