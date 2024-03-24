
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
    "position",
    "reasons",
    "suggestions"
})
public class AuditFailedReason implements Serializable
{

    @JsonProperty("position")
    private String position;
    @JsonProperty("reasons")
    private List<String> reasons;
    @JsonProperty("suggestions")
    private List<String> suggestions;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 7370747386823598212L;

    @JsonProperty("position")
    public String getPosition() {
        return position;
    }

    @JsonProperty("position")
    public void setPosition(String position) {
        this.position = position;
    }

    @JsonProperty("reasons")
    public List<String> getReasons() {
        return reasons;
    }

    @JsonProperty("reasons")
    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    @JsonProperty("suggestions")
    public List<String> getSuggestions() {
        return suggestions;
    }

    @JsonProperty("suggestions")
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
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
