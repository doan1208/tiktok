
package com.dle.bean.product.detail;

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
    "external_sku_id",
    "id",
    "identifier_code",
    "inventory",
    "price",
    "sales_attributes",
    "seller_sku"
})
public class Sku implements Serializable
{

    @JsonProperty("external_sku_id")
    private String externalSkuId;
    @JsonProperty("id")
    private String id;
    @JsonProperty("identifier_code")
    private IdentifierCode identifierCode;
    @JsonProperty("inventory")
    private List<Inventory> inventory;
    @JsonProperty("price")
    private Price price;
    @JsonProperty("sales_attributes")
    private List<SalesAttribute> salesAttributes;
    @JsonProperty("seller_sku")
    private String sellerSku;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -2911332185381913304L;

    @JsonProperty("external_sku_id")
    public String getExternalSkuId() {
        return externalSkuId;
    }

    @JsonProperty("external_sku_id")
    public void setExternalSkuId(String externalSkuId) {
        this.externalSkuId = externalSkuId;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("identifier_code")
    public IdentifierCode getIdentifierCode() {
        return identifierCode;
    }

    @JsonProperty("identifier_code")
    public void setIdentifierCode(IdentifierCode identifierCode) {
        this.identifierCode = identifierCode;
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

    @JsonProperty("sales_attributes")
    public List<SalesAttribute> getSalesAttributes() {
        return salesAttributes;
    }

    @JsonProperty("sales_attributes")
    public void setSalesAttributes(List<SalesAttribute> salesAttributes) {
        this.salesAttributes = salesAttributes;
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
