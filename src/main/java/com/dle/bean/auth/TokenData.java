
package com.dle.bean.auth;

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
        "access_token",
        "access_token_expire_in",
        "refresh_token",
        "refresh_token_expire_in",
        "open_id",
        "seller_name",
        "seller_base_region",
        "user_type"
})
public class TokenData implements Serializable {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("access_token_expire_in")
    private Long accessTokenExpireIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_token_expire_in")
    private Long refreshTokenExpireIn;
    @JsonProperty("open_id")
    private String openId;
    @JsonProperty("seller_name")
    private String sellerName;
    @JsonProperty("seller_base_region")
    private String sellerBaseRegion;
    @JsonProperty("user_type")
    private Long userType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -1790545495649225936L;

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("access_token_expire_in")
    public Long getAccessTokenExpireIn() {
        return accessTokenExpireIn;
    }

    @JsonProperty("access_token_expire_in")
    public void setAccessTokenExpireIn(Long accessTokenExpireIn) {
        this.accessTokenExpireIn = accessTokenExpireIn;
    }

    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    @JsonProperty("refresh_token")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @JsonProperty("refresh_token_expire_in")
    public Long getRefreshTokenExpireIn() {
        return refreshTokenExpireIn;
    }

    @JsonProperty("refresh_token_expire_in")
    public void setRefreshTokenExpireIn(Long refreshTokenExpireIn) {
        this.refreshTokenExpireIn = refreshTokenExpireIn;
    }

    @JsonProperty("open_id")
    public String getOpenId() {
        return openId;
    }

    @JsonProperty("open_id")
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @JsonProperty("seller_name")
    public String getSellerName() {
        return sellerName;
    }

    @JsonProperty("seller_name")
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    @JsonProperty("seller_base_region")
    public String getSellerBaseRegion() {
        return sellerBaseRegion;
    }

    @JsonProperty("seller_base_region")
    public void setSellerBaseRegion(String sellerBaseRegion) {
        this.sellerBaseRegion = sellerBaseRegion;
    }

    @JsonProperty("user_type")
    public Long getUserType() {
        return userType;
    }

    @JsonProperty("user_type")
    public void setUserType(Long userType) {
        this.userType = userType;
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