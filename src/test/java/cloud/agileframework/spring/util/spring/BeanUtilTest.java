package cloud.agileframework.spring.util.spring;

import cloud.agileframework.common.util.object.DifferentCollectionField;
import cloud.agileframework.common.util.object.DifferentField;
import cloud.agileframework.common.util.object.DifferentSimpleField;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.spring.config.MessageResourceAutoConfiguration;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.DynamicUtil;
import cloud.agileframework.spring.util.MessageUtil;
import cloud.agileframework.spring.util.PropertiesUtil;
import com.agile.App;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class BeanUtilTest {
	@Test
	public void testGetBeanClass(){
		Class<?> clazz = BeanUtil.getBeanClass(MessageResourceAutoConfiguration.class);
		Assert.assertEquals(clazz.getCanonicalName(),clazz.getCanonicalName());
	}

	@Test
	public void testProperties() {
		//验证spring读取配置文件
		String id = BeanUtil.getApplicationContext().getId();
		Assert.assertEquals(id, PropertiesUtil.getProperty("spring.application.name"));
	}


	@Test
	public void compare() {
		List<DifferentField> dif = ObjectUtil.getDifferenceProperties(new O1("tudou", 12, Lists.newArrayList("李磊", "张娜拉")),
				new O1("tudou1", 11, Lists.newArrayList("李磊", "张天爱")));

		List<BeanMap> dif2 = dif.stream().map(BeanUtilTest::of).collect(Collectors.toList());

		String successText = "[{\"newValue\":\"tudou1\",\"fieldName\":\"name\",\"fieldRemark\":\"姓名\",\"valueDisplayType\":\"valueChange\",\"oldValue\":\"tudou\",\"fieldType\":\"java.lang.String\"}," +
				"{\"newValue\":11,\"fieldName\":\"age\",\"fieldRemark\":\"age\",\"valueDisplayType\":\"valueChange\",\"oldValue\":12,\"fieldType\":\"int\"}," +
				"{\"add\":[\"张天爱\"],\"fieldName\":\"friends\",\"fieldRemark\":\"friends\",\"del\":[\"张娜拉\"],\"valueDisplayType\":\"listChange\",\"fieldType\":\"java.util.List\"}]";
		Assert.assertEquals(JSON.parse(successText), JSON.toJSON(dif2));
	}

	/**
	 * 根据不同的差异类型，在差异信息中加入新的属性-valueDisplayType
	 *
	 * @param differentField 属性差异信息
	 * @param <A>            泛型
	 * @return 在differentField中增加了属性valueDisplayType后形成的动态对象
	 */
	public static <A extends DifferentField> BeanMap of(A differentField) {
		BeanMap map = null;
		if (differentField instanceof DifferentCollectionField) {
			map = DynamicUtil.withPropertyByObject(differentField, "valueDisplayType", ValueDisplayType.listChange, "EQUAL_FIELD").getBeanMap();
		} else if (differentField instanceof DifferentSimpleField) {
			map = DynamicUtil.withPropertyByObject(differentField, "valueDisplayType", ValueDisplayType.valueChange, "EQUAL_FIELD").getBeanMap();
		}
		return map;
	}

	/**
	 * 属性差异类型
	 */
	public enum ValueDisplayType {
		text, label, listChange, valueChange
	}
}