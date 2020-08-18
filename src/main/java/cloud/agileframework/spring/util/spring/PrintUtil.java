package cloud.agileframework.spring.util.spring;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import java.io.PrintStream;

/**
 * @author 佟盟 on 2018/11/9
 */
public class PrintUtil {

    /**
     * 打印颜色字符串
     *
     * @param color 颜色
     * @param text  内容
     */
    public static void print(AnsiColor color, Object... text) {
        PrintStream out = System.out;
        out.print(AnsiOutput.toString(color, text));
    }

    /**
     * 打印颜色字符串后换行
     *
     * @param text 内容
     */
    public static void println(String text) {
        PrintStream out = System.out;
        out.println(text);
    }

}
