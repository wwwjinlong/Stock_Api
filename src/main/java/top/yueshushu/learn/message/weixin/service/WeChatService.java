package top.yueshushu.learn.message.weixin.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.yueshushu.learn.common.Const;
import top.yueshushu.learn.message.weixin.model.TextMessage;
import top.yueshushu.learn.message.weixin.model.WxText;
import top.yueshushu.learn.message.weixin.properties.DefaultWXProperties;
import top.yueshushu.learn.message.weixin.util.WeChatUtil;
import top.yueshushu.learn.util.RedisUtil;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description 微信企业号发送消息
 * @Author yuejianli
 * @Date 2022/6/4 16:26
 **/
@Component
@Slf4j
public class WeChatService {
    private static String sendMessage_url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
    @Resource
    private DefaultWXProperties defaultWXProperties;
    @Resource
    private RedisUtil redisUtil;

    /**
     * 通过微信企业号发送消息
     *
     * @param wxUserId 微信用户标识
     * @param content  发送的内容
     * @return 通过微信企业号发送消息
     */
    public String sendMessage(String wxUserId, String content) {
        // 1.获取access_token:根据企业id和应用密钥获取access_token,并拼接请求url
        String accessToken = redisUtil.get(Const.CACHE_WE_CHAT);
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = WeChatUtil.getAccessToken(defaultWXProperties.getCorpId(), defaultWXProperties.getCoprsecret())
                    .getToken();
            redisUtil.set(Const.CACHE_WE_CHAT, accessToken, 2, TimeUnit.HOURS);
        }
        // 2.获取发送对象，并转成json
        Gson gson = new Gson();
        TextMessage message = new TextMessage();
        // 1.1非必需
        // 不区分大小写
        message.setTouser(wxUserId);
        //message.setToparty("1");
        //message.getTouser(totag);
        // txtMsg.setSafe(0);
        // 1.2必需
        message.setMsgtype("text");
        message.setAgentid(defaultWXProperties.getAgentId());
        WxText wxText = new WxText();
        wxText.setContent(content);
        message.setWxText(wxText);
        String jsonMessage = gson.toJson(message);
        // 3.获取请求的url
        String url = sendMessage_url.replace("ACCESS_TOKEN", accessToken);
        // 4.调用接口，发送消息
        JSONObject jsonObject = WeChatUtil.httpRequest(url, "POST", jsonMessage);
        // 4.错误消息处理
        if (null != jsonObject) {
            if (0 != jsonObject.getInteger("errcode")) {
                log.info("消息发送失败 errcode:{} errmsg:{}", jsonObject.getInteger("errcode"),
                        jsonObject.getString("errmsg"));
            }
        }
        return jsonObject.toString();
    }
}