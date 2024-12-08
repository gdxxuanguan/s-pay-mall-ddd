package cn.bugstack.api;

import cn.bugstack.api.dto.CreatePayRequestDTO;
import cn.bugstack.api.response.Response;
import com.alipay.api.AlipayApiException;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface IPayService {
    Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);
    String payNotify(HttpServletRequest request) throws AlipayApiException;




}
