package cloud.agileframework.spring.util;

import com.agile.App;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.MessageFormat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class MessageUtilTest {

    @Test
    public void testMessage() {
        final String replaceValue = "|tudou";
        String message = MessageUtil.message("messageKey", null, replaceValue);
        Assert.assertEquals(message, MessageFormat.format(PropertiesUtil.getProperty("messageKey"), replaceValue));
    }

    @Test
    public void testTestMessage() {
        final String defaultValue = "hello {0}";
        final String params = "tudou";
        String message = MessageUtil.message("dsa", defaultValue, params);
        Assert.assertEquals(message, MessageFormat.format(defaultValue, params));
    }

    @Test
    public void testMessageRequire() {
        final String defaultValue = "hello {0}";
        final String params = "tudou";
        String message = MessageUtil.messageRequire("dsa", defaultValue, params);
        Assert.assertEquals(message, MessageFormat.format(defaultValue, params));
    }
}