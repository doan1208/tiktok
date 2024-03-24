
package com.dle.bean.order.detail;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cancel_reason",
    "cancel_user",
    "currency",
    "display_status",
    "id",
    "is_gift",
    "item_tax",
    "original_price",
    "package_id",
    "package_status",
    "platform_discount",
    "product_id",
    "product_name",
    "rts_time",
    "sale_price",
    "seller_discount",
    "shipping_provider_id",
    "shipping_provider_name",
    "sku_id",
    "sku_image",
    "sku_type",
    "small_order_fee",
    "tracking_number"
})
public class LineItem implements Serializable
{

    @JsonProperty("cancel_reason")
    private String cancelReason;
    @JsonProperty("cancel_user")
    private String cancelUser;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("display_status")
    private String displayStatus;
    @JsonProperty("id")
    private String id;
    @JsonProperty("is_gift")
    private Boolean isGift;
    @JsonProperty("item_tax")
    private List<ItemTax> itemTax;
    @JsonProperty("original_price")
    private String originalPrice;
    @JsonProperty("package_id")
    private String packageId;
    @JsonProperty("package_status")
    private String packageStatus;
    @JsonProperty("platform_discount")
    private String platformDiscount;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("rts_time")
    private Long rtsTime;
    @JsonProperty("sale_price")
    private String salePrice;
    @JsonProperty("seller_discount")
    private String sellerDiscount;
    @JsonProperty("shipping_provider_id")
    private String shippingProviderId;
    @JsonProperty("shipping_provider_name")
    private String shippingProviderName;
    @JsonProperty("sku_id")
    private String skuId;
    @JsonProperty("sku_image")
    private String skuImage;
    @JsonProperty("sku_type")
    private String skuType;
    @JsonProperty("small_order_fee")
    private String smallOrderFee;
    @JsonProperty("tracking_number")
    private String trackingNumber;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -4857825502174385336L;

    @JsonProperty("cancel_reason")
    public String getCancelReason() {
        return cancelReason;
    }

    @JsonProperty("cancel_reason")
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @JsonProperty("cancel_user")
    public String getCancelUser() {
        return cancelUser;
    }

    @JsonProperty("cancel_user")
    public void setCancelUser(String cancelUser) {
        this.cancelUser = cancelUser;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("display_status")
    public String getDisplayStatus() {
        return displayStatus;
    }

    @JsonProperty("display_status")
    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("is_gift")
    public Boolean getIsGift() {
        return isGift;
    }

    @JsonProperty("is_gift")
    public void setIsGift(Boolean isGift) {
        this.isGift = isGift;
    }

    @JsonProperty("item_tax")
    public List<ItemTax> getItemTax() {
        return itemTax;
    }

    @JsonProperty("item_tax")
    public void setItemTax(List<ItemTax> itemTax) {
        this.itemTax = itemTax;
    }

    @JsonProperty("original_price")
    public String getOriginalPrice() {
        return originalPrice;
    }

    @JsonProperty("original_price")
    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    @JsonProperty("package_id")
    public String getPackageId() {
        return packageId;
    }

    @JsonProperty("package_id")
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @JsonProperty("package_status")
    public String getPackageStatus() {
        return packageStatus;
    }

    @JsonProperty("package_status")
    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }

    @JsonProperty("platform_discount")
    public String getPlatformDiscount() {
        return platformDiscount;
    }

    @JsonProperty("platform_discount")
    public void setPlatformDiscount(String platformDiscount) {
        this.platformDiscount = platformDiscount;
    }

    @JsonProperty("product_id")
    public String getProductId() {
        return productId;
    }

    @JsonProperty("product_id")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("product_name")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("product_name")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("rts_time")
    public Long getRtsTime() {
        return rtsTime;
    }

    @JsonProperty("rts_time")
    public void setRtsTime(Long rtsTime) {
        this.rtsTime = rtsTime;
    }

    @JsonProperty("sale_price")
    public String getSalePrice() {
        return salePrice;
    }

    @JsonProperty("sale_price")
    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    @JsonProperty("seller_discount")
    public String getSellerDiscount() {
        return sellerDiscount;
    }

    @JsonProperty("seller_discount")
    public void setSellerDiscount(String sellerDiscount) {
        this.sellerDiscount = sellerDiscount;
    }

    @JsonProperty("shipping_provider_id")
    public String getShippingProviderId() {
        return shippingProviderId;
    }

    @JsonProperty("shipping_provider_id")
    public void setShippingProviderId(String shippingProviderId) {
        this.shippingProviderId = shippingProviderId;
    }

    @JsonProperty("shipping_provider_name")
    public String getShippingProviderName() {
        return shippingProviderName;
    }

    @JsonProperty("shipping_provider_name")
    public void setShippingProviderName(String shippingProviderName) {
        this.shippingProviderName = shippingProviderName;
    }

    @JsonProperty("sku_id")
    public String getSkuId() {
        return skuId;
    }

    @JsonProperty("sku_id")
    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    @JsonProperty("sku_image")
    public String getSkuImage() {
        return skuImage;
    }

    @JsonProperty("sku_image")
    public void setSkuImage(String skuImage) {
        this.skuImage = skuImage;
    }

    @JsonProperty("sku_type")
    public String getSkuType() {
        return skuType;
    }

    @JsonProperty("sku_type")
    public void setSkuType(String skuType) {
        this.skuType = skuType;
    }

    @JsonProperty("small_order_fee")
    public String getSmallOrderFee() {
        return smallOrderFee;
    }

    @JsonProperty("small_order_fee")
    public void setSmallOrderFee(String smallOrderFee) {
        this.smallOrderFee = smallOrderFee;
    }

    @JsonProperty("tracking_number")
    public String getTrackingNumber() {
        return trackingNumber;
    }

    @JsonProperty("tracking_number")
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
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
