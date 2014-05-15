package com.lenovo.vctl.cloudstorage.ftp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lenovo.vctl.cloudstorage.UploadType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.pool.impl.GenericObjectPool;
//import org.apache.log4j.Logger;
//import org.apache.log4j.chainsaw.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lenovo.vctl.cloudstorage.ftp.FromItem;
import com.lenovo.vctl.cloudstorage.ftp.FtpConfig;
import com.lenovo.vctl.cloudstorage.ftp.FtpItem;
import com.lenovo.vctl.cloudstorage.ftp.extend.UploadFtp;
import com.lenovo.vctl.cloudstorage.ftp.validate.Validator;



/**
 *
 * @author songkun1
 *
 */
public class FtpUtil {
    private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);
    private static Map mapDirectory = Collections.synchronizedMap(new LRUMap(100));
    public static final String SPLITER = "_";
    private static final String TMP_FROM = "tmp";
    private static final int INTERVAL = 2;
    private static final int ALIVE_DIR_NUM = 3;

    /**
     * 创建文件目录
     *
     * @param path
     * @param ftp
     * @return
     * @throws IOException
     */
    public static boolean createDir(String path, FTPClient ftp) throws IOException {
        String hostName = ftp.getRemoteAddress().getHostAddress().toString();
        List<String> listPath = (List<String>) mapDirectory.get(hostName);
        if (listPath == null) {
            listPath = new CopyOnWriteArrayList<String>();
            mapDirectory.put(hostName, listPath);
        } else {
            if (listPath.size() > 1024) { // 防止内存泄露
                listPath.clear();
            }
        }

        if (ftp.isConnected()) {
            String[] paths = StringUtils.split(path, "/");
            StringBuffer sbPath = new StringBuffer();
            for (int i = 0; i < paths.length; i++) {
                if (StringUtils.isEmpty(paths[i])) {
                    continue;
                }
                if (!listPath.contains(sbPath.append(paths[i]).append("/").toString())) {
                    logger.info(sbPath.toString());
//                    synchronized(ftp){//防止多线程出现错误
                    if (!ftp.makeDirectory(sbPath.toString())) {
                        logger.error(ftp.getReplyString());
                    }
//                    }

                }
                ((CopyOnWriteArrayList) listPath).addIfAbsent(sbPath.toString());
            }
            logger.info("create " + sbPath.toString() + " complete");
            return true;
        } else {
            logger.error("FtpClient is disconnection ");
        }
        return false;
    }


    public static boolean directCreateDir(String path, FTPClient ftp) throws IOException {
        if (ftp.isConnected()) {
            String[] paths = StringUtils.split(path, "/");
            StringBuffer sbPath = new StringBuffer();
            for (int i = 0; i < paths.length; i++) {
                if (StringUtils.isEmpty(paths[i])) {
                    continue;
                }
                String tmp = sbPath.append(paths[i]).append("/").toString();
                ftp.makeDirectory(tmp);
            }
            logger.info("create " + sbPath.toString() + " complete");
            return true;
        } else {
            logger.error("FtpClient is disconnection ");
        }
        return false;
    }

    //    /**
//     *
//     * @return
//     */
    public static UploadFtp getFtpClient(String from) {
        FromItem fromItem = FtpConfig.getInstance().getFromItem(from);
        FtpItem ftpItem = null;
        if (fromItem != null) {
            int iSize = fromItem.getFtpItems().size();
            if (iSize > 0) {
                int index = RandomUtils.nextInt(iSize);
                ftpItem = fromItem.getFtpItems().get(index);
            }

        }

        return getFtpClient(ftpItem);
    }

    public static UploadFtp getFtpClientFromPool(String from) {
        GenericObjectPool<UploadFtp> ftpPool = FtpConfig.getInstance().getObjectPool(from);
        UploadFtp ftp = null;
        if (ftpPool != null) {
            try {
                ftp = ftpPool.borrowObject();
                while(ftp == null){
                    ftp = ftpPool.borrowObject();
                    if(!ftp.isConnected()){
                        try{
                            ftp.logout();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        try{
                            ftp.disconnect();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                ftp.setFromPool(true);
                return ftp;
            } catch (Exception e) {
                logger.error("get FtpClient from pool error ", e);
                return getFtpClient(from);
            }
        }else{
            return getFtpClient(from);
        }

    }



    public static UploadFtp getTmpFtpClientByUserId(long userId){
        FromItem fromItem = FtpConfig.getInstance().getFromItem(TMP_FROM);
        FtpItem ftpItem = null;
        if (fromItem != null) {
            int iSize = fromItem.getFtpItems().size();
            if (iSize > 0) {
                int index = (((int)userId) & 0xffff) % iSize;
                ftpItem = fromItem.getFtpItems().get(index);
            }

        }
        return getFtpClient(ftpItem);
    }


    //    /**
//     * 从一个FtpItem的配置中创建一个FtpClient
//     *
//     * @param ftpItem
//     * @return
//     */
    private static UploadFtp getFtpClient(FtpItem ftpItem) {
        if (ftpItem == null) {
            return null;
        }
        String hostName = ftpItem.getIp();
        int port = ftpItem.getPort();
        String userName = ftpItem.getUser();
        String passwd = ftpItem.getPasswd();
        UploadFtp ftp = new UploadFtp();
        ((UploadFtp) ftp).setFtpItem(ftpItem);
        try {
            ftp.connect(hostName, port);
            if (ftp.login(userName, passwd)) {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                return ftp;
            } else {
                logger.error("login error: " + userName + " " + ftp.getReplyString());
            }
        } catch (SocketException e) {
            logger.error("socketException: hostName[" + hostName + "] port[" + port + "]", e);

        } catch (IOException e) {
            logger.error("IOException: hostName[" + hostName + "] port[" + port + "]", e);
        }
        return null;
    }
    /**
     *     private String ip;
     private int port;
     private String domain;
     private String status = "run"; // stop
     private String weight = "1";
     private String path;

     private String passwd;
     private String user;
     * @param domain
     * @return
     */
    public static UploadFtp createFtpClient(String ip, String userName, String passwd, int port, String domain, String subPath){
        FtpItem ftpItem = new FtpItem();
        ftpItem.setUser(userName);
        ftpItem.setPasswd(passwd);
        ftpItem.setPort(port);
        ftpItem.setIp(ip);
        ftpItem.setBaseDir(subPath);
        ftpItem.setBaseUrl(domain);
        UploadFtp client = getFtpClient(ftpItem);
        return client;
    }

    public static String getDomain(UploadFtp ftp) {
        if (ftp != null) {
            UploadFtp uploadFtp = (UploadFtp) ftp;
            return uploadFtp.getFtpItem().getDomain();
        } else {
            return null;
        }
    }

    /**
     * 给出Path of Domain store
     *
     * @param ftp
     * @return
     */
    public static String getDomainPath(UploadFtp ftp) {
        if (ftp != null) {
            UploadFtp uploadFtp = (UploadFtp) ftp;
            return uploadFtp.getFtpItem().getPath();
        } else {
            return "";
        }
    }

    public static String getDomainPath(String from){
        FromItem fromItem = FtpConfig.getInstance().getFromItem(from);
        FtpItem ftpItem = null;
        if (fromItem != null) {
            ftpItem = fromItem.getFtpItems().get(0);
            if(ftpItem != null)return ftpItem.getPath();
        }
        return "";

    }


    /**
     * 指定后缀
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String[] createFile(InputStream[] is, String from,  String[] subNames, String suffixName)
            throws Exception {
        if (ArrayUtils.isEmpty(is) || ArrayUtils.isEmpty(subNames)) {
            return null;
        }
        String defaultSuffixName = getDefaultSuffixName(from);
        UploadFtp ftp = FtpUtil.getFtpClientFromPool(from);
        String[] lResult = new String[is.length];
        try {
            String alias = FtpUtil.getAlias(from);
            if (ftp != null) {
                String filePath = FileUtil.getFilePath(from);
                if (StringUtils.isNotEmpty(alias)) {
                    filePath = alias.toLowerCase() + "/" + filePath;
                }
                String fileName = FileUtil.getFileName(filePath);
                String domainPath = getDomainPath(ftp);
                if (StringUtils.isNotEmpty(domainPath)) {
                    domainPath = StringUtils.removeStart(domainPath, "/");
                    if (!domainPath.endsWith("/")) {
                        domainPath = domainPath + "/";
                    }
                }
                if (FtpUtil.createDir(domainPath + filePath, ftp)) {
                    for (int i = 0; i < is.length; i++) {
                        if (is[i] == null) {
                            continue;
                        }
                        try {
                            String index = ObjectUtils.toString(subNames[i], Integer.toString(i));
                            String fileStoreName = new StringBuilder().append(fileName).append("_").append(index).append(
                                    ".").append(StringUtils.defaultIfEmpty(suffixName, defaultSuffixName)).toString();
                            if (!ftp.storeFile(domainPath + filePath + "/" + fileStoreName, is[i])) {
                                logger.error(ftp.getReplyString());
                                lResult[i] = null;
                            } else {
                                lResult[i] = new StringBuilder().append("http://").append(FtpUtil.getDomain(ftp)).append(
                                        "/").append(filePath).append("/").append(fileStoreName).toString();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                            logger.error("");
                        }

                    }
                }
            } else {
                throw new IOException("don't connected store server: store server is null");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            closeFtp(from, ftp);
        }
        return lResult;
    }


    private static String getDefaultSuffixName(String from) {
        String defaultSuffixName = null;
        for(UploadType type : UploadType.values()){
            if(type.getType().equalsIgnoreCase(from)){
                defaultSuffixName = type.getType();
                break;
            }
        }
        return defaultSuffixName;
    }



    /**
     * 指定后缀
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String[] createFile(InputStream[] is, String from, String[] subNames, String[] suffixNames) throws Exception {
        if (ArrayUtils.isEmpty(is) || ArrayUtils.isEmpty(subNames) || ArrayUtils.isEmpty(suffixNames)) {
            return null;
        }
        UploadFtp ftp = FtpUtil.getFtpClientFromPool(from);
        String alias = FtpUtil.getAlias(from);
        String lResult[] = new String[is.length];
        String defaultSuffixName = "pic".equals(from)? "jpg":"flv";
        try {
            if (ftp != null) {
                String filePath = FileUtil.getFilePath(from);
                if (StringUtils.isNotEmpty(alias)) {
                    filePath = alias.toLowerCase() + "/" + filePath;
                }
                String fileName = FileUtil.getFileName(filePath);
                String domainPath = getDomainPath(ftp);
                if (StringUtils.isNotEmpty(domainPath)) {
                    domainPath = StringUtils.removeStart(domainPath, "/");
                    if (!domainPath.endsWith("/")) {
                        domainPath = domainPath + "/";
                    }
                }
                if (FtpUtil.createDir(domainPath + filePath, ftp)) {
                    for (int i = 0; i < is.length; i++) {
                        if (is[i] == null) {
                            continue;
                        }
                        try {
                            String index = ObjectUtils.toString(subNames[i], Integer.toString(i));
                            StringBuilder fileStoreName = new StringBuilder().append(fileName).append("_").append(index).append(".").append(StringUtils.defaultIfEmpty(suffixNames[i], defaultSuffixName));
                            if (!ftp.storeFile(domainPath + filePath + "/" + fileStoreName.toString(), is[i])) {
                                logger.error(ftp.getReplyString());
                            } else {
                                lResult[i] = new StringBuilder().append("http://").append(FtpUtil.getDomain(ftp)).append(
                                        "/").append(filePath).append("/").append(fileStoreName.toString()).toString();
                            }
                        } catch (IOException ex) {
//                        ex.printStackTrace(System.err);
                            logger.error("createFile error", ex);
                        }
                    }
                }
            } else {
                throw new IOException("don't connected store server: store server is null");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            closeFtp(from, ftp);
        }
        return lResult;
    }


    public static String[] storeFile(String srcDir, InputStream[] is, String from, String defaultName, String[] subNames, String suffixName) throws Exception {
        if (srcDir == null || srcDir.isEmpty() || ArrayUtils.isEmpty(is) || ArrayUtils.isEmpty(subNames)) {
            return null;
        }

        if(srcDir.lastIndexOf("/") == (srcDir.length()-1)) srcDir = srcDir.substring(0, srcDir.length()-1);
        FtpItem item = findFtpItem(srcDir);
        if(item == null) throw new Exception("ftp is null");
        String baseUrl = item.getBaseUrl();
        int iIndex = srcDir.indexOf(baseUrl);
        String filePath = item.getBaseDir() + srcDir.substring(iIndex + baseUrl.length());
        filePath = StringUtils.removeStart(filePath, "/");
        UploadFtp ftp = getFtpClient(item);
        String lResult[] = new String[is.length];
        String defaultSuffixName = "pic".equals(from)? "jpg":"flv";
        try {
            if (ftp != null) {
                if (FtpUtil.createDir(filePath, ftp)) {
                    for (int i = 0; i < is.length; i++) {
                        if (is[i] == null) {
                            continue;
                        }

                        try {
                            String index = ObjectUtils.toString(subNames[i], Integer.toString(i));
                            StringBuilder fileStoreName = new StringBuilder().append(defaultName).append("_").append(index).append(".").append(StringUtils.defaultIfEmpty(suffixName, defaultSuffixName));
                            if (!ftp.storeFile(filePath + "/" + fileStoreName.toString(), is[i])) {
                                logger.error(ftp.getReplyString());
                            } else {
                                lResult[i] = new StringBuilder().append(srcDir).append("/").append(fileStoreName.toString()).toString();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                            logger.error("");
                        }

                    }
                }
            } else {
                throw new IOException("don't connected store server: store server is null");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            closeFtp(from, ftp);
        }
        return lResult;
    }

    /**
     *  new file name will be defaultName_subName.suffixName
     *  like <p>1abc37deab128321_b.jpg<p>
     * @param srcDir
     * @param is
     * @param from
     * @param defaultName
     * @param subNames
     * @param suffixNames
     * @return
     * @throws IOException
     */
    public static String[] storeFile(String srcDir, InputStream[] is, String from, String defaultName, String[] subNames, String[] suffixNames) throws Exception {
        if (srcDir == null || srcDir.isEmpty() || ArrayUtils.isEmpty(is) || ArrayUtils.isEmpty(subNames) || ArrayUtils.isEmpty(suffixNames)) {
            return null;
        }
        if(srcDir.lastIndexOf("/") == (srcDir.length()-1)) srcDir = srcDir.substring(0, srcDir.length()-1);
        FtpItem item = findFtpItem(srcDir);
        if(item == null) throw new Exception("ftp is null");
        String baseUrl = item.getBaseUrl();
        int iIndex = srcDir.indexOf(baseUrl);
        String filePath = item.getBaseDir() + srcDir.substring(iIndex + baseUrl.length());
        filePath = StringUtils.removeStart(filePath, "/");
        UploadFtp ftp = getFtpClient(item);
        String lResult[] = new String[is.length];
        String defaultSuffixName = "pic".equals(from)? "jpg":"flv";
        try {
            if (ftp != null) {
                if (FtpUtil.createDir(filePath, ftp)) {
                    for (int i = 0; i < is.length; i++) {
                        if (is[i] == null) {
                            continue;
                        }
                        try {
                            String index = ObjectUtils.toString(subNames[i], Integer.toString(i));
                            StringBuilder fileStoreName = new StringBuilder().append(defaultName).append("_").append(index).append(".").append(StringUtils.defaultIfEmpty(suffixNames[i], defaultSuffixName));
                            if (!ftp.storeFile(filePath + "/" + fileStoreName.toString(), is[i])) {
                                logger.error(ftp.getReplyString());
                            } else {
                                lResult[i] = new StringBuilder().append(srcDir).append("/").append(fileStoreName.toString()).toString();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                            logger.error("");
                        }

                    }
                }

            } else {
                throw new IOException("don't connected store server: store server is null");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            closeFtp(from, ftp);
        }
        return lResult;
    }


    public static String storeFile(String srcDir, InputStream is, String from, String defaultName) throws Exception {
        if (srcDir == null || srcDir.isEmpty() || (is==null)) {
            return null;
        }
        UploadFtp ftp = FtpUtil.getFtpClientFromPool(from);
        if(srcDir.lastIndexOf("/") == (srcDir.length()-1)) srcDir = srcDir.substring(0, srcDir.length()-1);
        FtpItem item = findFtpItem(srcDir);
        if(item == null) throw new Exception("ftp is null");
        String baseUrl = item.getBaseUrl();
        int iIndex = srcDir.indexOf(baseUrl);
        String filePath = item.getBaseDir() + srcDir.substring(iIndex + baseUrl.length());
        filePath = StringUtils.removeStart(filePath, "/");
        String lResult = null;
        try {
            if (ftp != null) {
                if (FtpUtil.createDir(filePath, ftp)) {
                    try {
                        StringBuilder fileStoreName = new StringBuilder().append(defaultName);
                        if (!ftp.storeFile(filePath + "/" + fileStoreName.toString(), is)) {
                            logger.error(ftp.getReplyString());
                        } else {
                            lResult = new StringBuilder().append(srcDir).append("/").append(fileStoreName.toString()).toString();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                        logger.error("");
                    }

//                }
                }
            } else {
                throw new IOException("don't connected store server: store server is null");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            closeFtp(from, ftp);
        }
        return lResult;
    }

    /**
     * 指定后缀
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String[] createFileHouse(InputStream[] is, String from, String suffix) throws Exception {
        UploadFtp ftp = FtpUtil.getFtpClientFromPool(from);
        String alias = FtpUtil.getAlias(from);
        String lResult[] = new String[is.length];
        try {
            if (ftp != null) {
                String filePath = alias.toLowerCase();
//            if (StringUtils.isNotEmpty(alias)) {
//                filePath = alias.toLowerCase() + "/" + filePath;
//            }
                String fileName = FileUtil.getFileName(alias);
                String domainPath = getDomainPath(ftp);
                if (StringUtils.isNotEmpty(domainPath)) {
                    domainPath = StringUtils.removeStart(domainPath, "/");
                    if (!domainPath.endsWith("/")) {
                        domainPath = domainPath + "/";
                    }
                }

                if (FtpUtil.createDir(domainPath + filePath, ftp)) {
                    for (int i = 0; i < is.length; i++) {
                        if (is[i] == null) {
                            continue;
                        }
                        try {
                            String fileStoreName = new StringBuilder().append(fileName).append("_").append(i).append(".")
                                    .append(StringUtils.defaultIfEmpty(suffix, "jpg")).toString();
                            if (!ftp.storeFile(domainPath + filePath + "/" + fileStoreName, is[i])) {
                                logger.error(ftp.getReplyString());
                            } else {
                                lResult[i] = new StringBuilder().append("http://").append(FtpUtil.getDomain(ftp)).append(
                                        "/").append(filePath).append("/").append(fileStoreName).toString();
                            }
                        } catch (IOException ex) {
//                        ex.printStackTrace(System.err);
                            logger.error("createFileHouse error ",ex);
                        }

                    }
                }
            } else {
                throw new IOException("don't connected store server: store server is null");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            closeFtp(from, ftp);
        }
        return lResult;
    }




    /**
     * 指定后缀
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String createTempFile(InputStream is, long userId, String MD5, int totalPiece, int currentPiece, String suffix) throws Exception {
        Validator.validateStringIsNotEmpty("FtpUtil", "createTempFileHouse", "userId", userId+"");
        Validator.validateStringIsNotEmpty("FtpUtil", "createTempFileHouse", "MD5", MD5);
        UploadFtp ftp = getTmpFtpClientByUserId(userId);
//        String alias = FtpUtil.getAlias(TMP_FROM);
        String lResult = null;
        try {
            if (ftp != null) {
                String domainPath = getDomainPath(ftp);
                if (StringUtils.isNotEmpty(domainPath)) {
                    domainPath = StringUtils.removeStart(domainPath, "/");
                    if (!domainPath.endsWith("/")) {
                        domainPath = domainPath + "/";
                    }
                }
                int subPath = getSubPathBytime(domainPath, MD5, userId, ftp);
                String filePath =  subPath + "/" + MD5 + SPLITER + userId;
                String fileName = totalPiece + "_" + currentPiece;
                if (FtpUtil.directCreateDir(domainPath + filePath, ftp)) {
//            	  for (int i = 0; i < is.length; i++) {
                    if (is == null) {
//		                    continue;
                        return null;
                    }
                    try {
                        String fileStoreName = new StringBuilder().append(fileName).append("."+StringUtils.defaultIfEmpty(suffix, "")).toString();
                        if (!ftp.storeFile(domainPath + filePath + "/" + fileStoreName, is)) {
                            logger.error(ftp.getReplyString());
                        } else {
                            lResult = new StringBuilder().append("http://").append(FtpUtil.getDomain(ftp)).append(
                                    "/").append(filePath).append("/").append(fileStoreName).toString();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        logger.error("error = " + ex.getCause());
                    }
//            	  }
                }
            } else {
                throw new IOException("don't connected store server: store server is null");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            closeFtp(TMP_FROM, ftp);
        }
        return lResult;
    }

    public static boolean clearTmpDir() throws Exception{
        FromItem fromItem = FtpConfig.getInstance().getFromItem(TMP_FROM);
        boolean flag = true;
        FtpItem ftp = null;
        if (fromItem != null) {
            int iSize = fromItem.getFtpItems().size();
            if (iSize > 0) {
                int num = getPathNum(new Date().getHours());
                for(int i=0; i<iSize; i++){
                    ftp = fromItem.getFtpItems().get(i);
                    if (ftp != null) {
                        UploadFtp client = getFtpClient(ftp);
                        try {
                            if(client != null){
//                        	System.out.println("client = " + client);
                                String baseDir = ftp.getBaseDir() ;
                                baseDir = StringUtils.removeStart(baseDir, "/");
                                int [] clearDir = getNeedClearDir(num);
                                for(int j=0; j<clearDir.length; j++){
                                    flag = removeDirOrFile(client, baseDir +"/"+ clearDir[j] +"/") && flag;
                                }
                            }
                        } catch (Exception e) {
                            throw e;
                        }finally{
                            closeFtp(TMP_FROM, client);
                        }

                    }

                }

            }

        }
        return flag;
    }

    private static boolean removeDirOrFile(FTPClient client, String dir){
        if(dir == null) return true;
        if(!dir.endsWith("/"))dir = dir+"/";
        FTPFile[] files = null;
        try {
            files = client.listFiles(dir);
        } catch (Exception e) {
            return false;
        }
        if(files == null)return true;
        boolean flag = true;
        for(FTPFile file : files){
            if(file.isDirectory()){
                if(file.getName().equals(".") || file.getName().equals("..")) {
                    continue;
                }
                try {
                    if(client.listFiles(dir+"/"+file.getName()) != null && (client.listFiles(dir+"/"+file.getName())).length > 0){
                        flag = removeDirOrFile(client, dir + file.getName()) && flag;
                        flag = client.removeDirectory(dir + file.getName()) && flag;
                    }else{
                        flag = client.removeDirectory(dir + file.getName()) && flag;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    flag = false;
                }
            }else{
                try {

                    flag = client.deleteFile(dir + file.getName()) && flag;
                } catch (Exception e) {
                    e.printStackTrace();
                    flag = false;
                }
            }
        }
        return flag;

    }

    /**
     * 删除文件
     *
     * @param httpUrlAddress
     * @return
     */
    public static boolean removeFile(String httpUrlAddress) {
        if (StringUtils.isNotBlank(httpUrlAddress)) {
            FtpItem storeFtpItem = findFtpItem(httpUrlAddress);
            if (storeFtpItem != null) {
                logger.info("ip: " + storeFtpItem.getIp());
                String baseUrl = storeFtpItem.getBaseUrl();
                int iIndex = httpUrlAddress.indexOf(baseUrl);
                String fileName = storeFtpItem.getBaseDir() + httpUrlAddress.substring(iIndex + baseUrl.length());
                fileName = StringUtils.removeStart(fileName, "/");
                logger.info("path: " + fileName);
                UploadFtp client = getFtpClient(storeFtpItem);
                if (client != null) {
                    try {
                        if (!client.deleteFile(fileName)) {
                            logger.error(client.getReplyString());
                        } else {
                            logger.info("remove " + fileName + " succeed." );
                        }
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    } finally {
                        closeFtp(null, client);
                    }
                }
            } else {
                logger.error("don't find ftpItem [" + StringUtils.defaultIfEmpty(httpUrlAddress, "") + "]");
            }
        }
        return true;
    }



    /**
     * 删除文件
     *
     * @param httpUrlAddress
     * @return
     */
    public static boolean getFtpFile(String httpUrlAddress, OutputStream os) {
        if (StringUtils.isNotBlank(httpUrlAddress)) {
            FtpItem storeFtpItem = findFtpItem(httpUrlAddress);
            if (storeFtpItem != null) {
                logger.info("ip: " + storeFtpItem.getIp());
                String baseUrl = storeFtpItem.getBaseUrl();
                int iIndex = httpUrlAddress.indexOf(baseUrl);
                String fileName = storeFtpItem.getBaseDir() + httpUrlAddress.substring(iIndex + baseUrl.length());
                fileName = StringUtils.removeStart(fileName, "/");
                logger.info("path: " + fileName);
                UploadFtp client = getFtpClient(storeFtpItem);
                if (client != null) {
                    try {
                        if (!client.retrieveFile(fileName, os)) {
                            logger.error(client.getReplyString());
                            return false;
                        } else {
                            logger.info("retrieve " + fileName + " succeed." );
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    } finally {
                        closeFtp(null, client);
                    }
                }
            } else {
                logger.error("don't find ftpItem [" + StringUtils.defaultIfEmpty(httpUrlAddress, "") + "]");
            }
        }
        return true;
    }

    /**
     * find ftpItem by httpUrlAddress
     * @param httpUrlAddress
     * @return
     */

    private static FtpItem findFtpItem(String httpUrlAddress) {
        // find from default fromItem
        FromItem fromItem = FtpConfig.getInstance().getDefaultFromItem();
        if (fromItem != null) {
            List<FtpItem> ftpItemList = fromItem.getFtpItems();
            if (CollectionUtils.isNotEmpty(ftpItemList)) {
                for (FtpItem ftpItem : ftpItemList) {
                    if (ftpItem != null) {
                        String baseUrl = ftpItem.getBaseUrl();
                        int iIndex = httpUrlAddress.indexOf(baseUrl);
                        if (iIndex >= 0 && iIndex < 8) {
                            return ftpItem;
                        }
                    }
                }
            }
        }

        // find from other fromitem;
        Map<String, FromItem> map = FtpConfig.getInstance().getFromItemMap();
//        Set<String> keySet = map.keySet();
        Set<Entry<String, FromItem>> set = map.entrySet();
        for (Entry<String, FromItem> entry : set) {
//            fromItem = map.get(fromName);
//        	String fromName = entry.getKey();
            fromItem = entry.getValue();
            if (fromItem != null) {
                List<FtpItem> ftpItemList = fromItem.getFtpItems();
                if (CollectionUtils.isNotEmpty(ftpItemList)) {
                    for (FtpItem ftpItem : ftpItemList) {
                        if (ftpItem != null) {
                            String baseUrl = ftpItem.getBaseUrl();
                            int iIndex = httpUrlAddress.indexOf(baseUrl);
                            if (iIndex >= 0 && iIndex < 8) {
                                return ftpItem;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 清除
     */
    public static void clear() {
        mapDirectory.clear();
    }

    public static String getAlias(String from) {
        return FtpConfig.getInstance().getAlias(from);
    }
    public static String getSubPath(String from){
        return FileUtil.getFilePath(from);
    }



    public static String[] listFileNamesByDir(String httpUrlAddress){
        if (StringUtils.isNotBlank(httpUrlAddress)) {
            FtpItem storeFtpItem = findFtpItem(httpUrlAddress);
            if (storeFtpItem != null) {
                logger.info("ip: " + storeFtpItem.getIp());
                String baseUrl = storeFtpItem.getBaseUrl();
                int iIndex = httpUrlAddress.indexOf(baseUrl);
                String fileName = storeFtpItem.getBaseDir() + httpUrlAddress.substring(iIndex + baseUrl.length());
                fileName = StringUtils.removeStart(fileName, "/");
                logger.info("path: " + fileName);
                FTPClient client = getFtpClient(storeFtpItem);
                if (client != null) {
                    try {
                        String[] names = client.listNames(fileName);
                        return names;
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }
        return null;
    }

    private static int getSubPathBytime(String domainPath, String MD5, long userId, FTPClient ftp){
        int time = new Date().getHours();
        int num = getPathNum(time);
        String[] currentDirs = null;
        String[] oldDirs = null;
        try {
            currentDirs = ftp.listNames(domainPath + num);
            oldDirs = ftp.listNames(domainPath + ((num == 0)? 11:(num-1)));
        } catch (Exception e) {
            logger.error("getSubPathBytime  " ,e);
        }
        if(currentDirs != null && currentDirs.length > 0){
            for(int i=0; i<currentDirs.length; i++){
                if(currentDirs[i].indexOf(MD5+"_"+ userId) > 0){
                    return num;
                }
            }
        }
        if(oldDirs != null && oldDirs.length > 0){
            for(int i=0; i<oldDirs.length; i++){
                if(oldDirs[i].indexOf(MD5+"_"+ userId) > 0){
                    return ((num == 0)?11:(num-1));
                }
            }
        }
        return num;
    }

    private static int getPathNum(int hour){
        int total = 24 / INTERVAL;
        int tmp = 0;
        for(int i=0; i<total; i++){
            if((hour >= (i*INTERVAL)) && (hour < ((i+1)*INTERVAL))){
                tmp = i;
                break;
            }
        }
//    	if(tmp == 0){
//    		return (total - 1);
//    	}else{
//    		return (tmp - 1);
//    	}
        return tmp;
    }

    public static void main(String[] args) {
//    	System.out.println(FtpUtil.getPathNum(19));

        //storeFile(String srcDir, InputStream[] is, String from, String defaultName, String[] subNames, String suffixName)

        try {
            System.out.println((FtpUtil.storeFile("http://v2.ifaceshow.com/a/2012/1102/0955/", new InputStream[] { new FileInputStream("d:/pic/123.jpg")}, "video", "a64cbbe_13b377221fe51",new String[]{"0"}, "jpg"))[0]);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private static int[] getNeedClearDir(int num){
        int total = 24 / INTERVAL;
        int[] dirs = new int[total - ALIVE_DIR_NUM];
        int j = 0;
        for(int i=0; i<total; i++){
            if(num < 0){
                num = total -1;
            }
            if(i >= (ALIVE_DIR_NUM)){
                dirs[j++] = num;
            }
            num = num -1;

        }
        return dirs;
    }



    public static InputStream retrieveFileStream(String httpUrlAddress){
        if (StringUtils.isNotBlank(httpUrlAddress)) {
            FtpItem storeFtpItem = findFtpItem(httpUrlAddress);
            if (storeFtpItem != null) {
                logger.info("ip: " + storeFtpItem.getIp());
                String baseUrl = storeFtpItem.getBaseUrl();
                int iIndex = httpUrlAddress.indexOf(baseUrl);
                String fileName = storeFtpItem.getBaseDir() + httpUrlAddress.substring(iIndex + baseUrl.length());
                fileName = StringUtils.removeStart(fileName, "/");
                logger.info("path: " + fileName);
                FTPClient client = getFtpClient(storeFtpItem);
                if (client != null) {
                    try {
                        return client.retrieveFileStream(fileName);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
//						e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }


    public static void closeFtp(String from, UploadFtp client){
        if(client != null && client.isFromPool()){
            GenericObjectPool<UploadFtp> ftpPool = FtpConfig.getInstance().getObjectPool(from);
            if(ftpPool != null){
                try {
                    ftpPool.returnObject(client);
                    return ;
                } catch (Exception e) {
                    logger.error("return ftp to pool error " , e);
                }
            }
        }

        if(client != null){
            try {
                client.logout();
            } catch (Exception e) {
            }
            try {
                client.disconnect();
            } catch (Exception e) {
            }
        }
    }
}
