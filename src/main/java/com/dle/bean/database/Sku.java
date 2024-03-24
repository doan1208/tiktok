package com.dle.bean.database;

import jakarta.persistence.*;

@Entity
@Table(name = "SKU")
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "sku_id")
    String skuId;

    @Column(name = "product_id")
    String productId;

//    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false, insertable = false, updatable = false)
//    private Product product;

    @Column(name = "product_title")
    String productTitle;

    Integer quantity;

    String salePrice;

    @Column(name = "tax_exclusive_price")
    String taxExclusivePrice;

    String size;

    String color;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getTaxExclusivePrice() {
        return taxExclusivePrice;
    }

    public void setTaxExclusivePrice(String taxExclusivePrice) {
        this.taxExclusivePrice = taxExclusivePrice;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
