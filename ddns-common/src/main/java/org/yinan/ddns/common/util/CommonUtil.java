package org.yinan.ddns.common.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author yinan
 * @date 19-8-11
 */
public class CommonUtil {
    @SafeVarargs
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {

            totalLength += array.length;

        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }


    /**
     * 为字符串添加日期后缀名
     */
    public static String[] appendDate(String[] fileNames) {
        String[] newFileNames = new String[fileNames.length];
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        String dateString = "_" + format.format(new Date());
        for (int i = 0; i < fileNames.length; i++) {
            newFileNames[i] = fileNames[i] + dateString;
        }
        return newFileNames;
    }

    public static String appendDate(String fileName) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        fileName = fileName + "_" + format.format(new Date());
        return fileName;
    }
}
