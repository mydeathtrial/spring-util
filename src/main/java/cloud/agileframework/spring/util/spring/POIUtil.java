package cloud.agileframework.spring.util.spring;

import cloud.agileframework.common.util.file.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static cloud.agileframework.spring.util.spring.MultipartFileUtil.getFormat;

/**
 * @author 佟盟 on 2018/10/16
 */
public class POIUtil extends cloud.agileframework.common.util.file.poi.POIUtil {



    /**
     * 读取excel文件成list-map形式
     *
     * @param file excel文件
     * @return 格式化结果
     */
    public static List<LinkedHashMap<String, Object>> readExcel(MultipartFile file) {
        return readExcel(file, null);
    }

    private static Workbook parsing(Object file) {
        Workbook result = null;
        if (file instanceof File) {
            String[] s = ((File) file).getName().split("[.]");
            String suffix;
            if (s.length > 1) {
                suffix = s[s.length - 1];
            } else {
                suffix = Objects.requireNonNull(FileUtil.getFormat((File) file)).toLowerCase();
            }
            try {
                if ("xls".equals(suffix)) {
                    result = new HSSFWorkbook(new FileInputStream((File) file));
                } else {
                    result = new XSSFWorkbook(new FileInputStream((File) file));
                }
            } catch (Exception e) {
                result = null;
            }
        } else if (file instanceof MultipartFile) {
            String[] s = Objects.requireNonNull(((MultipartFile) file).getOriginalFilename()).split("[.]");
            String suffix;
            if (s.length > 1) {
                suffix = s[s.length - 1];
            } else {
                suffix = Objects.requireNonNull(getFormat((MultipartFile) file)).toLowerCase();
            }
            try {
                if ("xls".equals(suffix)) {
                    result = new HSSFWorkbook(((MultipartFile) file).getInputStream());
                } else {
                    result = new XSSFWorkbook(((MultipartFile) file).getInputStream());
                }
            } catch (Exception e) {
                result = null;
            }
        }

        return result;
    }
}
