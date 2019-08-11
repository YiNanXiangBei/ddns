package org.yinan.ddns.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author yinan
 * @date 19-6-10
 */
public class JsonUtil {

    /**
     * json字符串转换aunt成java对象
     * @param json
     * @param typeToken
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T json2object(String json, TypeToken<T> typeToken) {
        Gson gson = new Gson();
        return (T) gson.fromJson(json, typeToken.getType());
    }

    /**
     * java对象转为json对象
     */
    public static String object2json(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T json2object(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, classOfT);
    }
}
