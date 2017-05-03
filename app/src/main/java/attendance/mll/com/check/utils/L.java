package attendance.mll.com.check.utils;

import android.os.Environment;
import android.os.Process;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 打印日志的工具类,可以打印到文件,按日期,模块分文件存储
 * Created by danger on 16/8/17.
 */
public class L {

    public static final int KEEP_LOG_IN_DAYS = 7; // 只保留7天的日志,7天外的自动删除,前提是文件名以日期命名
    private static final HashMap<String, L> instances = new HashMap<>();
    private static final L l = new L("default");

    static {
        // 删除过早无用日志
        File logDir = prepareFile(null);
        if (logDir != null) {
            File[] logFiles = logDir.listFiles();
            if (logFiles != null) {


                for (File f : logFiles) {
                    if (System.currentTimeMillis() - f.lastModified() > KEEP_LOG_IN_DAYS * 24L * 3600000) {
                        f.delete();
                    }
                }
            }
        }
    }

    /**
     * %d{MM-dd} 大括号中设置日期格式
     * %L level 日志级别
     * %t 当前线程号
     * %p 当前进程号
     * %T tag
     * %M module
     * %m log content
     */
    String LOG_FILE_NAME_FORMAT = "%M%d{MM-dd}.txt"; // "%T%d{MM-dd}.txt";
    String LOG_CONTENT_FORMAT = "%d{HH:mm:ss} [%L][%T][tid:%t][pid:%p] %m\r\n";
    //    int logcatLevel = BuildConfig.forTest ? Level.DEBUG : Level.CLOSED;
    int logcatLevel = Level.DEBUG;
    //    int fileLevel = BuildConfig.forTest ? Level.DEBUG : Level.CLOSED;
    int fileLevel = Level.DEBUG;
    private String module;
    private String logFileName;
    private File logFile;
    private FileOutputStream fos;


    private L(String module) {
        this.module = module;

        this.logFileName = getFormattedString(LOG_FILE_NAME_FORMAT, module, null, -1, null);
    }

    public static L get() {

        return l;
    }

    public static L get(String module) {
        if (module == null || module.isEmpty()) {
            return l;
        }
        L l = instances.get(module);
        if (l == null) {
            l = new L(module);
            instances.put(module, l);
        }
        return l;
    }

    private static File prepareFile(String name) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File sdcardDir = Environment.getExternalStorageDirectory();
        File logDir = new File(sdcardDir + "/Android/data/" + "attendance.mll.com.check" + "/log");
        if (!logDir.exists()) {
            if (!logDir.mkdirs()) {
                return null;
            }
        }

        if (name == null) {
            return logDir;
        }
        return new File(logDir, name);
    }

    public void v(String msg) {
        if (logcatLevel <= Level.VERBOSE)
            Log.v(module, msg);

        if (fileLevel <= Level.VERBOSE)
            printLogToFile(null, Level.VERBOSE, msg);
    }

    public void v(String tag, String msg) {
        if (logcatLevel <= Level.VERBOSE)
            Log.v(tag, msg);

        if (fileLevel <= Level.VERBOSE)
            printLogToFile(tag, Level.VERBOSE, msg);
    }


    // 下面四个是默认tag的函数
    public void i(String msg) {
        if (logcatLevel <= Level.INFO)
            Log.i(module, msg);

        if (fileLevel <= Level.INFO)
            printLogToFile(null, Level.INFO, msg);
    }

    // 下面是传入自定义tag的函数
    public void i(String tag, String msg) {
        if (logcatLevel <= Level.INFO)
            Log.i(tag, msg);

        if (fileLevel <= Level.INFO)
            printLogToFile(tag, Level.INFO, msg);
    }

    public void d(String msg) {
        if (logcatLevel <= Level.DEBUG)
            Log.d(module, msg);

        if (fileLevel <= Level.DEBUG)
            printLogToFile(null, Level.DEBUG, msg);
    }

    public void d(String tag, String msg) {
        if (logcatLevel <= Level.DEBUG)
            Log.d(tag, msg);

        if (fileLevel <= Level.DEBUG)
            printLogToFile(tag, Level.DEBUG, msg);
    }

    public void w(String msg) {
        if (logcatLevel <= Level.WARN)
            Log.w(module, msg);

        if (fileLevel <= Level.WARN)
            printLogToFile(null, Level.WARN, msg);
    }

    public void w(String tag, String msg) {
        if (logcatLevel <= Level.WARN)
            Log.w(tag, msg);

        if (fileLevel <= Level.WARN)
            printLogToFile(tag, Level.WARN, msg);
    }

    public void e(String msg) {
        if (logcatLevel <= Level.ERROR)
            Log.e(module, msg);

        if (fileLevel <= Level.ERROR)
            printLogToFile(null, Level.ERROR, msg);
    }

    public void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public void e(String tag, String msg, Throwable throwable) {
        if (logcatLevel <= Level.ERROR)
            Log.e(tag, msg, throwable);

        if (fileLevel <= Level.ERROR) {
            printLogToFile(tag, Level.ERROR, msg + "\n" + Log.getStackTraceString(throwable));
        }
    }

    public String getFormattedString(String formatter, String module, String tag, int level, String content) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < formatter.length(); i++) {
            char c = formatter.charAt(i);
            if (c != '%') {
                b.append(c);
                continue;
            }

            char c1 = formatter.charAt(i + 1);
            switch (c1) {
                case 'd'://  %d{MM-dd} 大括号中设置日期格式
                    int end = formatter.indexOf('}', i + 1);
                    if (end != -1) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(formatter.substring(i + 3, end), Locale.ENGLISH);
                        b.append(dateFormat.format(new Date()));
                    }
                    i = end - 1;
                    break;
                case 'L': // %L level 日志级别
                    if (level <= Level.ALL) {
                        b.append("ALL");
                    } else if (level == Level.VERBOSE) {
                        b.append("VERBOSE");
                    } else if (level == Level.DEBUG) {
                        b.append("DEBUG");
                    } else if (level == Level.INFO) {
                        b.append("INFO");
                    } else if (level == Level.ERROR) {
                        b.append("ERROR");
                    } else if (level >= Level.ALL) {
                        b.append("ALL");
                    }
                    break;
                case 't':// %t 当前线程号
                    b.append(Thread.currentThread().getId());
                    break;
                case 'p': // * %p 当前进程号
                    b.append(Process.myPid());
                    break;
                case 'T': // T tag
                    if (tag != null) b.append(tag);
                    break;
                case 'M': //  %M module
                    if (module != null) b.append(module);
                    break;
                case 'm': // * %m log content
                    if (content != null) b.append(content);
                    break;
            }
            i++;
        }

        return b.toString();
    }

    private void printLogToFile(String tag, int level, String content) {

        try {
            if (fos == null) {
                logFile = prepareFile(logFileName);
                if (logFile == null) {
                    return;
                }
                // logFile = new RandomAccessFile(f, "rws");
                // logFile.seek(logFile.length());
                fos = new FileOutputStream(logFile, true);
            }

            String fullContent = getFormattedString(LOG_CONTENT_FORMAT, module, tag, level, content);
            fos.write(fullContent.getBytes("utf-8"));
            fos.flush();

            if (!logFile.exists()) {
                throw new Exception();
            }
            // logFile.write(fullContent.getBytes("utf-8"));
            // logFile.getFD().sync();

        } catch (Exception e) {
            // e.printStackTrace();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    // e1.printStackTrace();
                }
            }
            fos = null;
        }
    }

    private interface Level {
        int ALL = 0;
        int VERBOSE = 1;
        int DEBUG = 2;
        int INFO = 3;
        int WARN = 4;
        int ERROR = 5;
        int CLOSED = 10;
    }
}
