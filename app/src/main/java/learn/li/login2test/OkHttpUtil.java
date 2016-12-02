package learn.li.login2test;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 李天烨 on 2016/8/16.
 */

public class OkHttpUtil {
    private  static  boolean result = false;
    private static String weatherJSON,loctionStr, postResult="0", loginStr;
    private static OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Post键值对
     */

    public static String LoginPostParams(String url, final String account, final String password) {
        final RequestBody body = new FormBody.Builder().add("phoneNumber", account)
                .add("password", password).build();
        final Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request", request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                setLoginStr(response.body().string());
                Log.i("MSG", loginStr+";;");
                if (!loginStr.equals("")) {
                    Log.w("toString", response.toString());
                    Log.i("200", "httpGet OK: " + account + "," + password + "," + loginStr);
                } else {
                    Log.i("!200", "httpGet error: " + account + "," + password + "," + loginStr);
                    Log.w("toString", response.toString());
                }

            }
        });
        String safe = "{\"error\":\"3\"}";
        if (loginStr == null) {
            return safe;
        } else {
            return loginStr;

        }
    }

    public static void setLoginStr(String response){
        loginStr = response;
    }

    public static String postFromParameters(final String account, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://192.168.50.197:8082/Mojito/user/login.do";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder().add("phoneNumber", account)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder().url(url).post(formBody).build();
                    okhttp3.Response response = okHttpClient.newCall(request).execute();
                    postResult = response.body().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return postResult;
    }

    /**
     * Post键值对
     */

    public static boolean RegisterPostParams(String url, final String account, final String password) {
        RequestBody body = new FormBody.Builder().add("phoneNumber", account)
                .add("password", password).build();

        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request",request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loginStr = response.body().string();
                if (response.isSuccessful()) {
                    Log.i("200", "httpGet OK: " + account+","+password +","+ response.toString());
                    Log.i("body", loginStr);
                    setResult(true);
                } else {
                    Log.i("!200", "httpGet error: " + account+","+password +","+ response.toString());
                    Log.i("body", loginStr);
                    setResult(false);
                }
            }
        });
        return false;
    }


    public static void postLocParams(String url, String longitude, String latitude) {
        RequestBody body = new FormBody.Builder().add("longitude", longitude)
                .add("latitude", latitude)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request",request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    setResult(true);
                    Log.i("200", "httpGet OK: ");
                    Log.i("body", response.body().string());
                } else {
                    setResult(false);
                    Log.i("!200", "httpGet error: ");
                    Log.i("body", response.body().string());
                }
            }
        });
    }

    public static void postLocationParam(String url, final String longitude, final String latitude) {
        RequestBody body = new FormBody.Builder().add("x", longitude)
                                                 .add("y", latitude)
                                                 .build();
        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request",request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    setResult(true);
                    Log.i("200", "httpGet OK: "+response.toString());
                    Log.i("body", response.body().string());
                } else {
                    setResult(false);
                    Log.i("!200", "httpGet error: " + response.toString());
                    Log.i("body", response.body().string());
                }
            }
        });
    }

    public static void setResult(boolean num){
        result = num;
    }

    public static void dataPostTest(String url, byte[] data) throws UnsupportedEncodingException {
        String s = new String(data, "GBK");
        RequestBody body = new FormBody.Builder().add("data", s)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request", request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("超时：", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loctionStr = response.body().string();
                if (response.isSuccessful()) {
                    setResult(true);
                    Log.i("200", "httpGet OK: " + loctionStr);
                    Log.i("body", loctionStr);
                } else {
                    setResult(false);
                    Log.i("!200", "httpGet error: " + loctionStr);
                    Log.i("body", loctionStr);
                }
            }
        });
    }

    public static void dataPost(String url, String data) {
        RequestBody body = new FormBody.Builder().add("data", data)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request", request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("超时：", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    setResult(true);
                    Log.i("200", "httpGet OK: ");
                } else {
                    setResult(false);
                    Log.i("!200", "httpGet error: ");
                }
            }
        });
    }

}
