package com.lenovo.vctl.cloudstorage;

/**
 * Created with IntelliJ IDEA.
 * User: kangyang1
 * Date: 14-5-15
 * Time: 上午11:39
 * To change this template use File | Settings | File Templates.
 */

public class Constants {


    public static final String CLOUDSTORAGE_CREATE_OBJECT_URL = "https://iocos.lenovows.com/object/create";
    public static final String CLOUDSTORAGE_LINK_OBJECT_URL = "https://iocos.lenovows.com/object/link";
    public static final String CLOUDSTORAGE_REMOVE_OBJECT_URL = "https://iocos.lenovows.com/object/remove";
    public static final String CLOUDSTORAGE_COMMIT_OBJECT_URL = "https://iocos.lenovows.com/object/commit";
    public static final String CLOUDSTORAGE_WRITE_OBJECT_URL = "{0}/ldtv1/object/write/{1}/{2}";

    public static final String CLOUDSTORAGE_SUCCESS_CODE = "20000";

    public static final String CLOUDSTORAGE_API_KEY = "test";
    public static final String CLOUDSTORAGE_USER_KEY = "1313560910041203809";

    public static final String DEFAULT_OPTION = "0";

    // 5000 year is a year far away enough
    public static final String EXPIRATIONDATE = "5000-01-01 00:00:00";




    //dao related

    public static final String CloudStorage_Map_Url = "CloudStorage_Map_Url";

    public final static String STORAGE_MASTER_TYPE = "storage.master.type";
    //	public final static String SLAVE_STORE_NEED  = "storage.slave.store.need";
    public final static String CLOUD_STORE_NEED = "storage.cloud.store.need";
    public final static String CLOUD_STORE_NEED_TRUE = "true";

    public final static String STORAGE_MASTER_TYPE_FTP = "ftp";
    public final static String STORAGE_MASTER_TYPE_CLOUD = "cloud";
    public final static String SLAVE_STORE_NEED_TRUE = "true";



    ///  v2
    public static final String CLOUDSTORAGE_DOMAIN = "cos.lenovows.com";
    public static final String FTPSTORAGE_DOMAIN = "ifaceshow.com";
    public static final String CLOUDSTORAGE_UPDATE_OBJECT_URL = "http://cos.lenovows.com/v1/object/003f21c3ad7b3c01:video_call/{0}";//"http://10.100.1.105/object/sk4/{0}";
    public static final String CLOUD_URL_DOMAIN = "cos.lenovows.com/v1/object/003f21c3ad7b3c01:video_call/";
    public static final String FTP_URL_TEMPLATE = "http://{0}.ifaceshow.com/{1}";

    public static final String CLOUD_URL = "http://cos.lenovows.com/v1/object/003f21c3ad7b3c01:video_call/";

}
