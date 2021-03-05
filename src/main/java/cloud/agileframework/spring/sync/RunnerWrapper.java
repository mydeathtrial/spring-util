package cloud.agileframework.spring.sync;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author 佟盟
 * 日期 2021-01-07 10:47
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class RunnerWrapper {
    @Transactional(rollbackFor = Exception.class)
    public void run(Runner runner) {
        runner.run();
    }
}
