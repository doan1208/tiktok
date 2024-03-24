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
        "create_time_ge",
        "create_time_le",
        "seller_skus",
        "status",
        "update_time_ge",
        "update_time_le"
})
public class ProductListRequest implements Serializable
{

    @JsonProperty("create_time_ge")
    private Long createTimeGe;
    @JsonProperty("create_time_le")
    private Long createTimeLe;
    @JsonProperty("seller_skus")
    private List<String> sellerSkus;
    @JsonProperty("status")
    private String status;
    @JsonProperty("update_time_ge")
    private Long updateTimeGe;
    @JsonProperty("update_time_le")
    private Long updateTimeLe;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 5477167619573575519L;

    @JsonProperty("create_time_ge")
    public Long getCreateTimeGe() {
        return createTimeGe;
    }

    @JsonProperty("create_time_ge")
    public void setCreateTimeGe(Long createTimeGe) {
        this.createTimeGe = createTimeGe;
    }

    @JsonProperty("create_time_le")
    public Long getCreateTimeLe() {
        return createTimeLe;
    }

    @JsonProperty("create_time_le")
    public void setCreateTimeLe(Long createTimeLe) {
        this.createTimeLe = createTimeLe;
    }

    @JsonProperty("seller_skus")
    public List<String> getSellerSkus() {
        return sellerSkus;
    }

    @JsonProperty("seller_skus")
    public void setSellerSkus(List<String> sellerSkus) {
        this.sellerSkus = sellerSkus;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("update_time_ge")
    public Long getUpdateTimeGe() {
        return updateTimeGe;
    }

    @JsonProperty("update_time_ge")
    public void setUpdateTimeGe(Long updateTimeGe) {
        this.updateTimeGe = updateTimeGe;
    }

    @JsonProperty("update_time_le")
    public Long getUpdateTimeLe() {
        return updateTimeLe;
    }

    @JsonProperty("update_time_le")
    public void setUpdateTimeLe(Long updateTimeLe) {
        this.updateTimeLe = updateTimeLe;
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