package cn.bugstack.domain.order.service;

import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCarEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IOrderService {

    PayOrderEntity createOrder(ShopCarEntity shopCarEntity) throws Exception;

    void changeOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);


}
