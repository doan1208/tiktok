
package com.dle.bean.order.detail;

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
    "address_detail",
    "address_line1",
    "address_line2",
    "address_line3",
    "address_line4",
    "district_info",
    "full_address",
    "name",
    "phone_number",
    "postal_code",
    "region_code"
})
public class RecipientAddress implements Serializable
{

    @JsonProperty("address_detail")
    private String addressDetail;
    @JsonProperty("address_line1")
    private String addressLine1;
    @JsonProperty("address_line2")
    private String addressLine2;
    @JsonProperty("address_line3")
    private String addressLine3;
    @JsonProperty("address_line4")
    private String addressLine4;
    @JsonProperty("district_info")
    private List<DistrictInfo> districtInfo;
    @JsonProperty("full_address")
    private String fullAddress;
    @JsonProperty("name")
    private String name;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("region_code")
    private String regionCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -7817194346889166322L;

    @JsonProperty("address_detail")
    public String getAddressDetail() {
        return addressDetail;
    }

    @JsonProperty("address_detail")
    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    @JsonProperty("address_line1")
    public String getAddressLine1() {
        return addressLine1;
    }

    @JsonProperty("address_line1")
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @JsonProperty("address_line2")
    public String getAddressLine2() {
        return addressLine2;
    }

    @JsonProperty("address_line2")
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @JsonProperty("address_line3")
    public String getAddressLine3() {
        return addressLine3;
    }

    @JsonProperty("address_line3")
    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    @JsonProperty("address_line4")
    public String getAddressLine4() {
        return addressLine4;
    }

    @JsonProperty("address_line4")
    public void setAddressLine4(String addressLine4) {
        this.addressLine4 = addressLine4;
    }

    @JsonProperty("district_info")
    public List<DistrictInfo> getDistrictInfo() {
        return districtInfo;
    }

    @JsonProperty("district_info")
    public void setDistrictInfo(List<DistrictInfo> districtInfo) {
        this.districtInfo = districtInfo;
    }

    @JsonProperty("full_address")
    public String getFullAddress() {
        return fullAddress;
    }

    @JsonProperty("full_address")
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("postal_code")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("postal_code")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty("region_code")
    public String getRegionCode() {
        return regionCode;
    }

    @JsonProperty("region_code")
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
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
