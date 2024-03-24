
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
    "quantity",
    "warehouse_id"
})
public class Inventory implements Serializable
{

    @JsonProperty("quantity")
    private Long quantity;
    @JsonProperty("warehouse_id")
    private String warehouseId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 2738733525640512376L;

    @JsonProperty("quantity")
    public Long getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
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
