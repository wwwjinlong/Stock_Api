package top.yueshushu.learn.test;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Http2Test {

    @Test
    public void test1() {
        String fs = "m:90+t:1+f:!50";// 板块
        String fs2 = "b:bk0436+f:!50";// 板块成分股
        System.out.println(fs);
        JSONArray list = dataList(fs2);
        System.out.println(list.size());
    }

    JSONArray dataList(String fs) {
        JSONArray allList = new JSONArray();
        for (int i = 1; i < 100; i++) {

            try {
//                Thread.sleep(1000);
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("np", 1);
//                paramMap.put("fltt", 1);
                paramMap.put("fs", fs);
//                paramMap.put("fields", "f12,f13,f14,f1,f2,f4,f3,f152,f20,f8,f104,f105,f128,f140,f141,f207,f208,f209,f136,f222");
//                paramMap.put("fid", "f3");
                paramMap.put("pn", i);
                paramMap.put("pz", 50);
                paramMap.put("po", 1);
                paramMap.put("dect", 1);
                String response = HttpUtil.createGet("https://push2.eastmoney.com/api/qt/clist/get")
                        .form(paramMap)
                        .header("Accept", "*/*")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                        .header("Cache-Control", "no-cache")
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36")
                        .execute().body();
                System.out.println(i);
                if (!response.contains("diff")) {
                    break;
                }
                JSONObject jsonObject = JSON.parseObject(response);
                JSONArray list = jsonObject.getJSONObject("data").getJSONArray("diff");
                if (CollectionUtils.isEmpty(list)) {
                    break;
                }
                allList.addAll(list);
//                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        return allList;
    }
}
