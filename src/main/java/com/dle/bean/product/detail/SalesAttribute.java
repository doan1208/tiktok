
package com.dle.bean.product.detail;

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
    "id",
    "name",
    "sku_img",
    "value_id",
    "value_name"
})
public class SalesAttribute implements Serializable
{

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sku_img")
    private SkuImg skuImg;
    @JsonProperty("value_id")
    private String valueId;
    @JsonProperty("value_name")
    private String valueName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -5735315072767861565L;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("sku_img")
    public SkuImg getSkuImg() {
        return skuImg;
    }

    @JsonProperty("sku_img")
    public void setSkuImg(SkuImg skuImg) {
        this.skuImg = skuImg;
    }

    @JsonProperty("value_id")
    public String getValueId() {
        return valueId;
    }

    @JsonProperty("value_id")
    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    @JsonProperty("value_name")
    public String getValueName() {
        return valueName;
    }

    @JsonProperty("value_name")
    public void setValueName(String valueName) {
        this.valueName = valueName;
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
