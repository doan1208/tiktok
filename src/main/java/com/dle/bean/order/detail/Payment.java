
package com.dle.bean.order.detail;

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
    "currency",
    "original_shipping_fee",
    "original_total_product_price",
    "platform_discount",
    "product_tax",
    "seller_discount",
    "shipping_fee",
    "shipping_fee_platform_discount",
    "shipping_fee_seller_discount",
    "shipping_fee_tax",
    "small_order_fee",
    "sub_total",
    "tax",
    "total_amount"
})
public class Payment implements Serializable
{

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("original_shipping_fee")
    private String originalShippingFee;
    @JsonProperty("original_total_product_price")
    private String originalTotalProductPrice;
    @JsonProperty("platform_discount")
    private String platformDiscount;
    @JsonProperty("product_tax")
    private String productTax;
    @JsonProperty("seller_discount")
    private String sellerDiscount;
    @JsonProperty("shipping_fee")
    private String shippingFee;
    @JsonProperty("shipping_fee_platform_discount")
    private String shippingFeePlatformDiscount;
    @JsonProperty("shipping_fee_seller_discount")
    private String shippingFeeSellerDiscount;
    @JsonProperty("shipping_fee_tax")
    private String shippingFeeTax;
    @JsonProperty("small_order_fee")
    private String smallOrderFee;
    @JsonProperty("sub_total")
    private String subTotal;
    @JsonProperty("tax")
    private String tax;
    @JsonProperty("total_amount")
    private String totalAmount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -5844283796731983640L;

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("original_shipping_fee")
    public String getOriginalShippingFee() {
        return originalShippingFee;
    }

    @JsonProperty("original_shipping_fee")
    public void setOriginalShippingFee(String originalShippingFee) {
        this.originalShippingFee = originalShippingFee;
    }

    @JsonProperty("original_total_product_price")
    public String getOriginalTotalProductPrice() {
        return originalTotalProductPrice;
    }

    @JsonProperty("original_total_product_price")
    public void setOriginalTotalProductPrice(String originalTotalProductPrice) {
        this.originalTotalProductPrice = originalTotalProductPrice;
    }

    @JsonProperty("platform_discount")
    public String getPlatformDiscount() {
        return platformDiscount;
    }

    @JsonProperty("platform_discount")
    public void setPlatformDiscount(String platformDiscount) {
        this.platformDiscount = platformDiscount;
    }

    @JsonProperty("product_tax")
    public String getProductTax() {
        return productTax;
    }

    @JsonProperty("product_tax")
    public void setProductTax(String productTax) {
        this.productTax = productTax;
    }

    @JsonProperty("seller_discount")
    public String getSellerDiscount() {
        return sellerDiscount;
    }

    @JsonProperty("seller_discount")
    public void setSellerDiscount(String sellerDiscount) {
        this.sellerDiscount = sellerDiscount;
    }

    @JsonProperty("shipping_fee")
    public String getShippingFee() {
        return shippingFee;
    }

    @JsonProperty("shipping_fee")
    public void setShippingFee(String shippingFee) {
        this.shippingFee = shippingFee;
    }

    @JsonProperty("shipping_fee_platform_discount")
    public String getShippingFeePlatformDiscount() {
        return shippingFeePlatformDiscount;
    }

    @JsonProperty("shipping_fee_platform_discount")
    public void setShippingFeePlatformDiscount(String shippingFeePlatformDiscount) {
        this.shippingFeePlatformDiscount = shippingFeePlatformDiscount;
    }

    @JsonProperty("shipping_fee_seller_discount")
    public String getShippingFeeSellerDiscount() {
        return shippingFeeSellerDiscount;
    }

    @JsonProperty("shipping_fee_seller_discount")
    public void setShippingFeeSellerDiscount(String shippingFeeSellerDiscount) {
        this.shippingFeeSellerDiscount = shippingFeeSellerDiscount;
    }

    @JsonProperty("shipping_fee_tax")
    public String getShippingFeeTax() {
        return shippingFeeTax;
    }

    @JsonProperty("shipping_fee_tax")
    public void setShippingFeeTax(String shippingFeeTax) {
        this.shippingFeeTax = shippingFeeTax;
    }

    @JsonProperty("small_order_fee")
    public String getSmallOrderFee() {
        return smallOrderFee;
    }

    @JsonProperty("small_order_fee")
    public void setSmallOrderFee(String smallOrderFee) {
        this.smallOrderFee = smallOrderFee;
    }

    @JsonProperty("sub_total")
    public String getSubTotal() {
        return subTotal;
    }

    @JsonProperty("sub_total")
    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    @JsonProperty("tax")
    public String getTax() {
        return tax;
    }

    @JsonProperty("tax")
    public void setTax(String tax) {
        this.tax = tax;
    }

    @JsonProperty("total_amount")
    public String getTotalAmount() {
        return totalAmount;
    }

    @JsonProperty("total_amount")
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
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
