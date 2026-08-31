package com.whaleal.aihub.convert;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用 commons-lang3 反射复制同名属性，替代 Hutool {@code BeanUtil}。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
public final class BeanCopy {

    private BeanCopy() {
    }

    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, false, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        copyProperties(source, target, true, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static <T> T copy(Object source, Class<T> targetType, String... ignoreFields) {
        T target = newInstance(targetType);
        copyProperties(source, target, false, ignoreFields);
        return target;
    }

    public static <T> List<T> copyToList(Collection<?> source, Class<T> targetType) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<T>();
        }
        List<T> result = new ArrayList<T>(source.size());
        for (Object item : source) {
            if (item == null) {
                result.add(null);
                continue;
            }
            result.add(copy(item, targetType));
        }
        return result;
    }

    private static void copyProperties(Object source, Object target, boolean ignoreNull, String... ignoreFields) {
        if (source == null || target == null) {
            return;
        }
        Set<String> ignored = new HashSet<String>();
        if (ignoreFields != null) {
            Collections.addAll(ignored, ignoreFields);
        }
        List<Field> sourceFields = FieldUtils.getAllFieldsList(source.getClass());
        for (Field sourceField : sourceFields) {
            if (shouldSkip(sourceField) || ignored.contains(sourceField.getName())) {
                continue;
            }
            Field targetField = FieldUtils.getField(target.getClass(), sourceField.getName(), true);
            if (targetField == null || shouldSkip(targetField) || Modifier.isFinal(targetField.getModifiers())) {
                continue;
            }
            try {
                Object value = FieldUtils.readField(sourceField, source, true);
                if (ignoreNull && value == null) {
                    continue;
                }
                if (value != null && !isWritable(targetField.getType(), value.getClass())) {
                    continue;
                }
                FieldUtils.writeField(targetField, target, value, true);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot copy field '" + sourceField.getName() + "'", e);
            }
        }
    }

    private static boolean shouldSkip(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || field.isSynthetic();
    }

    private static boolean isWritable(Class<?> targetType, Class<?> valueType) {
        Class<?> resolvedTarget = ClassUtils.primitiveToWrapper(targetType);
        Class<?> resolvedValue = ClassUtils.primitiveToWrapper(valueType);
        return resolvedTarget.isAssignableFrom(resolvedValue);
    }

    private static <T> T newInstance(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Missing no-args constructor for " + type.getName(), e);
        }
    }
}
