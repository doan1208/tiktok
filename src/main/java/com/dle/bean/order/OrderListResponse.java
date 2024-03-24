
package com.dle.bean.order;

import com.dle.bean.BaseResponse;

public class OrderListResponse extends BaseResponse<Data> {

    public void combine(OrderListResponse orderListResponse) {
        if (orderListResponse.getData() != null && orderListResponse.getData().getOrders() != null)
            this.getData().getOrders().addAll(orderListResponse.getData().getOrders());
    }

}
