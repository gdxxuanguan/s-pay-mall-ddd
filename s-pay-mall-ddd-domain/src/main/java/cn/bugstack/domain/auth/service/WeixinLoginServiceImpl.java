package cn.bugstack.domain.auth.service;

import cn.bugstack.domain.auth.adapter.port.ILoginPort;
import cn.bugstack.domain.auth.adapter.port.IWeixinApiService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class WeixinLoginServiceImpl implements ILoginService {

    @Resource
    private ILoginPort iLoginPort;





    @Override
    public String createQrCodeTicket() throws Exception {
        return iLoginPort.creatQrCodeTiket();
    }

    @Override
    public String checkLogin(String ticket) {

        return openidToken.getIfPresent(ticket);
    }

    @Override
    public void saveLoginSate(String ticket, String openid) throws IOException {

    }
}
