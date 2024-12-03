package cn.bugstack.domain.auth.service;

import java.io.IOException;

public interface ILoginService  {

    String createQrCodeTicket() throws Exception;
    String checkLogin(String ticket);
    void saveLoginSate(String ticket,String openid) throws IOException;
}
