package edu.sysu.citystorymap.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
    public static byte[] inputStreamToBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        try {
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public static String inputStreamToString(InputStream in ) {
        byte[] temp = inputStreamToBytes(in);
        return new String(temp);
    }
}
