package com.greens1995.myapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by zaratustra on 2017/9/14.
 *原repo地址：https://github.com/ZaratustraN/Check_VirtualAPK
 */
public class CheckVirtual {

    private static final String TAG = "CheckVirtual";

public static boolean isRunInVirtual() {

    String filter = getUidStrFormat();
    if (filter == null || filter.length() == 0){
        return false;
    }

    String result = exec("ps");
    if (result == null || result.isEmpty()) {
        return false;
    }

    String[] lines = result.split("\n");
    if (lines == null || lines.length <= 0) {
        return false;
    }

    int exitDirCount = 0;

    for (int i = 0; i < lines.length; i++) {
        if (lines[i].contains(filter)) {
            int pkgStartIndex = lines[i].lastIndexOf(" ");
            String processName = lines[i].substring(pkgStartIndex <= 0
                    ? 0 : pkgStartIndex + 1, lines[i].length());
            File dataFile = new File(String.format("/data/data/%s",
                    processName, Locale.CHINA));
            if (dataFile.exists()) {
                exitDirCount++;
            }
        }
    }

    return exitDirCount > 1;
}


    private static String exec(String command) {
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("sh");
            bufferedOutputStream = new BufferedOutputStream(process.getOutputStream());

            bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedOutputStream.write(command.getBytes());
            bufferedOutputStream.write('\n');
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

            process.waitFor();

            String outputStr = getStrFromBufferInputSteam(bufferedInputStream);
            return outputStr;
        } catch (Exception e) {
            return null;
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static String getStrFromBufferInputSteam(BufferedInputStream bufferedInputStream) {
        if (null == bufferedInputStream) {
            return "";
        }
        int BUFFER_SIZE = 512;
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder result = new StringBuilder();
        try {
            while (true) {
                int read = bufferedInputStream.read(buffer);
                if (read > 0) {
                    result.append(new String(buffer, 0, read));
                }
                if (read < BUFFER_SIZE) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String getUidStrFormat() {
        String filter = exec("cat /proc/self/cgroup");
        if (filter == null || filter.length() == 0){
            return null;
        }

        int uidStartIndex = filter.lastIndexOf("uid");
        int uidEndIndex = filter.lastIndexOf("/pid");
        if (uidStartIndex < 0) {
            return null;
        }
        if (uidEndIndex<=0){
            uidEndIndex = filter.length();
        }

        filter = filter.substring(uidStartIndex + 4, uidEndIndex);
        try {
            String strUid = filter.replaceAll("\n", "");
            if (isNumericZidai(strUid)){
                int uid = Integer.valueOf(strUid);
                filter = String.format("u0_a%d", uid - 10000);
                return filter;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNumericZidai(String str) {
        if (str == null || str.length() == 0){
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}