package com.lenovo.vctl.apps.image.upload.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.lenovo.vctl.apps.image.upload.util.StorageProperty;
import com.lenovo.vctl.apps.web.commons.ErrCode;
import com.lenovo.vctl.cloudstorage.core.Store;
import com.lenovo.vctl.cloudstorage.model.RecordObject;
/**
 * Created with IntelliJ IDEA.
 * User: kangyang1
 * Date: 14-4-8
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class VideoUploadController extends BaseController {

    private static final Log logger = LogFactory.getLog(VideoUploadController.class);
    private static boolean needStore2Cloud = false;
    private static FileUpload upload = null;
    private static String sPath = "/data/uploads";
    private static DiskFileItemFactory factory = null;
    private static int sizeThreshold = 1024;
    private static File directory = null;

    private static final Log statusLogger = LogFactory.getLog("scribe"); //BI日志

    @PostConstruct
    public void init() {
        String size = StorageProperty.getInstance().getProperty("PhotoSizeThreshold");// 文件超过多少放在磁盘
        sPath = StorageProperty.getInstance().getProperty("PhotoRepository");// 磁盘文件放在什么地方
        if (sPath != null && sPath.endsWith("/")) {
            sPath = sPath.substring(0, sPath.length() - 1);
        }
        //5m最大的图片
        String fileSizeMax = StorageProperty.getInstance().getProperty("PhotoFileSizeMax"); // 文件最大可以上传读大
        String sizeMax = StorageProperty.getInstance().getProperty("PhotoSizeMax");// 每次请求是多大
        sizeThreshold = NumberUtils.toInt(size, 1024);
        if (StringUtils.isNotEmpty(sPath)) {
            directory = new File(sPath);
            try {
                FileUtils.forceMkdir(directory);
            } catch (IOException e) {
                logger.error("init error ", e);
                directory = null;
            }
        }
        // store repository
        factory = new DiskFileItemFactory();
        factory.setSizeThreshold(sizeThreshold);
        if (directory != null) {
            factory.setRepository(directory);
        }
        upload = new ServletFileUpload(factory);
        upload.setSizeMax(NumberUtils.toLong(sizeMax, -1l));
        upload.setFileSizeMax(NumberUtils.toLong(fileSizeMax, -1));
        logger.info("upload init complete.");
    }

    @RequestMapping(value = "/video/UploadPicture", method = RequestMethod.POST)
    public void doUploadPic(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
        logger.info("\n  Upload Video Picture \n");
        List<DiskFileItem> fileItems = new ArrayList<DiskFileItem>();
        Map<String, String> reqMaps = new HashMap<String, String>();
        // 第一步： 组装所有客户端请求参数
        fillFileItemAndRequestParameter(request, reqMaps, fileItems);
        // 第二步： 验证用户信息的有效性
        //第三步： 检查上传的文件信息是否有效
        DiskFileItem pic = fileItems.get(0);
        if (pic == null || (pic.getSize() <= 0)) {
            model.put(ERROR_CODE, ErrCode.ERROR_UPLOAD_PARAM_ERROR);
            model.put(ERROR_INFO, "prameter error");
            doResponse(PIC, model, response);
            return;
        }
        List<RecordObject> ls = new ArrayList<RecordObject>();
        try {
            ls = Store.getInstance().createFiles(new InputStream[]{pic.getInputStream()}, new String[]{""}, "png", PIC_FTP, 1, 0l, needStore2Cloud);// 保存到云存储使用异步方式
            model.put(PIC, ls.get(0).getFtpUrl());
        } catch (Exception e) {
            e.printStackTrace();
            model.put(ERROR_CODE, ErrCode.ERROR_UPLOAD_UNKNOWEN_ERROR);
            model.put(ERROR_INFO, "server error");
            logger.error("doUploadPic error ", e);
            doResponse(PIC, model, response);
            return;
        }
        doResponse(PIC, model, response);
        return;

    }

    @Override
    public FileUpload getUpload() {
        return upload;
    }

}
