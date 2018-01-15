package com.meiaomei.bankusher.utils;

import android.util.Log;

import com.meiaomei.bankusher.entity.Protocol;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by huyawen on 2018/1/11.
 * email:1754397982@qq.com
 */

public class UpLoadImage {

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*10000000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private static final String BOUNDARY = FileUtils.generateUuid() ; //UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
    private static final String PREFIX="----------------";
    private static final String LINE_END="\n\r";
    private static final String CONTENT_TYPE = "multipart/form-data"; //数据类型为文件

    /** * android上传文件到服务器
     * @param file 需要上传的文件
     * @param requestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file, String requestURL) {
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存

            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);//设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            String cooke=Protocol.getCookedId();
            conn.setRequestProperty("Cookie",cooke);

            if(file!=null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam=conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                String name=file.getName();
                String[] params = {"\"id\"","\"name\"","\"type\"","\"lastModifiedDate\"","\"size\""};
                String[] values = {"WU_FILE_1",
                                    file.getName(),
                                    "image/jpeg",//name.substring(name.lastIndexOf(".")+1,name.length())
                                    new Date().toString(),
                                    file.length()+""};
                //添加docName,docType,sessionKey,sig参数
                StringBuffer sb = new StringBuffer();
                for(int i=0;i<params.length;i++){
                    //添加分割边界
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);

                    sb.append("Content-Disposition: form-data; name=" + params[i] + LINE_END);
                    sb.append(LINE_END);
                    sb.append(values[i]);
                    sb.append(LINE_END);
//                    dos.write(sb.toString().getBytes());
                }

                //file内容
//                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);

                sb.append("Content-Disposition: form-data; name=\"file\";filename=" + "\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: image/jpeg"+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                //读取文件的内容
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();
                //写入文件二进制内容
                dos.write(LINE_END.getBytes());
                //写入end data
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:"+res);
                if(res==200) {
                    String oneLine;
                    StringBuffer response = new StringBuffer();
                    BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((oneLine = input.readLine()) != null) {
                        response.append(oneLine);
                    }
                    return response.toString();
                }else{
                    return res+"";
                }
            }else{
                return "file not found";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "failed";
        } catch (IOException e) {
            e.printStackTrace();
            return "failed";
        }
    }



    public static final String SUCCESS="1";
    public static final String FAILURE="0";
    public static String uploadFiletwo(File file,String RequestURL) {
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); conn.setReadTimeout(TIME_OUT); conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);
            //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file!=null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam=conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY); sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                String name=file.getName();
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+name+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:"+res);
                if(res==200)
                {
                    return SUCCESS;
                }
            }
        } catch (MalformedURLException e)
        { e.printStackTrace(); }
        catch (IOException e)
        { e.printStackTrace(); }
        return FAILURE;
    }





    HttpURLConnection connection = null;
    DataOutputStream dos = null;

    int bytesAvailable, bufferSize, bytesRead;
    int maxBufferSize = 1 * 1024 * 512;
    byte[] buffer = null;

    String boundary = "-----------------------------1954231646874";
    Map<String,String> map=new HashMap<>();

    FileInputStream fin = null;

    // 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
    private String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    public String uploadPicToWebServer(String serverUrl,String filePath) {

        try {
            URL url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();

            // 允许向url流中读写数据
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);

            // 启动post方法
            connection.setRequestMethod("POST");

            // 设置请求头内容
            connection.setRequestProperty("connection", "Keep-Alive");
            connection
                    .setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------1954231646874");

            dos = new DataOutputStream(connection.getOutputStream());
            fin = new FileInputStream(filePath);

            File file = new File(filePath);
            dos.writeBytes(boundary);
            dos.writeBytes("\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename="+file.getName());
            dos.writeBytes("\r\n");
            dos.writeBytes("Content-Type: image/jpeg");
            dos.writeBytes("\r\n");
            dos.writeBytes("\r\n");

            // 取得本地图片的字节流，向url流中写入图片字节流
            bytesAvailable = fin.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fin.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fin.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fin.read(buffer, 0, bufferSize);
            }
            dos.writeBytes("\r\n");
            dos.writeBytes("-----------------------------1954231646874--");
            dos.writeBytes("\r\n");
            dos.writeBytes("\r\n");

            // Server端返回的信息
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream inStream = connection.getInputStream();
                ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inStream.read(buffer)) != -1) {
                    outSteam.write(buffer, 0, len);
                }

                outSteam.close();
                inStream.close();
                return new String(outSteam.toByteArray());
            }

            if (dos != null) {
                dos.flush();
                dos.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
