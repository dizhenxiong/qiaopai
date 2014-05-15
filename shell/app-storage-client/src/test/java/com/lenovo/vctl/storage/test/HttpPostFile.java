package com.lenovo.vctl.storage.test;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Upload image by HTTP POST method
 * 
 * @author LuJian
 */
public class HttpPostFile implements Runnable{

    private static final String TAG = "HttpPostFile";

    private static final int BUFFER_SIZE = 1 * 1024;

//    HttpURLConnection mHttpURLConnection;

    private String mBoundary = "--------httppost123";

    private String mTwoHyphens = "--";

    public static String IMAGE_NAME = "image";

    public static String VIDEO_NAME = "video";

    public static void main(String[] args) {
       	Thread[] t = new Thread[10];
    	long start = System.currentTimeMillis();
    	for(int i=0; i<10; i++){
        	HttpPostFile f = new HttpPostFile();
        	System.out.println("i = " + i);
    		t[i] = new Thread(f);
        	t[i].start();
    	}
    	
    	for(int i=0; i<10; i++){
    		try {
				t[i].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	long end = System.currentTimeMillis();
    	System.out.println(" time used = " + (end - start));
    }
    
    public void run(){
    	int i=100;
    	while((i--) > 0){
    	  	Map<String, String> params = new HashMap<String, String>();
        	params.put("token", "71131906");
        	params.put("type", "0");
        	String fileParams = "d:/pic/123.JPG";
        	String strUrl = "http://dev.ifaceshow.com/1.0/pic/UploadPicture.json";
        	String fileType = "123.jpg";
        	
        	try {
    			doPostFile(params, fileParams, strUrl, fileType);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
  
    }
    
    /**
     * Send file to server by HTTP POST method
     * 
     * @param stringParams
     * @param fileParams
     * @param strUrl
     * @return
     * @throws java.io.IOException
     */
    public void doPostFile(Map<String, String> stringParams, String fileParams,
            String strUrl, String fileType) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
        mHttpURLConnection.setDoOutput(true);
        mHttpURLConnection.setUseCaches(false);
        mHttpURLConnection.setConnectTimeout(100000);
        mHttpURLConnection.setRequestMethod("POST");
//        mHttpURLConnection.setChunkedStreamingMode(2 * BUFFER_SIZE);
        mHttpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="
                + mBoundary);
        try {
            mHttpURLConnection.connect();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }
        DataOutputStream dataOutputStream = new DataOutputStream(
                mHttpURLConnection.getOutputStream());
        this.writeStringParams(stringParams, dataOutputStream);
        this.writeFileParams(fileParams, dataOutputStream, fileType);
        this.paramsEnd(dataOutputStream);
        dataOutputStream.flush();
        dataOutputStream.close();
        InputStream is = mHttpURLConnection.getInputStream();
        int responseCode = mHttpURLConnection.getResponseCode();
        if (responseCode == 200) {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        	String tmp = null;
        	while((tmp = reader.readLine()) != null){
        		System.out.println(tmp);
        	}
        } else {
        	System.out.println("Thread = "+Thread.currentThread().getId()+" error");
        }

    }

    /**
     * Encode request data to UTF-8
     * 
     * @param value
     * @return
     */
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add string parameters to request
     * 
     * @param parameters
     * @param dos
     * @throws java.io.IOException
     */
    private void writeStringParams(Map<String, String> stringParams, DataOutputStream dos)
            throws IOException {
        if (stringParams != null) {
            Set<Entry<String, String>> keySet = stringParams.entrySet();
            for (Entry<String, String> entry : keySet) {
                String name = entry.getKey();
                String value = entry.getValue();
                dos.writeBytes(mTwoHyphens + mBoundary + "\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
                dos.writeBytes("\r\n");
                dos.writeBytes(encode(value) + "\r\n");
            }
        }
    }

    /**
     * Add file parameters to request
     * 
     * @param fileParams: file path in the SD disk
     * @param dos
     * @throws java.io.IOException
     */
    private void writeFileParams(String fileParams, DataOutputStream dos, String fileType)
            throws IOException {
        if (fileParams != null) {
            // Set<Entry<String, byte[]>> keySet = fileParams.entrySet();
            // for (Map.Entry<String, byte[]> entry : keySet) {
            dos.writeBytes(mTwoHyphens + mBoundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + encode(fileType)
                    + "\"; filename=\"" + encode(fileType) + "\"\r\n");
            dos.writeBytes("Content-Type: " + this.getContentType(fileType) + "\r\n");
            dos.writeBytes("\r\n");
            FileInputStream fileStream = new FileInputStream(fileParams);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            while ((length = fileStream.read(buffer)) != -1) {
                dos.write(buffer, 0, length);
                // dos.flush();
            }
            dos.writeBytes("\r\n");

        }
        // }
    }

    /**
     * Get file type, image type is divide in image/png,image/jpg and so on.
     * Un-image is application/octet-stream
     * 
     * @param file
     * @return
     */
    private String getContentType(String fileType) {
        return IMAGE_NAME.equals(fileType) ? "image/png" : "application/octet-stream";
        // return "image/png";
        // return "application/octet-stream";
        // return "image/jpeg";
    }

    /**
     * Convert file to byte array
     * 
     * @param file
     * @return
     * @throws java.io.IOException
     */
//    @SuppressWarnings("unused")
//    private byte[] getBytes(File file) throws IOException {
//        FileInputStream fis = null;
//        ByteArrayOutputStream out = null;
//        try {
//            fis = new FileInputStream(file);
//            out = new ByteArrayOutputStream();
//            byte[] b = new byte[1024];
//            int n;
//            while ((n = fis.read(b)) != -1) {
//                out.write(b, 0, n);
//            }
//        } finally {
//            fis.close();
//        }
//        return out == null ? null : out.toByteArray();
//    }

    /**
     * Add request tail data
     * 
     * @param dos
     * @throws java.io.IOException
     */
    private void paramsEnd(DataOutputStream dos) throws IOException {
        dos.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens + "\r\n");
        dos.writeBytes("\r\n");
    }

}
