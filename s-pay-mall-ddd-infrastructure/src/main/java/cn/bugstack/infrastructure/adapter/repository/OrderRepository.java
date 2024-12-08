package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.order.adapter.event.PaySuccessMessageEvent;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.domain.order.model.entity.ShopCarEntity;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;

import cn.bugstack.infrastructure.dao.IOrderDao;
import cn.bugstack.infrastructure.dao.po.PayOrder;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.event.BaseEvent;
import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Repository
public class OrderRepository implements IOrderRepository {
    @Resource
    private IOrderDao orderDao;

    @Resource
    private PaySuccessMessageEvent paySuccessMessageEvent;

    @Resource
    private EventBus eventBus;

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        ProductEntity productEntity=orderAggregate.getProductEntity();
        OrderEntity orderEntity=orderAggregate.getOrderEntity();
        PayOrder payOrder=PayOrder.builder()
                .userId(orderAggregate.getUserId())
                .productId(productEntity.getProductId())
                .productName(productEntity.getProductDesc())
                .orderId(orderEntity.getOrderId())
                .orderTime(orderEntity.getOrderTime())
                .totalAmount(orderEntity.getTotalAmount())
                .status(orderEntity.getOrderStatusVO().getCode())
                .build();
        orderDao.insert(payOrder);
    }

    @Override
    public OrderEntity queryUnPayOrder(ShopCarEntity shopCarEntity) {
        //查询当前用户是否存在未支付订单或掉单
        PayOrder payOrder = new PayOrder();
        payOrder.setUserId(shopCarEntity.getUserId());
        payOrder.setProductId(shopCarEntity.getProductId());

        PayOrder unpaidOrder = orderDao.queryUnPayOrder(payOrder);

        if(unpaidOrder!=null){
            return OrderEntity.builder()
                    .productId(unpaidOrder.getProductId())
                    .productName(unpaidOrder.getProductName())
                    .orderId(unpaidOrder.getOrderId())
                    .orderTime(unpaidOrder.getOrderTime())
                    .totalAmount(unpaidOrder.getTotalAmount())
                    .orderStatusVO(OrderStatusVO.valueOf(unpaidOrder.getStatus()))
                    .payUrl(unpaidOrder.getPayUrl())
                    .build();
        }
        return null;
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        PayOrder payOrder=PayOrder.builder()
                .userId(payOrderEntity.getUserId())
                .orderId(payOrderEntity.getOrderId())
                .status(payOrderEntity.getOrderStatus().getCode())
                .payUrl(payOrderEntity.getPayUrl())
                .build();
        orderDao.updateOrderPayInfo(payOrder);
    }

    @Override
    public void changeOrderPaySuccess(String orderId) {
        PayOrder payOrder= PayOrder.builder()
                .orderId(orderId)
                .status(Constants.ResponseCode.SUCCESS.getCode())
                .build();
        orderDao.changeOrderPaySuccess(payOrder);

        BaseEvent.EventMessage<PaySuccessMessageEvent.PaySuccessMessage> eventMessage =
                paySuccessMessageEvent.buildEventMessage(
                        PaySuccessMessageEvent.PaySuccessMessage.builder()
                                .tradeNo(orderId)
                                .userId(payOrder.getUserId())
                                .build());
        PaySuccessMessageEvent.PaySuccessMessage message=eventMessage.getData();
        eventBus.post(message);
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderDao.changeOrderClose(orderId);
    }
}
