package cloud.agileframework.spring.util.spring;

import cloud.agileframework.common.util.json.JSONUtil;
import cloud.agileframework.common.util.object.DifferentCollectionField;
import cloud.agileframework.common.util.object.DifferentField;
import cloud.agileframework.common.util.object.DifferentSimpleField;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.DynamicUtil;
import cloud.agileframework.spring.util.MessageUtil;
import com.agile.App;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class BeanUtilTest {

    @Test
    public void getBean() {
        System.out.println(MessageUtil.message("messageKey", null, "|tudou"));
        System.out.println(BeanUtil.getApplicationContext().getId());
    }

    @Test
    public void compare() {
        List<DifferentField> dif = ObjectUtil.getDifferenceProperties(new O1("tudou", 12, Lists.newArrayList("李磊", "张娜拉")),
                new O1("tudou1", 11, Lists.newArrayList("李磊", "张天爱")));

        List<BeanMap> dif2 = dif.stream().map(BeanUtilTest::of).collect(Collectors.toList());
        System.out.println(JSONUtil.toJSONString(dif2, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
    }

    public static <A extends DifferentField> BeanMap of(A differentField) {
        BeanMap map = null;
        if (differentField instanceof DifferentCollectionField) {
            map = DynamicUtil.withPropertyByObject(differentField, "valueDisplayType", ValueDisplayType.listChange,"EQUAL_FIELD").getBeanMap();
        } else if (differentField instanceof DifferentSimpleField) {
            map = DynamicUtil.withPropertyByObject(differentField, "valueDisplayType", ValueDisplayType.valueChange,"EQUAL_FIELD").getBeanMap();
        }
        return map;
    }

    public enum ValueDisplayType {
        text, label, listChange, valueChange
    }
}