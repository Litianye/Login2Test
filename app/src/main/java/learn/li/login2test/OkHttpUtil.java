package learn.li.login2test;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 李天烨 on 2016/8/16.
 */

public class OkHttpUtil {

    private static String loctionStr, dataStr, loginStr, registerStr;
    private static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    }).build();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void closeClient(){

    }
    /**
     * Post键值对，登陆
     */

    public static String LoginPostParams(String url, final String account, final String password) throws InterruptedException {
        final RequestBody body = new FormBody.Builder().add("phoneNumber", account)
                .add("password", password).build();
        final Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request", request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("超时：", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                setLoginStr(response.body().string());
                Log.i("MSG", loginStr);
                if (!loginStr.equals("")) {
                    Log.w("toString", response.toString());
                    Log.i("200", "httpGet OK: " + account + "," + password + "," + loginStr);
                } else {
                    Log.i("!200", "httpGet error: " + account + "," + password + "," + loginStr);
                    Log.w("toString", response.toString());
                }

            }
        });
        while (loginStr==null){
            Thread.sleep(1000);
        }
        return loginStr;
    }

    /**
     * Post键值对，注册
     */

    public static String RegisterPostParams(String url, String phoneNumber, String password,
                                            String name, String email) throws InterruptedException {
        RequestBody body = new FormBody.Builder().add("phoneNumber", phoneNumber)
                .add("password", password)
                .add("name", name)
                .add("email", email)
                .build();

        final Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request",request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("超时：", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                registerStr = response.body().string();
                if (response.isSuccessful()) {
                    Log.i("login register 200", "httpGet OK: ");
                } else {
                    Log.i("login register !200", "httpGet error: ");
                }
            }
        });
        while (registerStr == null){
            Thread.sleep(1000);
        }
        return registerStr;
    }
    public static void setLoginStr(String response){
        loginStr = response;
    }

    public static void postLocParams(String url, String longitude, String latitude) {
        RequestBody body = new FormBody.Builder().add("locX", longitude)
                .add("locY", latitude)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request",request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("超时：", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    Log.i("location 200", "httpGet OK: ");
                } else {
                    Log.i("location !200", "httpGet error: ");
                }
            }
        });
    }

    public static void heartRatePost(String url, String heartRate) {
        RequestBody body = new FormBody.Builder().add("heartRate", heartRate)
                .build();
        final Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request", "dataPost request OK");
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("超时：", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("heartRatet_200", "httpGet OK: ");
                } else {

                    Log.i("heartRate_!200", "httpGet error: ");
                }
            }
        });
    }

    public static void dataPost(String url, final String data) {
        RequestBody body = new FormBody.Builder().add("data", data)
                .build();
        final Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request", "dataPost request OK");
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("超时：", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    dataStr = response.body().string();
                    Log.e("dataStr_response", dataStr);
                    Log.i("dataPost_200", "httpGet OK: ");
                } else {
                    Log.i("dataPost_!200", "httpGet error: ");
                }
            }
        });
    }

    public static void healthInfoPost(String url,String name, String medicalStatus, String medicalNote, String drugUse,
                                      String contactsName1, String contactsNumber1, String contactsName2, String contactNumber2,
                                      String weight, String stature, String irritability, String bloodType) {
        RequestBody body = new FormBody.Builder()
//                .add("phoneNumber", phoneNumber)
                .add("name", name)
                .add("medicalStatus", medicalStatus)
                .add("medicalNote", medicalNote)
                .add("drugUse", drugUse)
                .add("contactsName1", contactsName1)
                .add("contactsNumber1", contactsNumber1)
                .add("contactsName2", contactsName2)
                .add("contactsNumber2", contactNumber2)
                .add("weight", weight)
                .add("stature", stature)
                .add("irritability", irritability)
                .add("bloodType", bloodType)
                .build();
        final Request request = new Request.Builder().url(url).post(body).build();
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
                    Log.i("200", "httpGet OK: ");
                } else {
                    Log.i("!200 httpGet error", response.toString());
                }
            }
        });
    }

    public static String getDataStr() {
        if (dataStr != null){
            return dataStr;
        }else {
            return "{\"error\":\"0\"}";
        }
    }
}
