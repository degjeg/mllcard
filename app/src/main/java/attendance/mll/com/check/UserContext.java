package attendance.mll.com.check;

/**
 * Created by Administrator on 2017/5/2.
 */

public class UserContext {
    private String welcome;
    private String username;
    private String password;
    private String session;

    private static UserContext context; // = new UserContext();
    private String uid;

    public static UserContext get() {
        if (context == null) context = new UserContext();

        return context;
    }

    public void logout() {
        context = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        // sessionidadms=78712cd1e62411f750272a55672e20c7;
        int i1 = session.indexOf("sessionidadms=", 0);
        int i2 = session.indexOf(";", 30);

        this.session = session.substring(i1 + "sessionidadms=".length(), i2);
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }
}
