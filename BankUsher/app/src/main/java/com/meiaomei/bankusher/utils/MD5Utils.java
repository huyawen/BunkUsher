package com.meiaomei.bankusher.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/6/16.
 */
public class MD5Utils {


    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
//    protected static char hexDigits[] = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
    protected static MessageDigest messagedigest = null;
    static{
        try{
            messagedigest = MessageDigest.getInstance("MD5");
        }catch(NoSuchAlgorithmException nsaex){
            System.err.println(MD5Utils.class.getName()+"初始化失败,MessageDigest不支持MD5Utils。");
            nsaex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String md5=getFileMD5(new File(""));
        System.out.println("md5:"+md5);
    }


    public static String getFileMD5(File file) throws IOException {
//	   FileLock lock=null;
        FileChannel fc=null;
        String md5="";
        try {
            fc=new RandomAccessFile(file,"rw").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messagedigest.update(byteBuffer);
            byte [] b=messagedigest.digest();
            md5=bufferToHex(b);
            return md5;
        }catch (IOException e) {
            throw new IOException("取MD5异常");
        }
        finally{
            if(fc!=null){
                fc.close();
            }
        }
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }


    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }


    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }



    /**
     * md5编码
     * @param string
     * @return
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }



    /**
     * 验证是否是合法的签名
     * 此方法横容易被反编译破解
     * @return
     */
    public static boolean JavaValidateSign(Context context){
        boolean isValidated  = false;
        try {
            //得到签名
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;

            String relaseSign=signs[0].toCharsString();
            int hashCode=signs[0].hashCode();
            //将签名文件MD5编码一下
            String signStr = md5(signs[0].toCharsString());
            //将应用现在的签名MD5值和我们正确的MD5值对比
            return signStr.equals("a09920c835cd17c962a48954b7b971f7");//这里写release下的签名的MD5加密后的字符串
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return isValidated;
    }
}
