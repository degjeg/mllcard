package attendance.mll.com.check;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);


        String s = "<li class=\"head_welcome_info\">欢迎，员工 邓军.</li>"; // <li class="head_welcome_info">欢迎，员工 邓军.</li>

        Pattern pattern = Pattern.compile("\"head_welcome_info\">([\\S\\s]*)</li>");//(\S*)</li>
        Matcher matcher = pattern.matcher(s); // "  <input type=\"hidden\" id=\"id_self_services\" value=\"13846\"/> ");
        if (matcher.find()) {
            System.out.println("ExampleUnitTest"+ "uid:" + matcher.group(1));
            UserContext.get().setUid(matcher.group(1));
        }
    }
}