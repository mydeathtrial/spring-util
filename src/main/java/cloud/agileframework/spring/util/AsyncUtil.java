package cloud.agileframework.spring.util;

import cloud.agileframework.spring.sync.AsyncManager;
import cloud.agileframework.spring.sync.Runner;

import java.time.Duration;

/**
 * @author 佟盟
 * 日期 2021-03-15 17:34
 * 描述 异步执行工具
 * @version 1.0
 * @since 1.0
 */
public class AsyncUtil {
    public static void execute(Runner task) {
        AsyncManager.execute(task);
    }

    public static void execute(Runner task, Duration duration) {
        AsyncManager.execute(task, duration);
    }
}
