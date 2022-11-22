package cloud.agileframework.spring.util;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.collection.IterablesUtil;
import cloud.agileframework.common.util.file.FileUtil;
import cloud.agileframework.common.util.properties.PropertiesUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.spring.exception.CreateFileException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2017/12/21
 */
public class MultipartFileUtil {
    /**
     * 请求中获取文件数据
     *
     * @param request 请求对象
     */
    public static Map<String, Object> getFileFormRequest(HttpServletRequest request) {
        final int length = 16;
        HashMap<String, Object> map = new HashMap<>(length);
        MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (multipartRequest == null) {
            MultipartResolver multipartResolver = BeanUtil.getBean(MultipartResolver.class);
            if (multipartResolver instanceof CommonsMultipartResolver) {
                MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
                multipartRequest = resolver.resolveMultipart(request);
            } else if (multipartResolver instanceof StandardServletMultipartResolver) {
                multipartRequest = new StandardServletMultipartResolver().resolveMultipart(request);
            }
        }
        if (multipartRequest == null) {
            return map;
        }

        Iterator<String> fileNames = multipartRequest.getFileNames();
        while (fileNames.hasNext()) {
            String fileName = fileNames.next();
            map.put(fileName, multipartRequest.getFiles(fileName));
        }
        //增加表单提交的数据
        map.putAll(multipartRequest.getParameterMap());

        if (request instanceof ServletRequestWrapper) {
            MultipartHttpServletRequest childMultipartRequest = WebUtils.getNativeRequest(((ServletRequestWrapper) request).getRequest(), MultipartHttpServletRequest.class);
            if (childMultipartRequest != null) {
                map.putAll(getFileFormRequest(childMultipartRequest));
            }
        }
        return map;
    }


    /**
     * 检验文件格式
     */
    public static boolean checkFileFormat(File file) {
        String format = PropertiesUtil.getProperty("agile.upload.include_format");
        if (StringUtils.isEmpty(format)) {
            return true;
        }
        String[] formats = format.split(Constant.RegularAbout.COMMA, -1);
        return ArrayUtils.contains(formats, FilenameUtils.getExtension(file.getName()));
    }

    /**
     * 文件下载
     *
     * @param file 控制层直接下载文件
     * @return 文件下载响应
     * @throws FileNotFoundException 文件未找到
     */
    public static ResponseEntity<byte[]> downloadFile(File file) throws FileNotFoundException {
        byte[] byteFile;
        try {
            byteFile = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(file.length());
        headers.setContentDispositionFormData(Constant.HeaderAbout.ATTACHMENT, new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        return new ResponseEntity<>(byteFile, headers, HttpStatus.CREATED);
    }

    /**
     * 文件下载
     *
     * @param filePath 控制层直接下载文件
     * @return 文件下载响应
     * @throws FileNotFoundException 文件未找到
     */
    public static ResponseEntity<byte[]> downloadFile(String filePath) throws FileNotFoundException {
        return downloadFile(new File(filePath));
    }

    /**
     * 缓存目录全路径
     */
    private static String tempPath;

    /**
     * 获取缓存目录全路径
     *
     * @return 缓存目录路径
     */
    public static String getTempPath() {
        if (!StringUtil.isBlank(tempPath)) {
            return tempPath;
        }
        String filePath;
        MultipartProperties properties = BeanUtil.getBean(MultipartProperties.class);
        if (properties != null && properties.getLocation() != null) {
            filePath = properties.getLocation();
        } else {
            filePath = MultipartFileUtil.class.getResource("").getPath();
        }

        File file = new File(filePath);

        if (!file.exists()) {
            boolean is = file.mkdirs();
            if (!is) {
                throw new RuntimeException("缓存目录无法创建");
            }
        }
        try {
            tempPath = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!tempPath.endsWith(File.separator)) {
            tempPath += File.separator;
        }
        return tempPath;
    }

    public static List<String> uploadFile(Collection<MultipartFile> files, String dirName) {
        return uploadFile(files, dirName, null);
    }

    /**
     * 文件上传
     *
     * @param files   文件集合
     * @param dirName 父级文件目录名
     * @return 文件存储的相对目录集合
     */
    public static List<String> uploadFile(Collection<MultipartFile> files, String dirName, String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return files.stream().map(file -> {
                try {
                    return uploadFile(file, dirName);
                } catch (CreateFileException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            return IterablesUtil.map(Constant.NumberAbout.ONE, files, (index, file) -> {
                try {
                    return uploadFile(file, dirName, String.format("%s(%s)", fileName, index));
                } catch (CreateFileException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件存储的绝对路径
     * @throws CreateFileException 异常
     * @throws IOException         无法生成文件异常
     */
    public static String uploadFile(MultipartFile file) throws CreateFileException, IOException {
        return uploadFile(file, null);
    }

    public static String uploadFile(MultipartFile file, String dirName) throws CreateFileException, IOException {
        return uploadFile(file, dirName, null);
    }

    /**
     * 文件上传
     *
     * @param file    文件
     * @param dirName 父级文件目录名
     * @return 文件存储的绝对路径
     * @throws CreateFileException 异常
     * @throws IOException         无法生成文件异常
     */
    public static String uploadFile(MultipartFile file, String dirName, String fileName) throws CreateFileException, IOException {
        String dirPath;

        dirName = StringUtils.isEmpty(dirName) ? "" : dirName;
        if (FileUtil.isIllegalDirName(dirName)) {
            throw new RuntimeException("非法目录结构，存在被攻击威胁");
        } else {
            dirPath = MultipartFileUtil.getTempPath() + dirName;
        }

        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new CreateFileException(dir.getAbsolutePath());
        }

        try {
            dirPath = dir.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }

        fileName = StringUtils.isEmpty(fileName) ? file.getOriginalFilename() : fileName;
        String absoluteFileName = dirPath + fileName;
        File uploadFile = new File(absoluteFileName);
        if (!uploadFile.exists() && !uploadFile.createNewFile()) {
                throw new CreateFileException(absoluteFileName);
        }
        file.transferTo(uploadFile);
        return absoluteFileName;
    }

    /**
     * 取文件
     *
     * @param fileName 文件名
     * @return 文件
     * @throws FileNotFoundException 文件没找到
     */
    public static File getFile(String fileName) throws FileNotFoundException {
        File file;
        if (fileName.startsWith(File.separator)) {
            file = new File(fileName);
        } else {
            file = new File(MultipartFileUtil.getTempPath() + fileName);
        }
        if (file.exists()) {
            return file;
        }
        throw new FileNotFoundException(file.getAbsolutePath());
    }

    /**
     * 绝对路径转相对路径
     *
     * @param absolutelyPath 绝对路径
     * @return 针对缓存目录的相对路径
     */
    public static String toRelativePath(String absolutelyPath) {
        return absolutelyPath.replace(getTempPath(), "").replace(File.separator, "/");
    }
}
