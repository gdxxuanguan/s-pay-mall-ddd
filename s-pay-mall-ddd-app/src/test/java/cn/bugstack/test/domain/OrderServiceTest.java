package cn.bugstack.test.domain;

import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCarEntity;
import cn.bugstack.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {
    @Resource
    private IOrderService orderService;


    @Test
    public void testCreateOrder() throws Exception {
        ShopCarEntity shopCartReq = new ShopCarEntity();
        shopCartReq.setUserId("zhb");
        shopCartReq.setProductId("10001");
        PayOrderEntity payOrderRes =orderService.createOrder(shopCartReq);
        log.info("请求参数:{}", JSON.toJSONString(shopCartReq));
        log.info("测试结果:{}", JSON.toJSONString(payOrderRes));
    }
}
