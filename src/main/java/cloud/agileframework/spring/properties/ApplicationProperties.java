package cloud.agileframework.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/1/31 9:35
 * 描述： TODO
 * @since 1.0
 */

@ConfigurationProperties(prefix = "agile")
public class ApplicationProperties {
    /**
     * 版本号
     */
    private String version;
    /**
     * 项目标题
     */
    private String title;
    /**
     * 模块标签
     */
    private String moduleName = "sys";
    /**
     * 工作ID (0~31)
     */
    private long workerId = 1;
    /**
     * 数据中心ID (0~31)
     */
    private long dataCenterId = 1;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }
}
