package learn.li.login2test;

import android.util.Log;

import java.io.IOException;

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
    public static boolean result = false;
    public static String weatherJSON, loginStr ="0", loctionStr;
    public static OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Post键值对
     */

    public static boolean LoginPostParams(String url, final String account, final String password) {
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
            public void onResponse(Call call, final Response response) throws IOException {
                loginStr = response.body().string();
                System.out.println(loginStr+";;");
                if (response.isSuccessful()) {
                    Log.w("toString", response.toString());
                    Log.i("200", "httpGet OK: " + account+","+password +"," + loginStr);
                    setResult(true);
                } else {
                    Log.i("!200", "httpGet error: " + account+","+password +","+ loginStr);
                    Log.w("toString", response.toString());
                    setResult(false);
                }
            }
        });
        return result;
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
        return result;
    }


    public static boolean postLocParams(String url, String account, String devID,
                                        String loc) {
        RequestBody body = new FormBody.Builder().add("username", account)
                .add("devID", devID)
                .add("location", loc)
                .build();
        Log.i("device", loc);
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
        return result;
    }

    public static boolean postLocationParams(String url, final String longitude, final String latitude) {
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
        return result;
    }

    /**
     * POST提交Json数据
     *
     * @param url
     */
    public static void postJson(String url) {
        Log.i("点击确认", "点了");
        String json =  "{\"username\":\"0000\",\"devID\":\"001\",\"temp\":\"32\",\"hum\":\"30\",\"air\":\"150\",\"elec\":\"50\",\"pic\":\"2333\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        Log.i("request",request.toString());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r = response.body().string();
                if (response.isSuccessful()) {
                    Log.i("200", "httpGet1 OK: " + r);
                } else {
                    Log.i("!200", "httpGet1 error: " + r);
                }
            }
        });
    }

    public static boolean getResult(){
        return result;
    }

    public static void setResult(boolean num){
        result = num;
    }

    public static String weatherGet() throws IOException {
        String url = "http://api.yytianqi.com/observe?city=CH070101&key=w5ersf4nbd17ajhf";
        Request request = new Request.Builder()
                    .url(url)
                    .build();
        Log.i("request", request.toString());

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                weatherJSON = response.body().string();
                if (response.isSuccessful()) {
                    Log.i("Weather", "httpGet OK: " + response.toString());
                    Log.i("body", weatherJSON);
                } else {
                    Log.i("Weather", "httpGet error: " + response.toString());
                    Log.i("body", weatherJSON);
                }
            }
        });
        while (weatherJSON == null){
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        return weatherJSON;
    }

    public static String locationGet(String url, String account) {
        RequestBody body = new FormBody.Builder().add("username", account)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request", request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
        while (loctionStr == null){
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        return loctionStr;
    }
//    /**
//     * OkHttp的get请求
//     * 需要加线程
//     */
//    private void weatherGet(final String getUrl) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = OkHttpHelper.getCacheRequest_NOT_STORE(getUrl);
//                    Response response = client.newCall(request).execute();
//                    String r = response.body().string();
//                    if (response.isSuccessful()) {
//                        Log.i(TAG, "httpGet1 OK: " + r);
//                    } else {
//                        Log.i(TAG, "httpGet1 error: " + r);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//    }

}
