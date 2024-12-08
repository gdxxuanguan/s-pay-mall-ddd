package cn.bugstack.domain.order.service;

import cn.bugstack.domain.order.adapter.port.IProductPort;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.domain.order.model.entity.ShopCarEntity;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public abstract class AbstractOrderService implements IOrderService{

    protected final IOrderRepository repository;
    protected final IProductPort port;

    public AbstractOrderService(IOrderRepository repository, IProductPort port) {
        this.repository = repository;
        this.port = port;
    }


    @Override
    public PayOrderEntity createOrder(ShopCarEntity shopCarEntity) throws Exception {
        //查询当前用户是否存在未支付订单或掉单
//        PayOrderEntity payOrderReq = new PayOrderEntity();
//        payOrderReq.setUserId(shopCartReq.getUserId());
//        payOrderReq.setProductId(shopCartReq.getProductId());

        OrderEntity unpaidOrder = repository.queryUnPayOrder(shopCarEntity);
        if(unpaidOrder != null &&
                OrderStatusVO.PAY_WAIT.equals(unpaidOrder.getOrderStatusVO())){
            log.info("创建订单-存在未支付订单。userID:{} productId:{} " +
                            "orderId:{}", shopCarEntity.getUserId(), shopCarEntity.getProductId(),
                    unpaidOrder.getOrderId());
            return PayOrderEntity.builder()
                    .orderId(unpaidOrder.getOrderId())
                    .payUrl(unpaidOrder.getPayUrl())
                    .build();
        }else if(unpaidOrder != null &&
                OrderStatusVO.CREATE.equals(unpaidOrder.getOrderStatusVO())){
            log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} productId:{} orderId:{}", shopCarEntity.getUserId(), shopCarEntity.getProductId(), unpaidOrder.getOrderId());
//            PayOrder payOrder = doPrepayOrder(unpaidOrder.getProductId(), unpaidOrder.getProductName(), unpaidOrder.getOrderId(), unpaidOrder.getTotalAmount());

            PayOrderEntity payOrderEntity=doPrepayOrder(shopCarEntity.getUserId(), shopCarEntity.getProductId(),
                    unpaidOrder.getProductName(),unpaidOrder.getOrderId(),unpaidOrder.getTotalAmount());
            return PayOrderEntity.builder()
                    .orderId(payOrderEntity.getOrderId())
                    .payUrl(payOrderEntity.getPayUrl())
                    .build();
        }
        //2.首次下单,查询商品，创建订单
        ProductEntity productEntity=port.queryProductByProductId(shopCarEntity.getProductId());
        String orderId= RandomStringUtils.randomNumeric(16);

        log.info("创建订单，为新订单，创建支付单开始");

        OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName(),productEntity.getPrice());
        CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder()
                .userId(shopCarEntity.getUserId())
                .productEntity(productEntity)
                .orderEntity(orderEntity)
                .build();

        this.doSaveOrder(orderAggregate);

        PayOrderEntity payOrderEntity=doPrepayOrder(shopCarEntity.getUserId(), shopCarEntity.getProductId(),
                orderEntity.getProductName(),orderEntity.getOrderId(),orderEntity.getTotalAmount());



        return PayOrderEntity.builder()
                .orderId(payOrderEntity.getOrderId())
                .payUrl(payOrderEntity.getPayUrl())
                .build();


    }

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException;

    protected abstract void doSaveOrder(CreateOrderAggregate orderAggregate);


}
