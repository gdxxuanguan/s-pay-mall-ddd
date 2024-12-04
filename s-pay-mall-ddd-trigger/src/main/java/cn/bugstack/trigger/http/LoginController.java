package cn.bugstack.trigger.http;


import cn.bugstack.api.response.Response;
import cn.bugstack.domain.auth.service.ILoginService;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/login/")
public class LoginController {

    @Resource
    private ILoginService loginService;
    @Qualifier("openidToken")

    @RequestMapping(value = "weixin_qrcode_ticket",method = RequestMethod.GET)
    public Response<String> weixinQrCodeTicket(){
        try{
            String qrCodeTicket=loginService.createQrCodeTicket();
            log.info("生成微信扫码登录 ticket:{}",qrCodeTicket);
            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(qrCodeTicket)
                    .build();
        }catch (Exception e){
            log.error("生成微信扫码登录 ticket 失败" ,e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "check_login",method = RequestMethod.GET)
    public  Response<String> checkLogin(@RequestParam String ticket){
        try {
            String openidToken= loginService.checkLogin(ticket);
            log.info("扫码检测登录结果 ticket:{} openidToken:{}",ticket,openidToken);
            if(StringUtils.isNotBlank(openidToken)){
                return Response.<String>builder()
                        .code(Constants.ResponseCode.SUCCESS.getCode())
                        .info(Constants.ResponseCode.SUCCESS.getInfo())
                        .data(openidToken)
                        .build();
            }else{
                return Response.<String>builder()
                        .code(Constants.ResponseCode.NO_LOGIN.getCode())
                        .info(Constants.ResponseCode.NO_LOGIN.getInfo())
                        .build();
            }
        }catch (Exception e){
            log.info("扫码检测登录结果失败 ticket:{}",ticket,e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}