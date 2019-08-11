package org.yinan.ddns.common.util;

import io.netty.util.internal.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author yinan
 * @date 19-8-11
 */
public class BeanUtil {
    @SuppressWarnings("unchecked")
    public static Method getGetMethod(Class objectClass, String fieldName) {
        StringBuffer sb = new StringBuffer();
        sb.append("get");
        sb.append(fieldName.substring(0, 1).toUpperCase());
        sb.append(fieldName.substring(1));
        try {
            return objectClass.getMethod(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException("get bean method failed");
        }
    }

    /**
     * 获取当前对象【fieldName】属性的值
     * @param obj 对象
     * @param fieldName 属性名
     * @return
     * @throws Exception
     */
    public static Object getValue(Object obj, String fieldName) throws Exception{
        if (obj == null || StringUtil.isNullOrEmpty(fieldName)) {
            return null;
        }
        // 获取对象的属性
        Field field = obj.getClass().getDeclaredField(fieldName);
        // 对象的属性的访问权限设置为可访问
        field.setAccessible(true);
        // 返回此属性的值
        return field.get(obj);
    }
}
