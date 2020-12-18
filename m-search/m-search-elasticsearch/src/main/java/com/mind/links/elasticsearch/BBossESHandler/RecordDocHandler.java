package com.mind.links.elasticsearch.BBossESHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mind.links.common.entity.ekl.RecordEs;
import lombok.extern.slf4j.Slf4j;
import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-11-12 10:03
 * @version v1.0.0
 */
@Component
@Slf4j
public class RecordDocHandler {
    @Autowired
    private BBossESStarter esStarter;

    /**
     * mapper 路径
     */
    private final String mapperPath = "es/mapper/record.xml";

    /**
     * 索引表
     */
    private final String table = "device_message_record";

    public void createRecordIndex() {
        //创建加载配置文件的客户端工具，单实例多线程安全
        ClientInterface clientUtil = esStarter.getConfigRestClient(mapperPath);
        try {
            //判读索引表device是否存在，存在返回true，不存在返回false
            boolean exist = clientUtil.existIndice(table);

            //如果索引表device已经存在先删除mapping
            if (exist) {
                String r = clientUtil.dropIndice(table);
                log.info("===clientUtil.dropIndex(\"demo\") response:" + r);
                //获取最新建立的索引表结构
                String demoIndex = clientUtil.getIndice(table);
                log.info("===获取最新建立的索引表结构==" + demoIndex);
            }
            //创建索引表device (索引表名称,索引表mapping dsl脚本名称，在es/mapper/xxx.xml中定义xxx)
            clientUtil.createIndiceMapping(table,
                    "createRecordIndex");
            //获取最新建立的索引表结构
            String demoIndex = clientUtil.getIndice(table);
            log.info("===获取最新建立的索引表结构==" + demoIndex);
        } catch (ElasticSearchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void addAndUpdateRecord(RecordEs recordEs) throws ParseException {
        //创建创建/修改/获取/删除文档的客户端对象，单实例多线程安全
        ClientInterface clientUtil = esStarter.getRestClient();

        //向固定index demo添加或者修改文档,如果demoId已经存在做修改操作，否则做添加文档操作，返回处理结果
        /**
         //通过@ESId注解的字段值设置文档id
         String response = clientUtil.addDocument("demo"//索引表

         demo);
         */

        /**
         //直接指定文档id
         String response = clientUtil.addDocumentWithId("demo",//索引表
         demo,2l);
         */

        //强制刷新
        ClientOptions addOptions = new ClientOptions();

        addOptions
//				.setEsRetryOnConflict(1) // elasticsearch不能同时指定EsRetryOnConflict和version
                .setIdField("msgId")
//				.setVersion(2).setVersionType("internal")  //使用IfPrimaryTerm和IfSeqNo代替version
//				.setIfPrimaryTerm(1l)
//				.setIfSeqNo(13l)
//				.setPipeline("1")
                .setTimeout("100s")
                .setWaitForActiveShards(1)
                .setRefresh("true")
                .setRouting(1);
        //      .setMasterTimeout("10s");

        String response = clientUtil.addDocument(table,
                recordEs, addOptions);
        log.info("===打印结果：addDocument===\n" + response);

    }


    /**
     * 检索文档
     *
     * @throws ParseException e
     */
    public String search(RecordEs recordEss, Integer pageNumber, Integer pageSize) throws ParseException {
        //创建加载配置文件的客户端工具，用来检索文档，单实例多线程安全
        ClientInterface clientUtil = esStarter.getConfigRestClient(mapperPath);
        //设定查询条件,通过map传递变量参数值,key对于dsl中的变量名称
        Map<String, Object> params = new HashMap<String, Object>();
        //设置applicationName1和applicationName2两个变量的的精确值
        JSONObject ps = JSON.parseObject(JSON.toJSONString(recordEss));
        for (Map.Entry<String, Object> p : ps.entrySet()) {
            params.put(p.getKey(), p.getValue());
        }
        // 是否开始查询效率统计
        params.put("explain", false);

        params.put("from", pageNumber);
        params.put("size", pageSize);
        params.put("startTime", System.currentTimeMillis() - 1005160376159L);
        params.put("endTime", System.currentTimeMillis());
        params.put("pageSize", 1000);
        String s = table + "/_search";
        long count = clientUtil.count(table, "count",
                params);
        log.info("===count===" + count);

        String search = clientUtil.executeRequest(s,
                "search",
                params);

        log.info("===totalSize===" + search);
        return search;
    }
}
