package cloud.agileframework.spring.sync;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.spring.util.BeanUtil;

import java.time.Duration;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 *
 * @author mydeathtrial
 */
public class AsyncManager {
    /**
     * 操作延迟10毫秒
     */
    private static final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池
     */
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactory("延时队列"));
    private final ThreadPoolExecutor executor2 = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,Integer.MAX_VALUE,3,TimeUnit.MINUTES, new LinkedBlockingQueue<>(),new ThreadFactory("阻塞队列"));

    /**
     * 单例模式
     */
    private AsyncManager() {
    }

    private static volatile AsyncManager single;

    private static AsyncManager getSingle() {
        if (single != null) {
            return single;
        }

        synchronized (AsyncManager.class) {
            if (single != null) {
                return single;
            }
            single = new AsyncManager();
            return single;
        }
    }

    /**
     * 处理事务用的
     */
    private static final RunnerWrapper RUNNER_WRAPPER = BeanUtil.getBean(RunnerWrapper.class);

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public static void execute(Runner task) {
        getSingle().executor2.execute(() -> RUNNER_WRAPPER.run(task));
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public static void execute(Runner task, Duration duration) {
        getSingle().executor.schedule(new TimerTask() {
            @Override
            public void run() {
                RUNNER_WRAPPER.run(task);
            }
        }, duration.toMillis(), TimeUnit.MILLISECONDS);
    }


    /**
     * 停止任务线程池
     */
    public static void shutdown() {
        if (!getSingle().executor.isShutdown()) {
            getSingle().executor.shutdown();

            try {
                if (!getSingle().executor.awaitTermination(Constant.NumberAbout.TWO, TimeUnit.MINUTES)) {
                    getSingle().executor.shutdownNow();
                }
            } catch (InterruptedException var2) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
