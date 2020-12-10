package com.mind.links.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mind.links.common.exception.LinksException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ：lqd
 * @date ：Created in 2020/9/11 0011 14:12
 * description：format params
 * @modified By：
 * @version: $ 1.0
 */
@Component
public class DtoFormat<T> {
    /**
     * create by: lqd
     * description: TODO Conditional dynamic splicing
     * create time: 2020/9/11 0011 14:53
     * params：  * @Param: Object
     *
     * @return QueryWrapper<T>
     */
    public QueryWrapper<T> dtoFormat(Object o) throws Exception {
        try {
            QueryWrapper<T> qWrapper = new QueryWrapper<>();
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(o));
            for (Map.Entry<String, Object> next : jsonObject.entrySet()) {
                String key = next.getKey();
                Object value = next.getValue();
                qWrapper.eq(underscoreValue(key), value);
            }
            return qWrapper;
        } catch (Exception e) {
            throw new LinksException(406, "查询条件异常");
        }
    }

    /**
     * create by: lqd
     * description: TODO  userName -> user_name
     * create time: 2020/9/11 0011 14:52
     * params：  * @Param: key:userName
     *
     * @return user_name
     */
    private String underscoreValue(String value) {
        StringBuilder result = new StringBuilder();
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); i++) {
                String s = value.substring(i, i + 1);
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_").append(s.toLowerCase());
                } else {
                    result.append(s);
                }
            }
        }
        return result.toString();
    }

}
