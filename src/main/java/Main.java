import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;

import java.io.IOException;

/**
 * Created by hxiao on 16/1/20.
 */
public class Main {

    // 微信公众号、企业号Java SDK
//    https://github.com/chanjarster/weixin-java-tools
    // 我们需要的是servlet例子
    // https://github.com/chanjarster/weixin-java-tools/wiki/MP_Quick-Start


    public static void main(final String[] args) throws IOException {

        WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();

        config.setAppId("wx32c7228069d88769"); // 设置微信公众号的appid
        config.setSecret("24bba82c919a543982c78e77ed4b3863"); // 设置微信公众号的app corpSecret
        config.setToken("ojinscom"); // 设置微信公众号的token
        config.setAesKey("aoGcSLZpgW12i46uP8AZ2w6z707nSYmg1YLUsxj2Ee2"); // 设置微信公众号的EncodingAESKey

        WxMpService wxService = new WxMpServiceImpl();
        wxService.setWxMpConfigStorage(config);

        // 用户的openid在下面地址获得
        // https://mp.weixin.qq.com/debug/cgi-bin/apiinfo?t=index&type=用户管理&form=获取关注者列表接口%20/user/get
        String openid = "oqH0dwB9sigjYhWosAvF8NInxHHk";
        WxMpCustomMessage message = WxMpCustomMessage.TEXT()
                .toUser(openid)
                .content("Hello World")
                .build();

        try {
            wxService.customMessageSend(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
