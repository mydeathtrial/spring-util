package cloud.agileframework.spring.util;

import com.agile.App;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class AsyncUtilTest {

    @Test
    public void testExecute() {
        AtomicInteger index = new AtomicInteger(0);
        AsyncUtil.execute(() -> {
            Assert.assertEquals(index.get(), 1);
        });
        index.addAndGet(1);
    }

    @Test
    public void testTestExecute() throws InterruptedException {
        AtomicInteger index = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        AsyncUtil.execute(() -> {
            long end = System.currentTimeMillis();
            Assert.assertEquals(index.get(), 1);
            long duration = end - start;
            Assert.assertTrue(duration >= 2000 && duration <= 2500);
        }, Duration.ofSeconds(2));
        index.addAndGet(1);
        Thread.sleep(2500);
    }
}