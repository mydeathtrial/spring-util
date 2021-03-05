package cloud.agileframework.spring.sync;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 佟盟
 * 日期 2020/5/28 15:36
 * 描述 日志线程工厂
 * @version 1.0
 * @since 1.0
 */
public class ThreadFactory implements java.util.concurrent.ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ThreadFactory(String poolName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = String.format("pool-%s-%s-", POOL_NUMBER.getAndIncrement(), poolName);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        // 设置非守护线程
        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        // 设置线程优先级
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
