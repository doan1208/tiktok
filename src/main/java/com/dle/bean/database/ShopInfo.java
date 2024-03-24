package com.dle.bean.database;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "SHOP_INFO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopInfo implements Serializable {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "app_key")
    private String appKey;

    @Column(name = "app_secret")
    private String appSecret;

    @Column(name = "auth_code")
    private String authCode;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "access_token_expire_in")
    private Long accessTokenExpireIn;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expire_in")
    private Long refreshTokenExpireIn;

    @Column(name = "open_id")
    private String openId;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "seller_base_region")
    private String sellerBaseRegion;

    @Column(name = "user_type")
    private Long userType;

    @Column(name = "cipher")
    private String cipher;

    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "region")
    private String region;

    @Column(name = "seller_type")
    private String sellerType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getAccessTokenExpireIn() {
        return accessTokenExpireIn;
    }

    public void setAccessTokenExpireIn(Long accessTokenExpireIn) {
        this.accessTokenExpireIn = accessTokenExpireIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getRefreshTokenExpireIn() {
        return refreshTokenExpireIn;
    }

    public void setRefreshTokenExpireIn(Long refreshTokenExpireIn) {
        this.refreshTokenExpireIn = refreshTokenExpireIn;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerBaseRegion() {
        return sellerBaseRegion;
    }

    public void setSellerBaseRegion(String sellerBaseRegion) {
        this.sellerBaseRegion = sellerBaseRegion;
    }

    public Long getUserType() {
        return userType;
    }

    public void setUserType(Long userType) {
        this.userType = userType;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSellerType() {
        return sellerType;
    }

    public void setSellerType(String sellerType) {
        this.sellerType = sellerType;
    }
}
