
package com.dle.bean.product.list;

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
    "id",
    "inventory",
    "price",
    "seller_sku"
})
public class Sku implements Serializable
{

    @JsonProperty("id")
    private String id;
    @JsonProperty("inventory")
    private List<Inventory> inventory;
    @JsonProperty("price")
    private Price price;
    @JsonProperty("seller_sku")
    private String sellerSku;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -8982389633732473925L;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("inventory")
    public List<Inventory> getInventory() {
        return inventory;
    }

    @JsonProperty("inventory")
    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    @JsonProperty("price")
    public Price getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(Price price) {
        this.price = price;
    }

    @JsonProperty("seller_sku")
    public String getSellerSku() {
        return sellerSku;
    }

    @JsonProperty("seller_sku")
    public void setSellerSku(String sellerSku) {
        this.sellerSku = sellerSku;
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
