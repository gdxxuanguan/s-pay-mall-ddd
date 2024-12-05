package cn.bugstack.domain.order.adapter.port;

import cn.bugstack.domain.order.model.entity.ProductEntity;

public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);
}
