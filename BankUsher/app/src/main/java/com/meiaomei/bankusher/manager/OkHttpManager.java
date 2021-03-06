package com.meiaomei.bankusher.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.meiaomei.bankusher.entity.Protocol;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by meiaomei on 2017/4/17.
 */
public class OkHttpManager {

    private OkHttpClient client;
    //超时时间
    public static final int TIMEOUT = 1000 * 60;
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");//文件的mediatype 二进制流
    //XML文件的类型 请求
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=utf-8");
    //json请求
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Handler handler = new Handler(Looper.getMainLooper()) {

    };

    public OkHttpManager() {
        this.init();
    }

    private void init() {
        client = new OkHttpClient();
        //设置超时
        client.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS).readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public static String getUrl(String paramString, int paramInt) {
        switch (paramInt) {
            default:
                return "";
            case 0:
                return String.format("%s/j_spring_security_check", new Object[]{paramString});//登录
            case 1:
                return String.format("%s/mobile/findReceptionRecord", new Object[]{paramString});
            case 2:
                return String.format("%s/user/findById", new Object[]{paramString});
            case 4:
                return String.format("%s/entryexitrecord/update", new Object[]{paramString});//更新爱好的接口
            case 3:
                return String.format("%s/user/mobileUpdate", new Object[]{paramString});
            case 5:
                return String.format("%s/user/mobileAdd", new Object[]{paramString});
            case 6:
                return String.format("%s/doLogout", new Object[]{paramString});
            case 7:
                return String.format("%s/mobile/adduserInfo", new Object[]{paramString});//编辑用户信息  (此接口调不出来)
            case 8:
                return String.format("%s/user/update", new Object[]{paramString}); //编辑用户信息 维持cookie  (此接口不生效)内部接口
            case 9:
                return String.format("%s/uploadfile/fileUpload", new Object[]{paramString});// 上传图片接口
            case 10:
                return String.format("%s/mobile/getTerminalProduct", new Object[]{paramString});//更新apk版本的接口
            case 11:
                break;
        }
//        return String.format("%s/entryexitrecord/receptionSetting", new Object[]{paramString});
            return "";
    }

    /**
     * post请求  json数据为body
     */
    public void postJson(String url, String json, final HttpCallBack callBack) {

        String cookedId = Protocol.getCookedId();
        RequestBody localRequestBody = RequestBody.create(JSON, json);
        Request localRequest = new Request.Builder().
                 header("X-Requested-With".toLowerCase(), "XMLHttpRequest")
                .header("Cookie", cookedId)
                .url(url)
                .post(localRequestBody).build();

        OnStart(callBack);

        client.newCall(localRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.e("OkhttpManager", "-isSuccessful--" + response.isSuccessful());
                    onSuccess(callBack, response.body().string(), response.header("Set-Cookie"));
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }

    /**
     * 上传文件
     *
     * @param actionUrl 接口地址
     * @param filePath  本地文件地址
     */
    public <T> void upLoadFile( String actionUrl, String filePath, final ReqCallBack<T> callBack) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 这里演示添加用户ID
        builder.addFormDataPart("userId", "20160519142605");
        builder.addFormDataPart("id","WU_FILE_0");
        builder.addFormDataPart("name",new File(filePath).getName());
        builder.addFormDataPart("type","image/jpeg");
        builder.addFormDataPart("lastModifiedDate",new Date().toString());
        builder.addFormDataPart("size",new File(filePath).length()+"");
        builder.addFormDataPart("file", filePath,
                RequestBody.create(MediaType.parse("image/jpeg"), new File(filePath)));
        //创建File
        File file = new File(filePath);
        //创建RequestBody
        RequestBody body =builder.build();
        body.create(MEDIA_OBJECT_STREAM, file);
        String cookedId=Protocol.getCookedId();
        //创建Request
        final Request request = new Request.Builder()
//                .header("Cookie", cookedId)
                .url(actionUrl)
                .post(body).build();
        final Call call = client.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("结果", e.toString());
                failedCallBack("上传失败", callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e("结果", "response ----->" + string);
                    successCallBack((T) string, callBack);
                } else {
                    failedCallBack("上传失败", callBack);
                }
            }
        });
    }


    /**
     * post 请求发送   任务轮询请求消息头参数
     *
     * @param url
     * @param command
     * @param mac
     */
    public void postHead(String url, String command, String mac, String xmlString, final HttpCallBack callBack) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_XML, xmlString);////创建一个请求实体对象 RequestBody
        Request request = new Request.Builder().url(url)
                .addHeader("command", command)
                .addHeader("mac", mac).post(body).build();

        OnStart(callBack);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("OkHttpManager-fail", e.toString());
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.e("OkHttpManager-success", response.message());
                    onSuccess(callBack, response.body().string(), response.header("Set-Cookie"));
                } else {
                    Log.e("OkHttpManager", response.message());
                    OnError(callBack, response.message());
                }
            }
        });
    }

    /**
     * post请求  map是body
     *
     * @param url
     * @param map
     * @param callBack
     */
    public void postMap(String url, String command, String mac, Map<String, String> map, final HttpCallBack callBack) {
        FormBody.Builder builder = new FormBody.Builder();//请求form表单
        //遍历map
        if (map != null) {//添加表单
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody body = builder.build();//请求体
        Request request = new Request.Builder().addHeader("command", command).addHeader("mac", mac).url(url).post(body).build();//请求方式为post
        OnStart(callBack);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callBack, response.body().string(), response.header("Set-Cookie"));
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }


    /**
     * get 请求
     *
     * @param url
     * @param callBack
     */
    public void getJson(String url, final HttpCallBack callBack) {
        Request request = new Request.Builder().url(url).build();
        OnStart(callBack);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callBack, response.body().string(), response.header("Set-Cookie"));
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }

    public void OnStart(HttpCallBack callBack) {
        if (callBack != null) {
            callBack.onstart();
        }
    }

    public void onSuccess(final HttpCallBack callBack, final String data, final String cookie) {
        if (callBack != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {//在主线程操作
                    callBack.onSusscess(data, cookie);
                }
            });
        }
    }

    public void OnError(final HttpCallBack callBack, final String msg) {
        if (callBack != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(msg);
                }
            });
        }
    }

    public static abstract class HttpCallBack {
        //开始
        public void onstart() {
        }

        //成功回调
        public abstract void onSusscess(String data, String cookie);

        //失败
        public void onError(String meg) {
        }
    }


    public interface ReqCallBack<T> {
        /**
         * 响应成功
         */
        void onReqSuccess(T result);

        /**
         * 响应失败
         */
        void onReqFailed(String errorMsg);
    }

    /**
     * 统一同意处理成功信息
     *
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     *
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }
}
