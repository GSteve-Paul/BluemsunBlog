package com.bluemsun.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MD5Util
{
    public static String encryMD5(String data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, md5.digest()).toString(16);
        } catch (Exception ex) {
            return null;
        }
    }
}
