package cloud.agileframework.spring.util;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * 日期 2021-05-19 17:28
 * 描述 动态工具
 * @version 1.0
 * @since 1.0
 */
public class DynamicUtil {
    public static class DynamicBean {
        /**
         * 目标对象
         */
        private final Object target;

        /**
         * 属性集合
         */
        private final BeanMap beanMap;

        public DynamicBean(Class<?> superclass, Map<String, Class<?>> propertyMap) {
            this.target = generateBean(superclass, propertyMap);
            this.beanMap = BeanMap.create(this.target);
        }

        public DynamicBean(Class<?> superclass, String fieldName, Object fieldValue) {
            if (fieldValue == null) {
                throw new IllegalArgumentException();
            }
            HashMap<String, Class<?>> map = Maps.newHashMapWithExpectedSize(1);
            map.put(fieldName, fieldValue.getClass());
            this.target = generateBean(superclass, map);
            this.beanMap = BeanMap.create(this.target);
            setValue(fieldName, fieldValue);
        }


        /**
         * bean 添加属性和值
         *
         * @param property 属性名
         * @param value    属性值
         */
        public void setValue(String property, Object value) {
            beanMap.put(property, value);
        }

        /**
         * 获取属性值
         *
         * @param property 属性
         * @return 属性值
         */
        public Object getValue(String property) {
            return beanMap.get(property);
        }

        /**
         * 获取对象
         *
         * @return 代理对象
         */
        public Object getTarget() {
            return this.target;
        }

        /**
         * 取属性值集
         *
         * @return 属性集
         */
        public BeanMap getBeanMap() {
            return this.beanMap;
        }


        /**
         * 根据属性生成对象
         *
         * @param superclass  被代理的对象
         * @param propertyMap 属性集合
         * @return 代理对象
         */
        private Object generateBean(Class<?> superclass, Map<String, Class<?>> propertyMap) {
            BeanGenerator generator = new BeanGenerator();
            if (null != superclass) {
                generator.setSuperclass(superclass);
            }
            BeanGenerator.addProperties(generator, propertyMap);
            return generator.create();
        }
    }

    /**
     * 生成动态对象
     *
     * @param superclass  被代理对象
     * @param propertyMap 属性集合
     * @return 代理对象
     */
    public static DynamicBean of(Class<?> superclass, Map<String, Class<?>> propertyMap) {
        return new DynamicBean(superclass, propertyMap);
    }

    /**
     * 根据类型和一个属性信息生成动态对象
     *
     * @param superclass 依据对象
     * @param fieldName  属性名
     * @param fieldValue 属性值
     * @return 动态对象
     */
    public static DynamicBean withProperty(Class<?> superclass, String fieldName, Object fieldValue) {
        return new DynamicBean(superclass, fieldName, fieldValue);
    }

    /**
     * 根据类型和属性信息生成动态对象
     *
     * @param superclass  依据对象
     * @param propertyMap 属性集合
     * @return 动态对象
     */
    public static DynamicBean withProperties(Class<?> superclass, Map<String, Object> propertyMap) {
        Map<String, Class<?>> classMap = propertyMap.entrySet()
                .stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, b -> b.getValue().getClass()));
        DynamicBean dynamicBean = of(superclass, classMap);
        for (Map.Entry<String, Object> e : propertyMap.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            dynamicBean.setValue(e.getKey(), e.getValue());
        }
        return dynamicBean;
    }

    /**
     * 根据已有对象和一对属性值生成动态对象
     *
     * @param superObject 已有对象
     * @param fieldName   属性名
     * @param fieldValue  属性值
     * @return 动态对象
     */
    public static DynamicBean withPropertyByObject(Object superObject, String fieldName, Object fieldValue, String... excludeFields) {
        if (superObject == null) {
            return null;
        }

        Map<String, Object> valueMap = ObjectUtil.to(superObject, new TypeReference<Map<String, Object>>() {
        });
        valueMap.put(fieldName, fieldValue);
        for (String excludeField : excludeFields) {
            valueMap.remove(excludeField);
        }
        return withProperties(superObject.getClass(), valueMap);
    }

    /**
     * 根据已有对象生成动态对象
     *
     * @param superObject 已有对象
     * @param propertyMap 设置的属性值集合
     * @return 动态对象
     */
    public static DynamicBean withPropertyByObject(Object superObject, Map<String, Object> propertyMap, String... excludeFields) {
        if (superObject == null) {
            return null;
        }

        Map<String, Object> valueMap = ObjectUtil.to(superObject, new TypeReference<Map<String, Object>>() {
        });
        valueMap.putAll(propertyMap);
        for (String excludeField : excludeFields) {
            valueMap.remove(excludeField);
        }
        return withProperties(superObject.getClass(), valueMap);
    }


}
