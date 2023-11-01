package com.bluemsun.util;

public class UriUtil
{
    private static final String localPath = System.getProperty("user.dir") + "/file" + "/upload/";

    private final static String serverPath = "http" + "://" + "8.130.18.189" + ":" + "8081" + "/static/";

    public static String localToServer(String localUri) {
        String fileName = localUri.substring(localUri.indexOf("/upload/") + 8);
        return serverPath + fileName;
    }

    public static String serverToLocal(String serverUri) {
        String fileName = serverUri.substring(serverUri.indexOf("/static/") + 8);
        return localPath + fileName;
    }

    public static String getServerUri(String fileName) {
        return serverPath + fileName;
    }

    public static String getLocalUri(String fileName) {
        return localPath + fileName;
    }
}
