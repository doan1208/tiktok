package com.dle.bean.database;

import com.dle.bean.order.Package;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.stream.Collectors;

@Entity
@Table(name = "ORDER__")
public class Order {

    @Id
    String orderId;

    @Column
    String packageId;
    @Column
    String shopCode;
    @Column
    String orderStatus;
    @Column
    Long createTime;
    @Column
    Long updateTime;
    @Column
    Integer itemCount = 0;
    @Column
    String lineItems;
    @Column
    String docUrl;
    @Column
    String createDocError;
    @Column
    Integer downloadCount = 0;
    @Column
    String downloadError;
    @Column
    Date downloadDate;
    @Column
    String cancellation;
    @Column
    String cancelReason;

    public Order() {
    }

    public Order(com.dle.bean.order.Order order, String shopCode) {
        this.orderId = order.getId();
        this.packageId = order.getPackages().stream().map(Package::getId).collect(Collectors.joining(","));
        this.shopCode = shopCode;
        this.itemCount = order.getLineItems().size();
        this.lineItems = order.getLineItems().stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.joining(","));
        this.orderStatus = order.getStatus();
        this.createTime = order.getCreateTime();
        this.updateTime = order.getUpdateTime();
        this.cancellation = order.getCancellationInitiator();
        this.cancelReason = order.getCancelReason();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String id) {
        this.orderId = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String status) {
        this.orderStatus = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public String getLineItems() {
        return lineItems;
    }

    public void setLineItems(String lineItems) {
        this.lineItems = lineItems;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public String getCreateDocError() {
        return createDocError;
    }

    public void setCreateDocError(String createDocError) {
        this.createDocError = createDocError;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getDownloadError() {
        return downloadError;
    }

    public void setDownloadError(String downloadError) {
        this.downloadError = downloadError;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getCancellation() {
        return cancellation;
    }

    public void setCancellation(String cancel) {
        this.cancellation = cancel;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
