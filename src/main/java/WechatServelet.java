import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by han on 1/23/16.
 */
public class WechatServelet extends HttpServlet {

    // barcodeimage handler for processing barcode
    BarcodeImageHandler barcodeImageHandler = new BarcodeImageHandler();
    WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
    protected WxMpService wxMpService = new WxMpServiceImpl();
    protected WxMpMessageRouter wxMpMessageRouter;


    @Override
    public void init() throws ServletException {
        super.init();

        config.setAppId("wx32c7228069d88769"); // 设置微信公众号的appid
        config.setSecret("24bba82c919a543982c78e77ed4b3863"); // 设置微信公众号的app corpSecret
        config.setToken("ojinscom"); // 设置微信公众号的token
        config.setAesKey("aoGcSLZpgW12i46uP8AZ2w6z707nSYmg1YLUsxj2Ee2"); // 设置微信公众号的EncodingAESKey

        wxMpService.setWxMpConfigStorage(config);

        // default text handler;
        WxMpMessageHandler handler =
                (wxMpXmlMessage, map, wxMpService1, wxSessionManager) -> WxMpXmlOutMessage
                        .TEXT()
                        .content("测试加密消息")
                        .fromUser(wxMpXmlMessage.getToUserName()) // now server is "from", client is "to"
                        .toUser(wxMpXmlMessage.getFromUserName())
                        .build();



        wxMpMessageRouter = new WxMpMessageRouter(wxMpService);

        wxMpMessageRouter
                .rule()
                .async(false)
                .content("哈哈") // 拦截内容为“哈哈”的消息
                .handler(handler)
                .end()
                .rule()
                .async(false)
                .msgType("image") // forward anykind of image to barcode handler
                .handler(barcodeImageHandler)
                .end();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            response.getWriter().println("非法请求");
            return;
        }

        String echostr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echostr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            response.getWriter().println(echostr);
            return;
        }

        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
                "raw" :
                request.getParameter("encrypt_type");

        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            response.getWriter().write(outMessage.toXml());
            return;
        }

        if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage
                    .fromEncryptedXml(request.getInputStream(),
                            config, timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            response.getWriter().write(outMessage.toEncryptedXml(config));
            return;
        }

        response.getWriter().println("不可识别的加密类型");
    }
}
