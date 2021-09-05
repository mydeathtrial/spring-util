package cloud.agileframework.spring.util;

import cloud.agileframework.spring.util.spring.O1;
import com.agile.App;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class DynamicUtilTest {

	@Test
	public void testOf() {
		Map<String, Class<?>> map = Maps.newHashMap();
		map.put("p1",String.class);
		map.put("p2",Integer.class);
		DynamicUtil.DynamicBean clazz = DynamicUtil.of(O1.class, map);
		clazz.setValue("p1","111");

		Assert.assertThrows(ClassCastException.class,()-> clazz.setValue("p2","222"));

		clazz.setValue("p2",222);
		Assert.assertEquals(clazz.getValue("p1"),"111");
		Assert.assertEquals(clazz.getValue("p2"),222);
	}

	@Test
	public void testWithProperty() {
		DynamicUtil.DynamicBean o = DynamicUtil.withProperty(O1.class, "p1", 111);
		Assert.assertEquals(o.getValue("p1"),111);

		Assert.assertThrows(ClassCastException.class,()->{
			DynamicUtil.withProperty(O1.class, "name", 111);
		});

		DynamicUtil.DynamicBean o2 = DynamicUtil.withProperty(O1.class, "name", "111");
		Assert.assertEquals(o2.getValue("name"),"111");
	}

	@Test
	public void testWithProperties() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("p1","111");
		map.put("p2",222);
		DynamicUtil.DynamicBean o = DynamicUtil.withProperties(O1.class, map,"p1");
		Assert.assertNull(o.getValue("p1"));
		Assert.assertEquals(o.getValue("p2"),222);
	}

	@Test
	public void testWithPropertyByObject() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("p1","111");
		map.put("p2",222);
		DynamicUtil.DynamicBean o = DynamicUtil.withPropertyByObject(new O1("1",2, Lists.newArrayList()), map,"p1","name");
		Assert.assertNull(o.getValue("p1"));
		Assert.assertNull(o.getValue("name"));
		Assert.assertEquals(o.getValue("p2"),222);
	}
	@Test
	public void testTestWithPropertyByObject() {
		DynamicUtil.DynamicBean o = DynamicUtil.withPropertyByObject(new O1("1",2, Lists.newArrayList()), "p1","111","name");
		Assert.assertEquals(o.getValue("p1"),"111");
		Assert.assertNull(o.getValue("name"));
	}
}