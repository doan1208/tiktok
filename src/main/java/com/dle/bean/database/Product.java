package com.dle.bean.database;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "PRODUCT")
public class Product {

    @Id
    @Column(name = "product_id")
    String productId;

    @Column(name = "create_time")
    Long createTime;

    String status;

    String title;

    @Column(name = "update_time")
    Long updateTime;

//    @OneToMany
//    @JoinColumn(name = "sku_id") // we need to duplicate the physical information
//    @OneToMany(mappedBy = "product")
//    private Set<Sku> items;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

}
