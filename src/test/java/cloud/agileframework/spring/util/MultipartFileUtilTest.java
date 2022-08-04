package cloud.agileframework.spring.util;

import com.agile.App;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class MultipartFileUtilTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print())
                .alwaysExpect(status().isOk())
                .build();  //构造MockMvc
    }

    @Test
    public void testGetFormat() {
        String a = MultipartFileUtil.getFormat(new MockMultipartFile("a.TXT", new byte[]{}));
        Assert.assertEquals("txt", a);

        String b = MultipartFileUtil.getFormat(new MockMultipartFile("a.doc", new byte[]{}));
        Assert.assertEquals("doc", b);
    }

    @Test
    public void testGetFileFormRequest() throws Exception {
        MvcResult result = mockMvc.perform(fileUpload("/test")
                        .file("x.txt", new byte[]{}))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> map = MultipartFileUtil.getFileFormRequest(result.getRequest());
        Assert.assertTrue(((List<?>) map.get("x.txt")).get(0) instanceof MultipartFile);
    }

    @Test
    public void testCheckFileFormat() {
        MultipartFileUtil.checkFileFormat(new File("../a.txt"));
    }

    @Test
    public void testDownloadFile() throws FileNotFoundException {
//		MultipartFileUtil.downloadFile(new File("../a.txt"));
    }

    public void testTestDownloadFile() {
    }

    public void testTestDownloadFile1() {
    }

    public void testDownloadZip() {
    }

    public void testGetTempPath() {
    }

    public void testUploadFile() {
    }

    public void testTestUploadFile() {
    }

    public void testTestUploadFile1() {
    }

    public void testTestUploadFile2() {
    }

    public void testTestUploadFile3() {
    }

    public void testGetFile() {
    }

    public void testToRelativePath() {
    }
}