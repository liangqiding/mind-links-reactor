package com.mind.links.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 上传文件校验工具类
 *
 * @author qiding
 */
public class LinksFileUtil {

    /**
     * @date ：Created in 2021/01/06 08:57
     * description： 密码提花 （byte 取高四位->再 & 运算过滤负数 -> HEX_CODE.size >=15）
     */
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();


    /**
     * @date ：Created in 2021/01/06 08:57
     * description：TODO 文件类取MD5
     */
    public static String calcMd5(File file) {
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            return calcMd5(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @date ：Created in 2021/01/06 08:57
     * description：TODO  输入流取MD5
     */
    public static String calcMd5(InputStream stream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(HEX_CODE[(b >> 4) & 0xF]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }
}