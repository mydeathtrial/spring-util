package cloud.agileframework.spring.util;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.generator.SnowflakeIdWorker;
import cloud.agileframework.spring.properties.ApplicationProperties;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/19 9:32
 * 描述： 主键生成工具
 * @since 1.0
 */
public class IdUtil {
    private IdUtil() {
    }

    private static SnowflakeIdWorker snowflakeIdWorker;

    public static Long generatorId() {
        return getSnowflakeIdWorker().nextId();
    }

    public static String generatorIdToString() {
        return generatorId().toString();
    }

    public static SnowflakeIdWorker getSnowflakeIdWorker() {

        if (snowflakeIdWorker == null) {
            ApplicationProperties properties = BeanUtil.getBean(ApplicationProperties.class);
            if (properties == null) {
                snowflakeIdWorker = new SnowflakeIdWorker(Constant.NumberAbout.ONE, Constant.NumberAbout.ONE);
            } else {
                snowflakeIdWorker = new SnowflakeIdWorker(properties.getWorkerId(), properties.getDataCenterId());
            }
        }
        return snowflakeIdWorker;
    }
}
