package cloud.agileframework.spring.sync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟
 * 日期 2021-01-07 11:08
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class SyncSupportConfiguration {
    @Bean
    public RunnerWrapper runnerWrapper() {
        return new RunnerWrapper();
    }
}
