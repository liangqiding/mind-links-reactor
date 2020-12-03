package links.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.*;

/**
 * description : TODO 地址码处理类
 *
 * @author : qiDing
 * date: 2020-11-19 11:22
 * @version v1.0.0
 */
@Component
@Data
@Slf4j
public class FileManage {
    private final Logger logger = LoggerFactory.getLogger(FileManage.class);
    public static JSONArray parseArray;

    static {
        try {
            parseArray = getAreaJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static JSONArray getAreaJson() throws IOException {
        BufferedReader br = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        StringBuilder data = new StringBuilder();
        try {
            File file = new File("src/main/resources/place-code.json");
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String read = null;
            while ((read = br.readLine()) != null) {
                data.append(read);
            }
            parseArray = JSON.parseArray(String.valueOf(data));
        } finally {
            assert br != null;
            br.close();
            isr.close();
            fis.close();
        }
        return parseArray;
    }


}
