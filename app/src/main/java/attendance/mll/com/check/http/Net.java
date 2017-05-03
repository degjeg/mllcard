package attendance.mll.com.check.http;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import attendance.mll.com.check.CardTimes;
import attendance.mll.com.check.utils.GZipUti;
import attendance.mll.com.check.utils.L;
import attendance.mll.com.check.UserContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/5/2.
 */

public class Net {

    private static final String TAG = "net-";

    static L logger = L.get();

    public interface ServerInterface {
        @POST("selfservice/login")
        @FormUrlEncoded
        Call<String> login(@Body LoginReq body);

        @POST("selfservice/login/")
        @Headers({"x-requested-with: XMLHttpRequest",
                "Accept-Language: zh-cn",
                "Referer: http://221.237.152.61:81/accounts/login/",
                "Accept: text/html, */*",
                "Accept-Encoding: gzip, deflate"})
        @FormUrlEncoded
        Call<String> login1(@Field("username") String username, @Field("password") String password);


        @GET("selfservice/index/")
        @Headers({"Connection: keep-alive",
                "Upgrade-Insecure-Requests: 1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                "Accept: application/json, text/javascript, */*",
                "Origin: http://221.237.152.61:81",
                "X-Requested-With: XMLHttpRequest",
                "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36",
                "Content-Type: text/plain;charset=UTF-8",
                "Referer: http://221.237.152.61:81/accounts/login/",
                "Accept-Encoding: gzip, deflate",
                "Accept-Language: zh-CN,zh;q=0.8",
                /*"Cookie: sessionidadms=585f13804fc943492e75f411010f8545",*/})
            // @FormUrlEncoded
        Call<byte[]> index(@Header("Cookie:") String session);


        @POST("selfservice/att/CheckExact/?stamp=1493697223802&l=20&mnp=50")
        @Headers({"Connection: keep-alive",
                "Accept: application/json, text/javascript, */*",
                "Origin: http://221.237.152.61:81",
                "X-Requested-With: XMLHttpRequest",
                "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36",
                "Content-Type: text/plain;charset=UTF-8",
                "Referer: http://221.237.152.61:81/selfservice/index/",
                "Accept-Encoding: gzip, deflate",
                "Accept-Language: zh-CN,zh;q=0.8",
                /*"Cookie: sessionidadms=585f13804fc943492e75f411010f8545",*/})
            // @FormUrlEncoded
        Call<byte[]> checkExact(@Header("Cookie:") String session);


        @GET("selfservice/selfreport/")
        @Headers({"Connection: keep-alive",
                "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36",
                "Referer: http://221.237.152.61:81/selfservice/index/",
                "Accept-Encoding: gzip, deflate, sdch",
                "Accept-Language: zh-CN,zh;q=0.8",
                /*"Cookie: sessionidadms=585f13804fc943492e75f411010f8545",*/})
            // @FormUrlEncoded
        Call<byte[]> selfreport(@Header("Cookie:") String session);

        @POST("att/getallexcept/")
        @Headers({"Connection: keep-alive",
                "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36",
                "X-Requested-With: XMLHttpRequest",
                "Referer: http://221.237.152.61:81/selfservice/selfreport/",
                "Accept: application/json, text/javascript, */*",
                "Accept-Encoding: gzip, deflate",
                "Accept-Language: zh-CN,zh;q=0.8",
                /*"Cookie: sessionidadms=585f13804fc943492e75f411010f8545",*/})
            // @FormUrlEncoded
        Call<byte[]> getallexcept(@Header("Cookie:") String session);


        @POST("grid/att/CardTimes/")
        @Headers({
                // "Referer: http://221.237.152.61:81/page/att/CardTimes/?pure&ComeTime=2017-05-01&EndTime=2017-05-02&DeptIDs=&isForce=0&UserIDs=13846&dept_child=0"
                "Referer: http://221.237.152.61:81/page/att/CardTimes/?pure&ComeTime=2017-05-01&EndTime=2017-05-02&DeptIDs=&isForce=0&dept_child=0",
                "Accept: application/json, text/javascript, */*",
                "Origin: http://221.237.152.61:81",
        })
        Call<String> cardTimes(@Header("Cookie:") String session, @Header("Referer:") String referer, @Body String body);
    }

    public interface LoginHandler {
        void onLoginSuccess();

        void onLoginFail(String description);
    }

    public interface CardTimesHandler {
        void onCardTimeGet(CardTimes cardTimes);

        void onFailed(String description);
    }

    public static class MainThreadLoginHandler implements LoginHandler {
        LoginHandler othrer;

        public MainThreadLoginHandler(LoginHandler othrer) {
            this.othrer = othrer;
        }

        @Override
        public void onLoginSuccess() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    othrer.onLoginSuccess();
                }
            });
        }

        @Override
        public void onLoginFail(final String description) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    othrer.onLoginFail(description);
                }
            });
        }
    }

    public static class MainThreadCardTimesHandler implements CardTimesHandler {
        CardTimesHandler othrer;

        public MainThreadCardTimesHandler(CardTimesHandler othrer) {
            this.othrer = othrer;
        }

        @Override
        public void onCardTimeGet(final CardTimes cardTimes) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    othrer.onCardTimeGet(cardTimes);
                }
            });
        }

        @Override
        public void onFailed(final String description) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    othrer.onFailed(description);
                }
            });
        }
    }

    public static void login(String acc, String password, LoginHandler handler) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(ArrayConverterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://221.237.152.61:81")
                .build();
        ServerInterface service = retrofit.create(ServerInterface.class);

        // LoginReq loginReq = new LoginReq(acc, password);
        // Call<String> call = service.login(loginReq);
        //
        // Response<String> response = call.execute();
        // String value = response.body();
        // Log.e(TAG, "loginret:" + value);

        final MainThreadLoginHandler handler1 = new MainThreadLoginHandler(handler);
        Call<String> call = service.login1(acc, password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                final String value = response.body();
                logger.e(TAG, "loginret:" + value);

                for (String header : response.headers().names()) {
                    logger.e(TAG, header + ":" + response.headers().get(header));
                }

                if ("ok".equalsIgnoreCase(value)) {
                    UserContext.get().setSession(response.headers().get("Set-Cookie"));

                    handler1.onLoginSuccess();

                    selfreport();
                    // getallexcept();
                    checkExact();
                } else { // 用户名或密码错误，原因可能是:忘记密码；未区分字母大小写；未开启小键盘！
                    handler1.onLoginFail(value);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                handler1.onLoginFail(t.getMessage());
            }
        });


    }

    static void checkExact() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(ArrayConverterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://221.237.152.61:81")

                .build();
        ServerInterface service = retrofit.create(ServerInterface.class);

        Call<byte[]> call = service.checkExact("sessionidadms=" + UserContext.get().getSession());

        call.enqueue(new Callback<byte[]>() {
            @Override
            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
                byte[] beforeCompress = response.body();
                byte[] afterCompress = GZipUti.decompressForGzip(beforeCompress);


                for (String header : response.headers().names()) {
                    L.get("checkExact").e(TAG, header + ":" + response.headers().get(header));
                }
                L.get("checkExact").e(TAG + "checkExact", "解压前:" + beforeCompress.length + "\n" + Arrays.toString(beforeCompress));
                try {
                    if (afterCompress != null) {
                        L.get("checkExact").e(TAG + "checkExact", "解压后:" + afterCompress.length + "\n" + Arrays.toString(afterCompress));
                        L.get("checkExact").e(TAG + "checkExact", "字符串:" + new String(afterCompress, "utf-8"));
                    } else {
                        L.get("checkExact").e(TAG + "checkExact", "字符串:" + new String(beforeCompress, "utf-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<byte[]> call, Throwable t) {

            }
        });

    }

    static void selfreport() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(ArrayConverterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://221.237.152.61:81")

                .build();
        ServerInterface service = retrofit.create(ServerInterface.class);

        Call<byte[]> call = service.selfreport("sessionidadms=" + UserContext.get().getSession());

        call.enqueue(new Callback<byte[]>() {
            @Override
            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
                byte[] beforeCompress = response.body();
                byte[] afterCompress = GZipUti.decompressForGzip(beforeCompress);


                for (String header : response.headers().names()) {
                    L.get("selfreport").e(TAG, header + ":" + response.headers().get(header));
                }


                L.get("selfreport").e(TAG + "selfreport", "解压前:" + beforeCompress.length + "\n" + Arrays.toString(beforeCompress));


                try {
                    String content;
                    if (afterCompress != null) {
                        L.get("selfreport").e(TAG + "selfreport", "解压后:" + afterCompress.length + "\n" + Arrays.toString(afterCompress));
                        content = new String(afterCompress, "utf-8");
                    } else {
                        content = new String(beforeCompress, "utf-8");
                    }

                    L.get("selfreport").e(TAG + "selfreport", "字符串:" + content);

                    int index = content.indexOf("id_self_services");
                    if (index > 0) {
                        Pattern pattern = Pattern.compile("id_self_services\" value=\"(\\d+)\"");
                        Matcher matcher = pattern.matcher(content); // "  <input type=\"hidden\" id=\"id_self_services\" value=\"13846\"/> ");
                        if (matcher.find()) {
                            L.get("selfreport").e(TAG, "uid:" + matcher.group(1));
                            UserContext.get().setUid(matcher.group(1));
                        }
                    }

                    // <li class="head_welcome_info">欢迎，员工 邓军.</li>
                    index = content.indexOf("head_welcome_info");
                    if (index > 0) {
                        Pattern pattern = Pattern.compile("\"head_welcome_info\">([\\S\\s]*?)</li>");//(\S*)</li>
                        Matcher matcher = pattern.matcher(content); // "  <input type=\"hidden\" id=\"id_self_services\" value=\"13846\"/> ");
                        if (matcher.find()) {
                            L.get("selfreport").e(TAG, "uname:" + matcher.group(1));
                            UserContext.get().setWelcome(matcher.group(1));
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<byte[]> call, Throwable t) {

            }
        });

    }

    static void index() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(ArrayConverterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://221.237.152.61:81")

                .build();
        ServerInterface service = retrofit.create(ServerInterface.class);

        Call<byte[]> call = service.index("sessionidadms=" + UserContext.get().getSession());

        call.enqueue(new Callback<byte[]>() {
            @Override
            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
                byte[] beforeCompress = response.body();
                byte[] afterCompress = GZipUti.decompressForGzip(beforeCompress);


                for (String header : response.headers().names()) {
                    L.get("index").e(TAG + "index", header + ":" + response.headers().get(header));
                }
                L.get("index").e(TAG + "index", "解压前:" + beforeCompress.length + "\n" + Arrays.toString(beforeCompress));
                L.get("index").e(TAG + "index", "解压后:" + afterCompress.length + "\n" + Arrays.toString(afterCompress));

                try {
                    L.get("index").e(TAG + "index", "字符串:" + new String(afterCompress, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<byte[]> call, Throwable t) {

            }
        });

    }

//    static void getallexcept() {
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(ArrayConverterFactory.create())
//                // .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl("http://221.237.152.61:81")
//
//                .build();
//        ServerInterface service = retrofit.create(ServerInterface.class);
//
//        Call<byte[]> call = service.getallexcept("sessionidadms=" + UserContext.get().getSession());
//
//        call.enqueue(new Callback<byte[]>() {
//            @Override
//            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
//                byte[] beforeCompress = response.body();
//                byte[] afterCompress = GZipUti.decompressForGzip(beforeCompress);
//
//
//                for (String header : response.headers().names()) {
//                    logger.e(TAG + "getallexcept", header + ":" + response.headers().get(header));
//                }
//                logger.e(TAG + "getallexcept", "解压前:" + beforeCompress.length + "\n" + Arrays.toString(beforeCompress));
//                logger.e(TAG + "getallexcept", "解压后:" + afterCompress.length + "\n" + Arrays.toString(afterCompress));
//
//                try {
//                    logger.e(TAG + "getallexcept", "字符串:" + new String(afterCompress, "utf-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<byte[]> call, Throwable t) {
//
//            }
//        });
//
//    }

    public static void cardTimes(final CardTimesHandler handler) {
        // String testBody = "page=1&rp=20&sortname=undefined&sortorder=undefined&query=&qtype=&ComeTime=2017-05-01&DeptIDs=&dept_child=0&UserIDs=13846&EndTime=2017-05-02&isForce=0";
        Date dataNow = new Date();
        Date dataBeforeOnWeek = new Date(dataNow.getTime() - 7 * 24 * 3600000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String body = String.format(
                "page=1&rp=20&sortname=undefined&sortorder=undefined&query=&qtype=&ComeTime=%s&DeptIDs=&dept_child=0&UserIDs=%s&EndTime=%s&isForce=0",
                dateFormat.format(dataBeforeOnWeek),
                UserContext.get().getUid(),
                dateFormat.format(dataNow)
        );

        String refer = String.format("page=1&rp=20&sortname=undefined&sortorder=undefined&query=&qtype=&ComeTime=%s&DeptIDs=&dept_child=0&UserIDs=%s&EndTime=%s&isForce=0",
                dateFormat.format(dataBeforeOnWeek),
                UserContext.get().getUid(),
                dateFormat.format(dataNow)
        );
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                // .addConverterFactory(ArrayConverterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://221.237.152.61:81")

                .build();
        ServerInterface service = retrofit.create(ServerInterface.class);

        Call<String> call = service.cardTimes(
                "sessionidadms=" + UserContext.get().getSession(),
                refer,
                body);

        Response<String> response = null;
        final CardTimesHandler handler1 = new MainThreadCardTimesHandler(handler);

        L.get("cardTimes").e(TAG, "取打卡详情:" + "sessionidadms=" + UserContext.get().getSession() + "\n" +
                refer + "\n" +
                body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                for (String header : response.headers().names()) {
                    logger.e(TAG, header + ":" + response.headers().get(header));
                }
                L.get("cardTimes").e(TAG, "字符串:" + response.body());
                CardTimes cardTimes = JSON.parseObject(response.body(), CardTimes.class);
                handler1.onCardTimeGet(cardTimes);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                handler1.onCardTimeGet(null);
            }
        });


        // byte[] beforeCompress = response.body();
        // byte[] afterCompress = GZipUti.decompressForGzip(beforeCompress);


        // for (String header : response.headers().names()) {
        //     Log.e(TAG, header + ":" + response.headers().get(header));
        // }
        // Log.e(TAG, "解压前:" + beforeCompress.length + "\n" + Arrays.toString(beforeCompress));
        // Log.e(TAG, "解压后:" + afterCompress.length + "\n" + Arrays.toString(afterCompress));


    }
}
