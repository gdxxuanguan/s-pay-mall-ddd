package cn.bugstack.infrastructure.adapter.port;

import cn.bugstack.domain.auth.adapter.port.ILoginPort;
import cn.bugstack.infrastructure.gateway.IWeixinApiService;
import cn.bugstack.infrastructure.gateway.dto.WeixinQrCodeReqDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinQrCodeResDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTokenResDTO;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginPort implements ILoginPort {


    @Value("${weixin.config.app-id}")
    private String appid;
    @Value("${weixin.config.app-secret}")
    private String appSecret;
    @Value("${weixin.config.template_id}")
    private String template_id;

    @Resource
    private Cache<String,String> weixinAccessToken;
    @Resource
    private IWeixinApiService weixinApiService;
    @Resource
    private Cache<String,String> openidToken;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String creatQrCodeTicket() throws IOException {
//      String accessToken = weixinAccessToken.getIfPresent(appid);
        String accessToken = redisTemplate.opsForValue().get(appid);
        if(accessToken == null){
            Call<WeixinTokenResDTO> call=weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenResDTO weixinTokenRes=call.execute().body();
            assert weixinTokenRes!=null;
            accessToken=weixinTokenRes.getAccess_token();
//          weixinAccessToken.put(appid,accessToken);
            redisTemplate.opsForValue().set(appid,accessToken,2, TimeUnit.HOURS);
        }

        WeixinQrCodeReqDTO weixinQrCodeReq=WeixinQrCodeReqDTO.builder()
                .expire_seconds(2592000)
                .action_name(WeixinQrCodeReqDTO.ActionNameTypeVO.QR_SCENE.getCode())
                .action_info(WeixinQrCodeReqDTO.ActionInfo.builder()
                        .scene(WeixinQrCodeReqDTO.ActionInfo.Scene.builder()
                                .scene_id(100601)
                                .build())
                        .build())
                .build();
        Call<WeixinQrCodeResDTO> call=weixinApiService.createQrCode(accessToken,weixinQrCodeReq);
        WeixinQrCodeResDTO weixinQrCodeRes=call.execute().body();
        assert weixinQrCodeRes!=null;
        return weixinQrCodeRes.getTicket();
    }

    @Override
    public String checkLogin(String ticket) {
        return openidToken.getIfPresent(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        openidToken.put(ticket,openid);

        // 1. 获取 accessToken 【实际业务场景，按需处理下异常】
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if (null == accessToken){
            Call<WeixinTokenResDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenResDTO weixinTokenRes = call.execute().body();
            assert weixinTokenRes != null;
            accessToken = weixinTokenRes.getAccess_token();
            weixinAccessToken.put(appid, accessToken);
        }

        // 2. 发送模板消息
        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.USER, openid);

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinTemplateMessageDTO(openid, template_id);
        templateMessageDTO.setUrl("https://gaga.plus");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();
    }
}
