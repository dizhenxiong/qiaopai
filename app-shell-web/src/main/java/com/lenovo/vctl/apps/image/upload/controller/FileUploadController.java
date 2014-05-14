package com.lenovo.vctl.apps.image.upload.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.lenovo.vctl.apps.image.enums.UploadType;
import com.lenovo.vctl.apps.user.util.DateUtil;
import com.lenovo.vctl.apps.util.PropertiesUtil;
import com.lenovo.vctl.apps.web.commons.ErrCode;
import com.lenovo.vctl.cloudstorage.core.Store;
import com.lenovo.vctl.cloudstorage.model.RecordObject;

@Controller
public class FileUploadController extends BaseController {
	private static Log log = LogFactory.getLog(FileUploadController.class);

	private static String sPath = "/data/fileUploads";
	private static File directory = null;
	
	String fileSizeMax="42106880";
	String sizeMax="805242880";
	private static int sizeThreshold = 1024;
	
	private FileUpload upload = null;
	private DiskFileItemFactory factory = null;
	
	@PostConstruct
	public void init() {
		sPath=PropertiesUtil.getFileProperties("filestorage.properties", "FileRepository");
		if (sPath != null && sPath.endsWith("/"))
			sPath = sPath.substring(0, sPath.length() - 1);
		if (StringUtils.isNotEmpty(sPath)) {
			directory = new File(sPath);
			try {
				FileUtils.forceMkdir(directory);
			} catch (IOException e) {
				log.error("init error ", e);
				directory = null;
			}
		}
		
		factory = new DiskFileItemFactory();
		factory.setSizeThreshold(sizeThreshold);
		if (directory != null) {
			factory.setRepository(directory);
		}
		fileSizeMax =PropertiesUtil.getFileProperties("filestorage.properties", "FileSizeMax"); 
		sizeMax = PropertiesUtil.getFileProperties("filestorage.properties", "SizeMax");
		upload = new ServletFileUpload(factory);
		upload.setSizeMax(NumberUtils.toLong(sizeMax, -1l));
		upload.setFileSizeMax(NumberUtils.toLong(fileSizeMax, -1));
		log.info("upload init complete.");
	}

	@RequestMapping(value = "/file/uploadFile", method = RequestMethod.POST)
	public void uploadFile(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws Exception {
		long start = System.currentTimeMillis(),localend=start,ftpend=start;
		String url = null;
		DiskFileItem file=null;
		try {
			List<DiskFileItem> fileItems = new ArrayList<DiskFileItem>();
			Map<String, String> requestParamenters = new HashMap<String, String>();
			fillFileItemAndRequestParameter(request, requestParamenters, fileItems);
			
			String now = DateUtil.format(new Date(), DateUtil.FORMAT_LONG_STRING_DATE);
			String fileName = now + "-" + UUID.randomUUID().toString();
			File f = new File(sPath + "/" + fileName + ".bak");
			FileOutputStream fos = new FileOutputStream(f);
			file = fileItems.get(0);
			
			InputStream is = file.getInputStream();
			int n=1;
			ByteArrayOutputStream[] baos = new ByteArrayOutputStream[n];
			ByteArrayInputStream[] bais = new ByteArrayInputStream[n];
			try {
				for (int j = 0; j < n; j++) {
					byte[] buff = new byte[100*1024];
					int rc = 0;
					baos[j] = new ByteArrayOutputStream();
					while ((rc = is.read(buff)) > 0) {
						fos.write(buff, 0, rc);
						baos[j].write(buff, 0, rc);
					}
					bais[j] = new ByteArrayInputStream(baos[j].toByteArray());
				}
				// resin本地存一份
				fos.flush();
				localend = System.currentTimeMillis();
				// 存ftp
				String[] fs = new String[] { fileName };
				List<RecordObject> list = Store.getInstance().createFiles(bais, fs, UploadType.File.getSuffix(), UploadType.File.getType(),1, 0, false);// 保存到云存储使用异步方式
				ftpend = System.currentTimeMillis();
				url = list.get(0).getFtpUrl();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				for (int j = 0; j < bais.length; j++) {
					try {
						bais[j].close();
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				if (fos != null)
					fos.close();
			}
			model.put("url", url);
			model.put(CREATEAT, System.currentTimeMillis());
			model.put(ERROR_CODE, ErrCode.NO_ERROR);// 没有错误
		} catch (Exception e) {
			model.put(ERROR_CODE, ErrCode.ERROR_UPLOAD_UNKNOWEN_ERROR);
			model.put(ERROR_INFO, "server error");
			log.error("doUploadAudio error :" + e.getMessage(), e);
		}
		log.info("upload file success file="+file.getName()+",url="+url);
		log.info("upload file time,local="+(localend-start)+",ftp="+(ftpend-localend)+",total="+(ftpend-start)+",size="+file.getSize());
		response.getWriter().write(new JSONObject(model).toString());
	}


    @Override
    public FileUpload getUpload() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void fillFileItemAndRequestParameter(HttpServletRequest req, Map<String, String> requestParamenters, List<DiskFileItem> fileItems)
			throws FileUploadException, UnsupportedEncodingException {
		List /* FileItem */items = upload.parseRequest(new ServletRequestContext(req));
		if (CollectionUtils.isNotEmpty(items)) { // 有文件存在
			for (Iterator iterator = items.iterator(); iterator.hasNext();) {
				DiskFileItem fileItem = (DiskFileItem) iterator.next();
				if (fileItem.isFormField()) {
					String name = fileItem.getFieldName();
					String value = fileItem.getString("UTF-8");
					requestParamenters.put(name, value);
				} else {
					fileItems.add(fileItem);
				}
			}
		}
	}
}
