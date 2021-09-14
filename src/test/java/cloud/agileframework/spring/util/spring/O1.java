package cloud.agileframework.spring.util.spring;

import cloud.agileframework.common.annotation.Remark;

import java.util.List;

/**
 * @author 佟盟
 * 日期 2021-05-18 15:58
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class O1 {
	@Remark(value = "姓名", ignoreCompare = false)
	private String name;
	private int age;
	private List<String> friends;

	public O1() {
	}

	public O1(String name, int age, List<String> friends) {
		this.name = name;
		this.age = age;
		this.friends = friends;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<String> getFriends() {
		return friends;
	}

	public void setFriends(List<String> friends) {
		this.friends = friends;
	}
}
