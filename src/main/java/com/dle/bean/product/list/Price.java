
package com.dle.bean.product.list;

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
    "sale_price",
    "tax_exclusive_price"
})
public class Price implements Serializable
{

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("sale_price")
    private String salePrice;
    @JsonProperty("tax_exclusive_price")
    private String taxExclusivePrice;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -1436113061641377797L;

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("sale_price")
    public String getSalePrice() {
        return salePrice;
    }

    @JsonProperty("sale_price")
    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    @JsonProperty("tax_exclusive_price")
    public String getTaxExclusivePrice() {
        return taxExclusivePrice;
    }

    @JsonProperty("tax_exclusive_price")
    public void setTaxExclusivePrice(String taxExclusivePrice) {
        this.taxExclusivePrice = taxExclusivePrice;
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
