package com.agile;

import cloud.agileframework.spring.util.MappingUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author 佟盟
 * 日期 2021/9/5 16:08
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Controller
public class TestController {
	@RequestMapping("/test")
	public String test() {
		return "Hello world";
	}
}
