package com.lenovo.vctl.apps.image.listener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.lenovo.vctl.apps.commons.utils.GsonUtil;
import com.lenovo.vctl.apps.image.process.ImageUtil;
import com.lenovo.vctl.apps.image.transform.TransformPic;
import com.lenovo.vctl.cloudstorage.core.Store;
import com.lenovo.vctl.redis.client.util.RedisUtil;

public class Listener extends ContextLoaderListener implements ServletContextListener{
	
	private static Logger doTransfromQueueLog = Logger.getLogger("DoTransformQueueLog");
	private static Logger logger = Logger.getLogger(Listener.class);
	private static final String PICREGION = "PicRegion";
	private static final ImageUtil.TypeImages[] imageTypes = ImageUtil.TypeImages.values();
	private static final String[] subNames = new String[imageTypes.length];
	private static final String IMAGETYPE = "jpg";
	public static final String PIC_FTP = "pic";
	
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 20, 3000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	@Override
	public void contextInitialized(ServletContextEvent sce){
		WebApplicationContext servletContext =  WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		if(logger.isDebugEnabled())
			logger.debug("init Listener!");
		//初始化 需要转换的类型
		for(int i=0; i<subNames.length; i++){
			subNames[i] = imageTypes[i].getValue();
		}
		//初始化一个进程从queue中读取
		Producer p = new Producer();
		new Thread(p).start();
		if(logger.isDebugEnabled())
			logger.debug("producer is start!");
		// 刚开始启动应该load一遍上次redis没有处理完的数据
		Set<String> picStrings = RedisUtil.sMembers(PICREGION, PIC_FTP);
		if(picStrings != null && picStrings.size() > 0){
			for(String picString : picStrings){
				if(picString != null){
					TransformPic pic = GsonUtil.fromJson(picString, TransformPic.class);
					if(pic != null){
						Worker worker = new Worker(pic);
						executor.execute(worker);
					}
				}
			}
		}


	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		executor.shutdown();
	}
	
	private static class Producer implements Runnable{
		public TransformPic produce(){
			return null;
		}

		@Override
		public void run() {
			while(true){
				TransformPic pic = produce();
				if(pic == null){
					try{
						Thread.currentThread().sleep(1000);
					}catch(Exception e){
					}
				}else{
					Worker worker = new Worker(pic);
					executor.submit(worker);
				}
			}
		
		}
	}
	
	private static String[] getUrls(String src){
		//http://p3.ifaceshow.com/a/2013/0423/1530/a7354062_1298dce6a8345_src.jpg
		if(src == null)return null;
		String[] urls = new String[imageTypes.length];
		String prefixName = src.substring(src.lastIndexOf("/")+1);
		String dir = src.replace(prefixName, "");
		prefixName = prefixName.substring(0, prefixName.lastIndexOf("_"));
		for(int i=0 ;i< urls.length; i++){
			urls[i] = dir + prefixName + "_" + subNames[i] + ".jpg";
		}
		return urls;
	}
	static class Worker implements Runnable{
		private TransformPic pic;
		public Worker(TransformPic pic){
			this.pic = pic;
		}
		@Override
		public void run() {
			if(pic != null && pic.getUserId() > 0 && pic.getSrcUrl() != null){
				String picJson = GsonUtil.toJson(pic);
				File f = new File(pic.getPic());
				if(f != null){
					RedisUtil.sAdd(PICREGION, PIC_FTP, picJson);
					try {
						FileInputStream fis = new FileInputStream(f);
						BufferedInputStream bis= new BufferedInputStream(fis);
						bis.mark(Integer.MAX_VALUE);
						ByteArrayOutputStream[] baos = new ByteArrayOutputStream[imageTypes.length];
						ByteArrayInputStream[] bais = new ByteArrayInputStream[imageTypes.length];
						for(int j=0; j<imageTypes.length; j++){
							bis.reset();
							baos[j] = new ByteArrayOutputStream();
							ImageUtil.getInstance().writeImage(bis, baos[j], 0, imageTypes[j].getType(), IMAGETYPE);
							bais[j] = new ByteArrayInputStream(baos[j].toByteArray());
						}
						int i = 3;
						boolean flag = false;
						while(!flag && (i-- > 0)){//重试三次
//							List<RecordObject> list = Store.getInstance().storeFiles(pic.getSrcUrl(), bais, subNames, IMAGETYPE, PIC_FTP, 0, 0, true);
							String[] urls = getUrls(pic.getSrcUrl());		
							try {
								String[] list = Store.getInstance().storeToCloud(bais, urls , 0L);
								if(list != null && list.length > 0){
									flag = true;
									f.delete();//正确就删除这个文件
//									boolean state = false;
//									if(vctlUserService != null){
//										int j = 3;
//										while(!state && (j-- > 0)){//重试三次
//											try {
//												state = vctlUserService.uploadPicture(pic.getUserId(), pic.getSrcUrl());
//											} catch (Exception e) {
//												state = false;
//											}
//										}
//									}else{
//										doTransfromQueueLog.error("userService is null ");
//									}
//									if(!state){
//										doTransfromQueueLog.error("update user icon error " + pic.toString());
//									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("store 2 cloud error ", e);
							}


						}
						if(!flag){
							doTransfromQueueLog.error("transform pic error f = " + picJson);
						}
						
					}catch(Exception e){
						e.printStackTrace();
						doTransfromQueueLog.error("transform pic error f = " + picJson);
					}finally{
						RedisUtil.sRem(PICREGION, PIC_FTP, picJson);
					}
				}else{
					doTransfromQueueLog.error("File is Null f = " + picJson);
				}
				
			}
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println(Listener.getUrls("http://p3.ifaceshow.com/a/2013/0423/1530/a7354062_1298dce6a8345_src.jpg")[0]);
	}
	 
}


