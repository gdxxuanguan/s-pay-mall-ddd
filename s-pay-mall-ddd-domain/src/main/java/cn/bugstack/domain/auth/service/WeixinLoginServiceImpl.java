package cn.bugstack.domain.auth.service;

import cn.bugstack.domain.auth.adapter.port.ILoginPort;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class WeixinLoginServiceImpl implements ILoginService {

    @Resource
    private ILoginPort iLoginPort;





    @Override
    public String createQrCodeTicket() throws Exception {
        return iLoginPort.creatQrCodeTicket();
    }

    @Override
    public String checkLogin(String ticket) {

        return iLoginPort.checkLogin(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        iLoginPort.saveLoginState(ticket,openid);
    }
}
