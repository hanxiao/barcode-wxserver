import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Created by hxiao on 16/1/20.
 */
public class Main extends HttpServlet {

    // 微信公众号、企业号Java SDK
    // https://github.com/chanjarster/weixin-java-tools
    // 我们需要的是servlet例子
    // https://github.com/chanjarster/weixin-java-tools/wiki/MP_Quick-Start

    public static void main(final String[] args) throws IOException {

        try {
            WechatServelet wechatServelet = new WechatServelet();
            wechatServelet.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }






}
