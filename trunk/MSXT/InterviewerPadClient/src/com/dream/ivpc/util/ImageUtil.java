package com.dream.ivpc.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;

public class ImageUtil {
	
    /**  
     * Get image from newwork  
     * @param path The path of image  
     * @return byte[]
     * @throws Exception  
     */  
    public static byte[] getImage(String path) throws Exception{   
        URL url = new URL(path);   
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
        conn.setConnectTimeout(5 * 1000);   
        conn.setRequestMethod("GET");   
        InputStream inStream = conn.getInputStream();   
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){   
            return readStream(inStream);   
        }   
        return null;   
    }   
  
    /**  
     * Get image from newwork  
     * @param path The path of image  
     * @return InputStream
     * @throws Exception  
     */
    public static InputStream getImageStream(String path) throws Exception{   
        URL url = new URL(path);   
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
        conn.setConnectTimeout(5 * 1000);   
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){   
        	return conn.getInputStream();      
        }   
        return null; 
    }
    /**  
     * Get data from stream 
     * @param inStream  
     * @return byte[]
     * @throws Exception  
     */  
    public static byte[] readStream(InputStream inStream) throws Exception{   
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();   
        byte[] buffer = new byte[1024];   
        int len = 0;   
        while( (len=inStream.read(buffer)) != -1){   
            outStream.write(buffer, 0, len);   
        }   
        outStream.close();   
        inStream.close();   
        return outStream.toByteArray();   
    } 

    /**
     * 淇濆瓨鏂囦欢
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static void saveFile(Bitmap bm, String path,String fileName) throws IOException {
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }
}
