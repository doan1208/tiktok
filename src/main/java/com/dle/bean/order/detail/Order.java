
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
    "buyer_email",
    "buyer_message",
    "cancel_order_sla_time",
    "cancel_reason",
    "cancellation_initiator",
    "cpf",
    "create_time",
    "delivery_option_id",
    "delivery_option_name",
    "delivery_sla_time",
    "fulfillment_type",
    "has_updated_recipient_address",
    "id",
    "is_cod",
    "is_sample_order",
    "line_items",
    "need_upload_invoice",
    "packages",
    "paid_time",
    "payment",
    "payment_method_name",
    "recipient_address",
    "rts_sla_time",
    "rts_time",
    "seller_note",
    "shipping_provider",
    "shipping_provider_id",
    "shipping_type",
    "split_or_combine_tag",
    "status",
    "tracking_number",
    "tts_sla_time",
    "update_time",
    "user_id",
    "warehouse_id"
})
public class Order implements Serializable
{

    @JsonProperty("buyer_email")
    private String buyerEmail;
    @JsonProperty("buyer_message")
    private String buyerMessage;
    @JsonProperty("cancel_order_sla_time")
    private Long cancelOrderSlaTime;
    @JsonProperty("cancel_reason")
    private String cancelReason;
    @JsonProperty("cancellation_initiator")
    private String cancellationInitiator;
    @JsonProperty("cpf")
    private String cpf;
    @JsonProperty("create_time")
    private Long createTime;
    @JsonProperty("delivery_option_id")
    private String deliveryOptionId;
    @JsonProperty("delivery_option_name")
    private String deliveryOptionName;
    @JsonProperty("delivery_sla_time")
    private Long deliverySlaTime;
    @JsonProperty("fulfillment_type")
    private String fulfillmentType;
    @JsonProperty("has_updated_recipient_address")
    private Boolean hasUpdatedRecipientAddress;
    @JsonProperty("id")
    private String id;
    @JsonProperty("is_cod")
    private Boolean isCod;
    @JsonProperty("is_sample_order")
    private Boolean isSampleOrder;
    @JsonProperty("line_items")
    private List<LineItem> lineItems;
    @JsonProperty("need_upload_invoice")
    private String needUploadInvoice;
    @JsonProperty("packages")
    private List<Package> packages;
    @JsonProperty("paid_time")
    private Long paidTime;
    @JsonProperty("payment")
    private Payment payment;
    @JsonProperty("payment_method_name")
    private String paymentMethodName;
    @JsonProperty("recipient_address")
    private RecipientAddress recipientAddress;
    @JsonProperty("rts_sla_time")
    private Long rtsSlaTime;
    @JsonProperty("rts_time")
    private Long rtsTime;
    @JsonProperty("seller_note")
    private String sellerNote;
    @JsonProperty("shipping_provider")
    private String shippingProvider;
    @JsonProperty("shipping_provider_id")
    private String shippingProviderId;
    @JsonProperty("shipping_type")
    private String shippingType;
    @JsonProperty("split_or_combine_tag")
    private String splitOrCombineTag;
    @JsonProperty("status")
    private String status;
    @JsonProperty("tracking_number")
    private String trackingNumber;
    @JsonProperty("tts_sla_time")
    private Long ttsSlaTime;
    @JsonProperty("update_time")
    private Long updateTime;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("warehouse_id")
    private String warehouseId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 1558614655481147382L;

    @JsonProperty("buyer_email")
    public String getBuyerEmail() {
        return buyerEmail;
    }

    @JsonProperty("buyer_email")
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    @JsonProperty("buyer_message")
    public String getBuyerMessage() {
        return buyerMessage;
    }

    @JsonProperty("buyer_message")
    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    @JsonProperty("cancel_order_sla_time")
    public Long getCancelOrderSlaTime() {
        return cancelOrderSlaTime;
    }

    @JsonProperty("cancel_order_sla_time")
    public void setCancelOrderSlaTime(Long cancelOrderSlaTime) {
        this.cancelOrderSlaTime = cancelOrderSlaTime;
    }

    @JsonProperty("cancel_reason")
    public String getCancelReason() {
        return cancelReason;
    }

    @JsonProperty("cancel_reason")
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @JsonProperty("cancellation_initiator")
    public String getCancellationInitiator() {
        return cancellationInitiator;
    }

    @JsonProperty("cancellation_initiator")
    public void setCancellationInitiator(String cancellationInitiator) {
        this.cancellationInitiator = cancellationInitiator;
    }

    @JsonProperty("cpf")
    public String getCpf() {
        return cpf;
    }

    @JsonProperty("cpf")
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @JsonProperty("create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @JsonProperty("create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @JsonProperty("delivery_option_id")
    public String getDeliveryOptionId() {
        return deliveryOptionId;
    }

    @JsonProperty("delivery_option_id")
    public void setDeliveryOptionId(String deliveryOptionId) {
        this.deliveryOptionId = deliveryOptionId;
    }

    @JsonProperty("delivery_option_name")
    public String getDeliveryOptionName() {
        return deliveryOptionName;
    }

    @JsonProperty("delivery_option_name")
    public void setDeliveryOptionName(String deliveryOptionName) {
        this.deliveryOptionName = deliveryOptionName;
    }

    @JsonProperty("delivery_sla_time")
    public Long getDeliverySlaTime() {
        return deliverySlaTime;
    }

    @JsonProperty("delivery_sla_time")
    public void setDeliverySlaTime(Long deliverySlaTime) {
        this.deliverySlaTime = deliverySlaTime;
    }

    @JsonProperty("fulfillment_type")
    public String getFulfillmentType() {
        return fulfillmentType;
    }

    @JsonProperty("fulfillment_type")
    public void setFulfillmentType(String fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }

    @JsonProperty("has_updated_recipient_address")
    public Boolean getHasUpdatedRecipientAddress() {
        return hasUpdatedRecipientAddress;
    }

    @JsonProperty("has_updated_recipient_address")
    public void setHasUpdatedRecipientAddress(Boolean hasUpdatedRecipientAddress) {
        this.hasUpdatedRecipientAddress = hasUpdatedRecipientAddress;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("is_cod")
    public Boolean getIsCod() {
        return isCod;
    }

    @JsonProperty("is_cod")
    public void setIsCod(Boolean isCod) {
        this.isCod = isCod;
    }

    @JsonProperty("is_sample_order")
    public Boolean getIsSampleOrder() {
        return isSampleOrder;
    }

    @JsonProperty("is_sample_order")
    public void setIsSampleOrder(Boolean isSampleOrder) {
        this.isSampleOrder = isSampleOrder;
    }

    @JsonProperty("line_items")
    public List<LineItem> getLineItems() {
        return lineItems;
    }

    @JsonProperty("line_items")
    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    @JsonProperty("need_upload_invoice")
    public String getNeedUploadInvoice() {
        return needUploadInvoice;
    }

    @JsonProperty("need_upload_invoice")
    public void setNeedUploadInvoice(String needUploadInvoice) {
        this.needUploadInvoice = needUploadInvoice;
    }

    @JsonProperty("packages")
    public List<Package> getPackages() {
        return packages;
    }

    @JsonProperty("packages")
    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    @JsonProperty("paid_time")
    public Long getPaidTime() {
        return paidTime;
    }

    @JsonProperty("paid_time")
    public void setPaidTime(Long paidTime) {
        this.paidTime = paidTime;
    }

    @JsonProperty("payment")
    public Payment getPayment() {
        return payment;
    }

    @JsonProperty("payment")
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @JsonProperty("payment_method_name")
    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    @JsonProperty("payment_method_name")
    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    @JsonProperty("recipient_address")
    public RecipientAddress getRecipientAddress() {
        return recipientAddress;
    }

    @JsonProperty("recipient_address")
    public void setRecipientAddress(RecipientAddress recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    @JsonProperty("rts_sla_time")
    public Long getRtsSlaTime() {
        return rtsSlaTime;
    }

    @JsonProperty("rts_sla_time")
    public void setRtsSlaTime(Long rtsSlaTime) {
        this.rtsSlaTime = rtsSlaTime;
    }

    @JsonProperty("rts_time")
    public Long getRtsTime() {
        return rtsTime;
    }

    @JsonProperty("rts_time")
    public void setRtsTime(Long rtsTime) {
        this.rtsTime = rtsTime;
    }

    @JsonProperty("seller_note")
    public String getSellerNote() {
        return sellerNote;
    }

    @JsonProperty("seller_note")
    public void setSellerNote(String sellerNote) {
        this.sellerNote = sellerNote;
    }

    @JsonProperty("shipping_provider")
    public String getShippingProvider() {
        return shippingProvider;
    }

    @JsonProperty("shipping_provider")
    public void setShippingProvider(String shippingProvider) {
        this.shippingProvider = shippingProvider;
    }

    @JsonProperty("shipping_provider_id")
    public String getShippingProviderId() {
        return shippingProviderId;
    }

    @JsonProperty("shipping_provider_id")
    public void setShippingProviderId(String shippingProviderId) {
        this.shippingProviderId = shippingProviderId;
    }

    @JsonProperty("shipping_type")
    public String getShippingType() {
        return shippingType;
    }

    @JsonProperty("shipping_type")
    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    @JsonProperty("split_or_combine_tag")
    public String getSplitOrCombineTag() {
        return splitOrCombineTag;
    }

    @JsonProperty("split_or_combine_tag")
    public void setSplitOrCombineTag(String splitOrCombineTag) {
        this.splitOrCombineTag = splitOrCombineTag;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("tracking_number")
    public String getTrackingNumber() {
        return trackingNumber;
    }

    @JsonProperty("tracking_number")
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @JsonProperty("tts_sla_time")
    public Long getTtsSlaTime() {
        return ttsSlaTime;
    }

    @JsonProperty("tts_sla_time")
    public void setTtsSlaTime(Long ttsSlaTime) {
        this.ttsSlaTime = ttsSlaTime;
    }

    @JsonProperty("update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @JsonProperty("update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("warehouse_id")
    public String getWarehouseId() {
        return warehouseId;
    }

    @JsonProperty("warehouse_id")
    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
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
