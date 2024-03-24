
package com.dle.bean.order;

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
    "tax_amount",
    "tax_type"
})
public class ItemTax implements Serializable
{

    @JsonProperty("tax_amount")
    private String taxAmount;
    @JsonProperty("tax_type")
    private String taxType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -7534043785330028462L;

    @JsonProperty("tax_amount")
    public String getTaxAmount() {
        return taxAmount;
    }

    @JsonProperty("tax_amount")
    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    @JsonProperty("tax_type")
    public String getTaxType() {
        return taxType;
    }

    @JsonProperty("tax_type")
    public void setTaxType(String taxType) {
        this.taxType = taxType;
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
