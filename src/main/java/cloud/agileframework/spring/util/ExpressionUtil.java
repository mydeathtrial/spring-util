package cloud.agileframework.spring.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author 佟盟
 * 日期 2021-05-13 14:40
 * 描述 表达式解析器
 * @version 1.0
 * @since 1.0
 */
public class ExpressionUtil {
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final EvaluationContext CONTEXT = new StandardEvaluationContext(BeanUtil.getApplicationContext());


}
