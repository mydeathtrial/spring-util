package cloud.agileframework.spring.util;

import com.agile.App;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class IdUtilTest {
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 2, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Test
    public void testGeneratorId() {
        Set<Long> set = Sets.newConcurrentHashSet();
        IntStream.range(0, 10000).parallel().forEach((index) -> {
            set.add(IdUtil.generatorId());
        });
        Assert.assertEquals(set.size(), 10000);
    }

    @Test
    public void testGeneratorIdToString() {
        Set<String> set = Sets.newConcurrentHashSet();
        IntStream.range(0, 10000).parallel().forEach((index) -> {
            set.add(IdUtil.generatorIdToString());
        });
        Assert.assertEquals(set.size(), 10000);
    }
}