
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
    "address_level_name",
    "address_name"
})
public class DistrictInfo implements Serializable
{

    @JsonProperty("address_level_name")
    private String addressLevelName;
    @JsonProperty("address_name")
    private String addressName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 6846898334670423744L;

    @JsonProperty("address_level_name")
    public String getAddressLevelName() {
        return addressLevelName;
    }

    @JsonProperty("address_level_name")
    public void setAddressLevelName(String addressLevelName) {
        this.addressLevelName = addressLevelName;
    }

    @JsonProperty("address_name")
    public String getAddressName() {
        return addressName;
    }

    @JsonProperty("address_name")
    public void setAddressName(String addressName) {
        this.addressName = addressName;
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
