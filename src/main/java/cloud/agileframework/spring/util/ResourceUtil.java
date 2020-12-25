package cloud.agileframework.spring.util;

import cloud.agileframework.common.constant.Constant;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 描述：
 * <p>创建时间：2019/1/10<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class ResourceUtil {
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * URL prefix for loading from the file system: "file:".
     */
    public static final String FILE_URL_PREFIX = "file:";

    /**
     * URL prefix for loading from a jar file: "jar:".
     */
    public static final String JAR_URL_PREFIX = "jar:";

    /**
     * URL prefix for loading from a war file on Tomcat: "war:".
     */
    public static final String WAR_URL_PREFIX = "war:";

    /**
     * URL protocol for a file in the file system: "file".
     */
    public static final String URL_PROTOCOL_FILE = "file";

    /**
     * URL protocol for an entry from a jar file: "jar".
     */
    public static final String URL_PROTOCOL_JAR = "jar";

    /**
     * URL protocol for an entry from a war file: "war".
     */
    public static final String URL_PROTOCOL_WAR = "war";

    /**
     * URL protocol for an entry from a zip file: "zip".
     */
    public static final String URL_PROTOCOL_ZIP = "zip";

    /**
     * URL protocol for an entry from a WebSphere jar file: "wsjar".
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";

    /**
     * URL protocol for an entry from a JBoss jar file: "vfszip".
     */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";

    /**
     * URL protocol for a JBoss file system resource: "vfsfile".
     */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

    /**
     * URL protocol for a general JBoss VFS resource: "vfs".
     */
    public static final String URL_PROTOCOL_VFS = "vfs";

    /**
     * File extension for a regular jar file: ".jar".
     */
    public static final String JAR_FILE_EXTENSION = ".jar";

    /**
     * Separator between JAR URL and file path within the JAR: "!/".
     */
    public static final String JAR_URL_SEPARATOR = "!/";

    /**
     * Special separator between WAR URL and jar part on Tomcat.
     */
    public static final String WAR_URL_SEPARATOR = "*/";

    private static Class forName(String entryPath) {
        String name = entryPath.replace("/", ".").replace(".class", "");
        Class result;
        try {
            result = Class.forName(name);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    private static JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException ex) {
                return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }


    public static void findJarFilePaths(URL rootDirURL, String extension, Set<String> fileResourcePaths)
            throws IOException {

        URLConnection con = rootDirURL.openConnection();
        JarFile jarFile;
        String jarFileUrl;

        if (con instanceof JarURLConnection) {
            JarURLConnection jarCon = (JarURLConnection) con;
            jarFile = jarCon.getJarFile();
        } else {
            String urlFile = rootDirURL.getFile();
            int separatorIndex = urlFile.indexOf(WAR_URL_SEPARATOR);
            if (separatorIndex == -1) {
                separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
            }
            if (separatorIndex != -1) {
                jarFileUrl = urlFile.substring(0, separatorIndex);
                jarFile = getJarFile(jarFileUrl);
            } else {
                jarFile = new JarFile(urlFile);
            }
        }

        try {

            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.endsWith(extension)) {
                    fileResourcePaths.add(entryPath);
                }
            }
        } finally {
            IOUtils.closeQuietly(jarFile);
        }
    }

//    public static void main(String[] args) {
//        getClassFromPackage("com.alibaba", true);
//    }

    /**
     * 在package对应的路径下找到所有的class
     */
    private static void findClassInPackageByFile(String filePath, String extension, Set<String> classes) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(file -> {
            boolean acceptDir = file.isDirectory();
            boolean acceptClass = file.getName().endsWith(extension);
            return acceptDir || acceptClass;
        });

        if (dirFiles == null || dirFiles.length == 0) {
            return;
        }

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(file.getAbsolutePath(), extension, classes);
            } else {
                String reourcePath = file.getAbsolutePath().replace("\\", "/").replace(Thread.currentThread().getContextClassLoader().getResource("").getPath().replaceFirst("/", ""), "");
                try {
                    classes.add(reourcePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 转为Uri
     *
     * @param location 地址
     * @return uri
     * @throws URISyntaxException 异常
     */
    public static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }

    /**
     * 获得包下面的所有的class
     *
     * @param recursive 是否遍历子包
     * @return 包下所有类
     */
    public static Set<Class<?>> getClassFromPackage(String packageName, boolean recursive) {
        Set<Class<?>> classes = new HashSet<>();
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {

                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                Set<String> paths = new HashSet<>();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassInPackageByFile(filePath, ".class", paths);
                } else if ("jar".equals(protocol)) {
                    findJarFilePaths(url, ".class", paths);
                }
                for (String path : paths) {
                    Class clazz = forName(path);
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 检索所有指定name的配置文件
     *
     * @param name
     * @return
     */
    public static Resource[] getResources(String name, String extension) {
        ClassLoader classLoader = ResourceUtil.class.getClassLoader();
        String target = name.replace('.', '/');
        try {
            return new PathMatchingResourcePatternResolver(classLoader)
                    .getResources("classpath*:" + target + "." + extension);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getClassPath(Resource resource) throws IOException {
        String classesPath = ResourceUtil.class.getResource(Constant.RegularAbout.SLASH).getPath();
        classesPath = URLDecoder.decode(classesPath, "utf-8");
        String path = URLDecoder.decode(resource.getURL().getPath(), "utf-8");
        if (path.contains(classesPath)) {
            return path.replaceFirst(classesPath, Constant.RegularAbout.BLANK);
        }
        path = path.replaceFirst("classes", "test-classes");
        if (path.contains(classesPath)) {
            return path.replaceFirst(classesPath, Constant.RegularAbout.BLANK);
        }
        return null;
    }

}
