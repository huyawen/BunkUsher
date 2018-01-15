package com.meiaomei.bankusher.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.meiaomei.bankusher.BankUsherApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * Created by huyawen on 2017/3/10.
 * <p/>
 * 获取设备信息
 */
public class DeviceInfoUtils {

    ConnectivityManager mConnectivity;
    TelephonyManager mTelephony;
    NetworkInfo info;
    Context context;

    public DeviceInfoUtils(Context context) {
        this.context = context;
        mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        info = mConnectivity.getActiveNetworkInfo();
    }

    public void getSetting() {//无线网下读取ip的以及修改的方法
        String statdns1 = android.provider.Settings.System.WIFI_STATIC_DNS1;
        String statdns2 = android.provider.Settings.System.WIFI_STATIC_DNS2;
        String sgateway = android.provider.Settings.System.WIFI_STATIC_GATEWAY;
        String staticip = android.provider.Settings.System.WIFI_STATIC_IP;
        String snetmask = android.provider.Settings.System.WIFI_STATIC_NETMASK;
        String staticus = android.provider.Settings.System.WIFI_USE_STATIC_IP;

        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "0");
        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_DNS1, "192.168.0.2");
        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_DNS2, "192.168.0.3");
        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_GATEWAY, "192.168.0.1");
        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_NETMASK, "255.255.255.0");
        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_IP, "1");
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 程序是否在前台运行
     */
    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) BankUsherApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = BankUsherApplication.getAppContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);//后台的服务太多  以至于没检查到自己的那一个
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }



    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在不是activity的类里
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    //静默卸载
    public static void uninstallSlient(String apkName, String taskId, String version, String macAdress, String ipAndPort) {
        String cmd = "pm uninstall " + BankUsherApplication.getAppContext().getPackageName();
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            //卸载也需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            //执行命令
            int flag = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是有线还是无线
     *
     * @return
     */
    public int getNetworkType() {
        int type = 1;
        if (info == null) {//无网络连接
            return 2;
        }
        int netType = info.getType();
        if (info.isConnected()) {
            if (netType == ConnectivityManager.TYPE_WIFI) { //wifi
                type = 1;
            } else { //有线
                type = 0;
            }
        }

        return type;
    }


    /**
     * 获取当前应用版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // 获取PackageManager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是当前类的包名，0代表是获取版本信息
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }


    /**
     * 获取当前应用版本号
     *
     * @return
     */
    public static String getAppVersionName() {
        String versionName = "";
        try {
            // 获取PackageManager的实例
            PackageManager packageManager = BankUsherApplication.getAppContext().getPackageManager();
            // getPackageName()是当前类的包名，0代表是获取版本信息
            PackageInfo packageInfo = packageManager.getPackageInfo(BankUsherApplication.getAppContext().getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }


    /**
     * 获取设备MAC
     *
     * @return
     */
    public static String getDeviceMac(Context context) {
        String result = "";
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            result = wifiInfo.getMacAddress();
            result = result.replace(":", "");
            return result.trim();
        } catch (Exception e) {
            return result;
        }
    }


    /**
     * 通过调用Linux的busybox命令获取Mac地址  可以取到手机的mac地址
     */
    public static String getMacFromCallCmd() {
        try {
            String readLine = "";
            Process process = Runtime.getRuntime().exec("busybox ifconfig");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((readLine = bufferedReader.readLine()) != null) {//执行命令cmd，只取结果中含有"HWaddr"的这一行
                if (readLine.contains("HWaddr")) {
                    return readLine.substring(readLine.indexOf("HWaddr") + 6, readLine.length() - 1);
                }
            }
        } catch (Exception e) {  //如果因设备不支持busybox工具而发生异常。
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 以太网下获取mac地址
     * wifi 下同样可以获得 mac地址
     */
    public static String getMacAddress() {
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }

                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return strMacAddr;
    }


    /**
     * 获取移动设备本地IP
     * 以太网下的ip
     *
     * @return
     */
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = (InetAddress) en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

    public static String getMacAddressNew() {
        /*获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。*/
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }


    public static String getDevicedId(){
        Build  bd=new Build();
        String hardware = bd.HARDWARE;
        String hdNum = bd.SERIAL;
        String mac = getMacAddressNew();
        String id=hardware + hdNum + mac;
        String md5id=MD5Utils.md5(id);
        return md5id;
    }


    public static String getPortAndIp(String[] args) {
        // 执行linux命令并且输出结果
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HashMap<String, String> getWebMacInfo() {
        String s_netmask = "";//子网掩码
        String s_gateway = "";//默认网关
        String s_dns1 = "";
        String ip = "";
        HashMap<String, String> map = new HashMap<>();

        Build bd = new Build();
        String model = bd.MODEL;//手机型号
        Log.e("===model", "======" + model);
        String version_release = Build.VERSION.RELEASE; // 设备的android系统版本
        Log.e("===version_release", "======" + version_release);
        String desplay = bd.DISPLAY;
        Log.e("===desplay", "======" + desplay);//获取设备显示的版本包
        String cpuId = bd.CPU_ABI;
        Log.e("===cpuId", "======" + cpuId + "|" + bd.CPU_ABI2);//获取设备CPUID
        String userName = bd.USER;
        Log.e("===username", "======" + userName);//获取设备用户名
        String type = bd.TYPE;
        Log.e("===type", "======" + type);//获取设备版本类型
        String deviceId = bd.ID;
        Log.e("===deviceId", "======" + deviceId);//获取设备id
        String hardware = bd.HARDWARE;
        Log.e("===hardware", "======" + hardware);//获取硬件
        String rootBoard = bd.BOARD;
        Log.e("===rootBoard", "======" + rootBoard);//获取设备基板名称
        String deviceName = bd.MANUFACTURER;
        Log.e("===deviceName", "======" + deviceName);//获取设备名称


        String macAdress = DeviceInfoUtils.getMacAddress();
        Log.e(" strMacAddr:======", "" + macAdress);
        DeviceInfoUtils deviceInfoUtils = new DeviceInfoUtils(context);
        if (deviceInfoUtils.getNetworkType() == 1) {
            Log.e("NetworkType======", "NetworkType:" + "无线wifi");
            //无线网环境下的  子网掩码和默认网关
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo d = wifiManager.getDhcpInfo();//存储当前网络状态的状态参数信息，但是返回值是integer类型
            s_gateway = "Default Gateway: " + Formatter.formatIpAddress(d.gateway);//默认网关
            String s_ipAddress = "IP Address: " + Formatter.formatIpAddress(d.ipAddress);
            s_netmask = "Subnet Mask: " + Formatter.formatIpAddress(d.netmask);//子网掩码
            String s_leaseDuration = "Lease Time: " + Formatter.formatIpAddress(d.leaseDuration);
            s_dns1 = "DNS 1" + Formatter.formatIpAddress(d.dns1);
            String s_dns2 = "DNS 2: " + Formatter.formatIpAddress(d.dns2);
//          String s_serverAddress="Server IP: "+Formatter.formatIpAddress(d.serverAddress);//服务器地址
            Log.e("Address=====", s_ipAddress + " ");
            Log.e("Default Gateway:=====", s_gateway + " ");
            Log.e("Subnet Mask:=====", s_netmask + " ");
            int wifi = getWiFi();
            if (wifi != 0) {//不为0 为wifi的ip
                ip = intToIp(wifi);
                Log.e("wifi连接下的ip=====", ip + "");
            }
        } else if (deviceInfoUtils.getNetworkType() == 0) {//以太网下
            Log.e("NetworkType======", "NetworkType:" + "网线连接");
            InetAddress in = DeviceInfoUtils.getLocalInetAddress();
            ip = in.toString().replace("/", "");

            try {
                //获取ETHERNET_SERVICE参数
                String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);
                Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");
                Class<?> ipConfigurationClass = Class.forName("android.net.IpConfiguration");
                Class<?> staticIpConfigurationClass = Class.forName("android.net.StaticIpConfiguration");
                //获取ethernetManager服务对象
                Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);
                //得到ipConfiguration的对象
                Object getConfiguration = ethernetManagerClass.getDeclaredMethod("getConfiguration").invoke(ethernetManager);
                if (getConfiguration != null) {
                    //得到staticIpConfiguration的对象
                    Object staticIpConfiguration = ipConfigurationClass.getDeclaredMethod("getStaticIpConfiguration").invoke(getConfiguration);
                    Constructor<?> con = staticIpConfigurationClass.getDeclaredConstructor(staticIpConfigurationClass);//得到构造方法
                    Object object = con.newInstance(staticIpConfiguration);//初始化构造方法
                    Field field = staticIpConfigurationClass.getDeclaredField("gateway");
                    InetAddress igateway = (InetAddress) field.get(object);
                    String gateway = igateway.toString().replace("/", "");
                    Field fi = staticIpConfigurationClass.getDeclaredField("ipAddress");
                    LinkAddress address = (LinkAddress) fi.get(object);
                    Log.e("gateway", "gateway===  " + gateway);
                    Log.e("LinkAddress", "LinkAddress===  " + address.getAddress().toString());
                    Log.e("LinkAddress", "LinkAddress===  " + address.getPrefixLength() + "");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(" could not be invoked", e);
            }
            Log.e("以太网的ip=====", ip + "");

        } else if (deviceInfoUtils.getNetworkType() == 2) {
            Log.e("NetworkType======", "NetworkType:" + "无网络");
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int densityDpi = (int) dm.densityDpi;
        Log.e("density=====", density + " ");
        Log.e("densityDpi=====", densityDpi + " ");
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Log.e("设备的输出分辨率为=======", width + " x" + height);

        //硬件系统可用空间与总空间
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();//指1024 byte
        long blockCount = sf.getBlockCount();
        long availCount = sf.getAvailableBlocks();
        Log.e("======", "block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 + "KB");
        Log.e("===++++++++", "可用的block数目：:" + availCount + ",可用大小:" + availCount * blockSize / 1024 + "KB");

        map.put("ip", ip);
        map.put("mac", macAdress);
        map.put("defaultGateway", s_gateway);
        map.put("dns", s_dns1);
        map.put("subnetMask", s_netmask);
        map.put("totalPhysicalMemory", blockSize * blockCount / 1024 + "KB");

        map.put("deviceName", deviceName);
        map.put("systemType", "android" + version_release);
        map.put("firmwareVersion", model);
        map.put("outputResolution", width + " x" + height);
        map.put("harddiskFreespace", availCount * blockSize / 1024 + "KB");
        map.put("loginUserName", userName);
        map.put("cpuId", cpuId);
        map.put("diskId", deviceId);

        return map;
    }

    //返回WiFi地址的整数表示
    private int getWiFi() {
        int ip = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {//WiFi是否开启状态
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ip = wifiInfo.getIpAddress();
        }
        return ip;
    }

    //整数转换成IP的形式
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

}
