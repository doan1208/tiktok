package com.dle.bean;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "code",
        "data",
        "message",
        "request_id"
})
public class BaseResponse<D> implements Serializable {

    @JsonProperty("code")
    protected Integer code;

    @JsonProperty("message")
    protected String message;

    @JsonProperty("data")
    protected D data;

    @JsonProperty("request_id")
    protected String requestId;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 791389870556444957L;

    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(Integer code) {
        this.code = code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("data")
    public D getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(D data) {
        this.data = data;
    }

    @JsonProperty("request_id")
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty("request_id")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public <T extends BaseResponse<D>> T copyToChild(T child){
        child.setCode(this.code);
        child.setMessage(this.message);
        child.setRequestId(this.requestId);
        child.setData(this.getData());
        return child;
    }

}
