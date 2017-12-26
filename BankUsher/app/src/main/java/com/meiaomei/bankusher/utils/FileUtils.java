package com.meiaomei.bankusher.utils;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Created by huyawen on 2017/3/16.
 * <p/>
 * 文件保存的工具类
 */
public class FileUtils {


    public static String SDPATH = Environment.getExternalStorageDirectory() + "/";
    private static ArrayList<String> filelist = new ArrayList<String>();
    ProgressListener progressListener;

    public FileUtils() {
    }


    /**
     * 在SD卡上创建文件 file/文件名
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录 file
     *
     * @param dirName 目录名字
     * @return 文件目录
     */
    public static File createDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }
    //判断是不是文件夹
    public static boolean isDirectory(String dirName) {
        File file = new File(SDPATH + dirName);
        return file.isDirectory();
    }


    /**
     * 创建目录
     *
     * @param path 输出路径
     * @return 返回
     */
    public static File createPathIfNotExit(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param filePath file的路径
     * @return
     */
    public static File createFileIfNotExit(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 创建新的文件
     *
     * @param filePath 文件路径
     * @return 新的文件
     */
    public static File createNewFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 向sd卡中存储数据 （主要负责向 sd卡中存储String字符串）
     *
     * @param file 文件
     * @param info 输入信息内容
     * @return 是否成功写入
     */
    public static boolean writeDatesToSDCard(File file, String info) {
        boolean flag = true;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(info);
            writer.write("\t\r\n");
            writer.flush();
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 向sd卡中存储数据 (以流的方式 主要负责网上传过来的apk 同步文件等)
     *
     * @param file        文件
     * @param inputStream 输入流
     * @return 是否成功写入
     */
    public static boolean writeDatesToSDCard(File file, InputStream inputStream) {
        boolean flag = true;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buf = new byte[4096];
            int ch = -1;
            while ((ch = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, ch);
            }
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 向sdcard中写入文件
     *
     * @param filename 文件名
     * @param content  文件内容
     */
    public static boolean saveToSDCard(String filename, String dirName, String content) {
        boolean flag = true;
        File file = new File(SDPATH + dirName, filename);
        try {
            OutputStream out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.close();
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 读取file文本的内容
     *
     * @param file 文本路径
     * @return
     */
    public static String readSDCardMsg(File file) {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String buffer = null;
            while ((buffer = reader.readLine()) != null) {
                sb.append(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public File write2SDFromInput(String path, String fileName, InputStream input, int fileLength, ProgressListener listener) {
        File file = null;
        OutputStream output = null;

        try {
            createDir(path);//先创建目录 xml/files
            file = createSDFile(path + fileName);//创建文件
            output = new FileOutputStream(file);
            byte[] buf = new byte[4096];
            int ch = -1;
            int total = 0;
            while ((ch = input.read(buf)) != -1) {
                output.write(buf, 0, ch);

                total += ch;
                float t = (1.0f * total) / fileLength;
                progressListener = listener;
                if (progressListener != null) {
                    progressListener.update(t);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public interface ProgressListener {

        void update(float progress);
    }


    //通过递归得到某一路径下所有的目录及其文件
    public static ArrayList getFiles(String filePath) {
        File root = new File(filePath);
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {//是文件夹
                getFiles(file.getAbsolutePath());
                filelist.add(file.getAbsolutePath());
                Log.e("filepath=====", "显示" + filePath + "下所有子目录及其文件" + file.getAbsolutePath());
            } else {//是文件
                filelist.add(file.getAbsolutePath());
                Log.e("filepath=====", "显示" + filePath + "下所有子目录" + file.getAbsolutePath());
            }
        }
        return filelist;
    }


    //删除某文件夹下的文件
    public static void deleteFile(File root){
        File files [] =root.listFiles();
        if (files!=null){
            for (File file : files){
                if (file.isDirectory()){

                }else {
                    if (file.exists()){
                        file.delete();
                    }
                }
            }
        }


    }
    //删除文件下的所有资源
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        root.delete();
    }


    /**
     * Gzip 压缩数据
     *
     * @param unGzipStr
     * @return
     */
    public static String compressForGzip(String unGzipStr) {

        if (TextUtils.isEmpty(unGzipStr)) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(unGzipStr.getBytes());
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return new BASE64Encoder().encode(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gzip解压数据
     *
     * @param gzipStr
     * @return
     */
    public static String decompressForGzip(String gzipStr) {
        if (TextUtils.isEmpty(gzipStr)) {
            return null;
        }
        byte[] t = new byte[0];
        try {
            t = new BASE64Decoder().decodeBuffer(gzipStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            gzip.close();
            in.close();
            out.close();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 产生32位，删除"-"符合的UUID。
     *
     * @return 适应于本应用的UUID字符串
     */
    public static String generateUuid() {
        String id = "";
        String[] str = UUID.randomUUID().toString().split("-");
        for (int x = 0; x < str.length; x++) {
            id = id + str[x];
        }
        return id;
    }
}
