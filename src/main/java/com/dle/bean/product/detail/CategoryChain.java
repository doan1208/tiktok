
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
    "is_leaf",
    "local_name",
    "parent_id"
})
public class CategoryChain implements Serializable
{

    @JsonProperty("id")
    private String id;
    @JsonProperty("is_leaf")
    private Boolean isLeaf;
    @JsonProperty("local_name")
    private String localName;
    @JsonProperty("parent_id")
    private String parentId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -4586999565192798418L;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("is_leaf")
    public Boolean getIsLeaf() {
        return isLeaf;
    }

    @JsonProperty("is_leaf")
    public void setIsLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    @JsonProperty("local_name")
    public String getLocalName() {
        return localName;
    }

    @JsonProperty("local_name")
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @JsonProperty("parent_id")
    public String getParentId() {
        return parentId;
    }

    @JsonProperty("parent_id")
    public void setParentId(String parentId) {
        this.parentId = parentId;
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
