package attendance.mll.com.check.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/5/2.
 */

public class T {
    static Toast toast;

    static Context context;

    public static void init(Context context) {
        T.context = context;
    }

    public static void show(String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
