
package com.dle.bean.order;

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
    "next_page_token",
    "orders",
    "total_count"
})
public class Data implements Serializable
{

    @JsonProperty("next_page_token")
    private String nextPageToken;
    @JsonProperty("orders")
    private List<Order> orders;
    @JsonProperty("total_count")
    private Long totalCount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 3870479583133268462L;

    @JsonProperty("next_page_token")
    public String getNextPageToken() {
        return nextPageToken;
    }

    @JsonProperty("next_page_token")
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @JsonProperty("orders")
    public List<Order> getOrders() {
        return orders;
    }

    @JsonProperty("orders")
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @JsonProperty("total_count")
    public Long getTotalCount() {
        return totalCount;
    }

    @JsonProperty("total_count")
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
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
