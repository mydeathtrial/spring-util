package cloud.agileframework.spring.util.spring;

import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.MessageUtil;
import com.agile.App;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class BeanUtilTest {

    @Test
    public void getBean() {
        System.out.println(MessageUtil.message("messageKey", "|tudou"));
        ;
        System.out.println(BeanUtil.getApplicationContext().getId());
    }
}