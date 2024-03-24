package com.dle.bean.database;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "DOCUMENT")
public class Document extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "package_id")
    String packageId;

    @Column(name = "shop_code")
    String shopCode;

    @Column(name = "doc_url")
    String docUrl;

    @Column(name = "count")
    Integer count = 0;

    @Column(name = "error_message")
    String errorMessage;

    @Column(name = "download_date")
    Date downloadDate;

    public Document() {
    }

    public Document(String packageId, String shopCode, Date downloadDate) {
        this.packageId = packageId;
        this.shopCode = shopCode;
        this.downloadDate = downloadDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }
}
