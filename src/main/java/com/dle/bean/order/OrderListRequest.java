package com.dle.bean.order;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "buyer_user_id",
        "create_time_ge",
        "create_time_lt",
        "order_status",
        "shipping_type",
        "update_time_ge",
        "update_time_lt"
})
public class OrderListRequest implements Serializable
{

    @JsonProperty("buyer_user_id")
    private String buyerUserId;
    @JsonProperty("create_time_ge")
    private Long createTimeGe;
    @JsonProperty("create_time_lt")
    private Long createTimeLt;
    @JsonProperty("order_status")
    private String orderStatus;
    @JsonProperty("shipping_type")
    private String shippingType;
    @JsonProperty("update_time_ge")
    private Long updateTimeGe;
    @JsonProperty("update_time_lt")
    private Long updateTimeLt;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -4879223731745406175L;

    @JsonProperty("buyer_user_id")
    public String getBuyerUserId() {
        return buyerUserId;
    }

    @JsonProperty("buyer_user_id")
    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    @JsonProperty("create_time_ge")
    public Long getCreateTimeGe() {
        return createTimeGe;
    }

    @JsonProperty("create_time_ge")
    public void setCreateTimeGe(Long createTimeGe) {
        this.createTimeGe = createTimeGe;
    }

    @JsonProperty("create_time_lt")
    public Long getCreateTimeLt() {
        return createTimeLt;
    }

    @JsonProperty("create_time_lt")
    public void setCreateTimeLt(Long createTimeLt) {
        this.createTimeLt = createTimeLt;
    }

    @JsonProperty("order_status")
    public String getOrderStatus() {
        return orderStatus;
    }

    @JsonProperty("order_status")
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @JsonProperty("shipping_type")
    public String getShippingType() {
        return shippingType;
    }

    @JsonProperty("shipping_type")
    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    @JsonProperty("update_time_ge")
    public Long getUpdateTimeGe() {
        return updateTimeGe;
    }

    @JsonProperty("update_time_ge")
    public void setUpdateTimeGe(Long updateTimeGe) {
        this.updateTimeGe = updateTimeGe;
    }

    @JsonProperty("update_time_lt")
    public Long getUpdateTimeLt() {
        return updateTimeLt;
    }

    @JsonProperty("update_time_lt")
    public void setUpdateTimeLt(Long updateTimeLt) {
        this.updateTimeLt = updateTimeLt;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
