package com.lenovo.vctl.cloudstorage.ftp.pool;


import com.lenovo.vctl.cloudstorage.ftp.FromItem;
import com.lenovo.vctl.cloudstorage.ftp.FtpItem;
import com.lenovo.vctl.cloudstorage.ftp.extend.UploadFtp;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPClientPoolableFactory  implements PoolableObjectFactory<UploadFtp>{
    private static Logger logger = LoggerFactory.getLogger(FTPClientPoolableFactory.class);
    private FromItem fromItem;
    private volatile int index = 0;
    private int pooledObjectNum;


    public FTPClientPoolableFactory(FromItem item){
        this.fromItem = item;
        this.pooledObjectNum = fromItem.getFtpItems() == null? 0: fromItem.getFtpItems().size();
    }

    @Override
    public void activateObject(UploadFtp arg0) throws Exception {
        if(arg0 != null){
            if(!arg0.isConnected())
                arg0.connect();
            arg0.login();
        }


    }

    @Override
    public void destroyObject(UploadFtp arg0) throws Exception {
        if(arg0 != null){
            if(arg0.isConnected())
                arg0.disconnect();
        }
    }

    @Override
    public UploadFtp makeObject() throws Exception {
        return getFtpClient();

    }

    @Override
    public void passivateObject(UploadFtp arg0) throws Exception {
        if(arg0 != null){
            if(logger.isDebugEnabled())
                logger.debug("arg0  passivateObject " + arg0);
            if(arg0.isConnected())
                arg0.disconnect();

        }
    }

    @Override
    public boolean validateObject(UploadFtp arg0) {
        if(arg0 != null && arg0.getFtpItem() != null)return true;
        return false;
    }


    private UploadFtp getFtpClient() {
        if (fromItem != null) {
            if (pooledObjectNum > 0) {
                int index = nextIndex();
                FtpItem ftpItem = fromItem.getFtpItems().get(index);
                UploadFtp ftp = new UploadFtp();
                if(ftpItem != null){
                    ftp.setFtpItem(ftpItem);
                    return ftp;
                }

            }

        }
        return null;

    }

    private int nextIndex(){
        if(index < (pooledObjectNum - 1)) return index++;
        else {
            index = 0;
            return index++;
        }
    }

}
