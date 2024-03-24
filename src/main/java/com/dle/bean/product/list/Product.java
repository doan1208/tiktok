
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
    "create_time",
    "id",
    "product_sync_fail_reasons",
    "sales_regions",
    "skus",
    "status",
    "title",
    "update_time"
})
public class Product implements Serializable
{

    @JsonProperty("create_time")
    private Long createTime;
    @JsonProperty("id")
    private String id;
    @JsonProperty("product_sync_fail_reasons")
    private String productSyncFailReasons;
    @JsonProperty("sales_regions")
    private List<String> salesRegions;
    @JsonProperty("skus")
    private List<Sku> skus;
    @JsonProperty("status")
    private String status;
    @JsonProperty("title")
    private String title;
    @JsonProperty("update_time")
    private Long updateTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 784891517002000844L;

    @JsonProperty("create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @JsonProperty("create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("product_sync_fail_reasons")
    public String getProductSyncFailReasons() {
        return productSyncFailReasons;
    }

    @JsonProperty("product_sync_fail_reasons")
    public void setProductSyncFailReasons(String productSyncFailReasons) {
        this.productSyncFailReasons = productSyncFailReasons;
    }

    @JsonProperty("sales_regions")
    public List<String> getSalesRegions() {
        return salesRegions;
    }

    @JsonProperty("sales_regions")
    public void setSalesRegions(List<String> salesRegions) {
        this.salesRegions = salesRegions;
    }

    @JsonProperty("skus")
    public List<Sku> getSkus() {
        return skus;
    }

    @JsonProperty("skus")
    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @JsonProperty("update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
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
