package attendance.mll.com.check.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2017/5/2.
 */

public class GZipUti {
    public static final int BUFFERSIZE = 100000;

    /**
     * Gzip解压数据
     *
     * @param gzipStr
     * @return
     */
    public static byte[] decompressForGzip(byte[] compressedData) {
        if (compressedData == null) {
            return null;
        }
        byte[] t = compressedData;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[BUFFERSIZE];
            int n = 0;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            gzip.close();
            in.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
