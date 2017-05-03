package attendance.mll.com.check.http;

/**
 * Created by Administrator on 2017/5/2.
 */

public class LoginReq {
    String username; // =17950&
    String password; // ="123456";
    String template9 = "";
    String finnger10 = "";
    String finnger9 = "";
    String template10 = "";
    String login_type = "pwd";
    String client_language = "zh-cn";

    public LoginReq(String acc, String password) {
        username = acc;
        password = password;
    }
}
