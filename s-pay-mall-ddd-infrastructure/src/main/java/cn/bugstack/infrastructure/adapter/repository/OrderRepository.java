package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.domain.order.model.entity.ShopCarEntity;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import cn.bugstack.infrastructure.dao.IOrderDao;
import cn.bugstack.infrastructure.dao.po.PayOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OrderRepository implements IOrderRepository {
    @Resource
    private IOrderDao orderDao;

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
}
