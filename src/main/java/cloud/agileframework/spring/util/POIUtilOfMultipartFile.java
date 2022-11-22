package cloud.agileframework.spring.util;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.file.poi.CellInfo;
import cloud.agileframework.common.util.file.poi.ExcelFormatException;
import cloud.agileframework.common.util.file.poi.POIUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 佟盟 on 2018/10/16
 */
public class POIUtilOfMultipartFile extends POIUtil {
    private static final Logger log = LoggerFactory.getLogger(POIUtilOfMultipartFile.class);


    /**
     * 读取excel文件成list-map形式
     *
     * @param file excel文件
     * @return 格式化结果
     */
    public static List<Map<String, Object>> readExcel(MultipartFile file) throws ExcelFormatException {
        return readExcel(file, null);
    }


    /**
     * 读取excel文件成list-map形式，并且map-key值对应columns内容
     *
     * @param file    文件
     * @param columns map-key对应字段
     * @return 格式化结果
     */
    public static List<Map<String, Object>> readExcel(MultipartFile file, List<CellInfo> columns) throws ExcelFormatException {
        Workbook excel = readFile(file);
        return readExcel(new TypeReference<Map<String, Object>>() {
        }, columns, excel);
    }

    /**
     * 读取excel文件成list-map形式，并且map-key值对应columns内容
     *
     * @param file    文件
     * @param columns map-key对应字段
     * @return 格式化结果
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz, List<CellInfo> columns) throws ExcelFormatException {
        Workbook excel = readFile(file);
        return readExcel(new TypeReference<>(clazz), columns, excel);
    }

    public static Workbook readFile(MultipartFile file) {
        Workbook result = null;
        final String xls = "xls";
        if (file != null) {
            String[] s = Objects.requireNonNull((file).getOriginalFilename()).split("[.]");
            String suffix;
            if (s.length > 1) {
                suffix = s[s.length - 1];
            } else {
                suffix = Objects.requireNonNull(FilenameUtils.getExtension(file.getName())).toLowerCase();
            }
            try {
                if (xls.equals(suffix)) {
                    result = new HSSFWorkbook((file).getInputStream());
                } else {
                    result = new XSSFWorkbook((file).getInputStream());
                }
            } catch (Exception e) {
                log.error("excel文件解析发生异常", e);
            }
        }

        return result;
    }
}
