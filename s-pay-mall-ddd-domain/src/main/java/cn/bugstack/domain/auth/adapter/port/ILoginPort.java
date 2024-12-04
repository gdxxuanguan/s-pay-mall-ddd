package cn.bugstack.domain.auth.adapter.port;

import java.io.IOException;

public interface ILoginPort {
    String creatQrCodeTicket() throws IOException;

    String checkLogin(String ticket);

    void saveLoginState(String ticket, String openid) throws IOException;
}
