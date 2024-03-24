
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
    "height",
    "thumb_urls",
    "uri",
    "urls",
    "width"
})
public class SkuImg implements Serializable
{

    @JsonProperty("height")
    private Long height;
    @JsonProperty("thumb_urls")
    private List<String> thumbUrls;
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("urls")
    private List<String> urls;
    @JsonProperty("width")
    private Long width;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -7542385283324238316L;

    @JsonProperty("height")
    public Long getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(Long height) {
        this.height = height;
    }

    @JsonProperty("thumb_urls")
    public List<String> getThumbUrls() {
        return thumbUrls;
    }

    @JsonProperty("thumb_urls")
    public void setThumbUrls(List<String> thumbUrls) {
        this.thumbUrls = thumbUrls;
    }

    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonProperty("urls")
    public List<String> getUrls() {
        return urls;
    }

    @JsonProperty("urls")
    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @JsonProperty("width")
    public Long getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(Long width) {
        this.width = width;
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
