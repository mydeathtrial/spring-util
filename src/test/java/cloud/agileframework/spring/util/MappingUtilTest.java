package cloud.agileframework.spring.util;

import com.agile.App;
import com.agile.TestController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class MappingUtilTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print())
                .alwaysExpect(status().isOk())
                .build();  //构造MockMvc
    }

    @Test
    public void testMatching() throws Exception {
        MvcResult result = mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andReturn();

        Method method = MappingUtil.matching(result.getRequest()).getMethod();
        Assert.assertEquals(method, TestController.class.getMethod("test"));
    }
}