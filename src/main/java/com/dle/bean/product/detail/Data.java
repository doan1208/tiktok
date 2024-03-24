
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
    "audit_failed_reasons",
    "brand",
    "category_chains",
    "certifications",
    "create_time",
    "delivery_options",
    "description",
    "external_product_id",
    "id",
    "is_cod_allowed",
    "main_images",
    "package_dimensions",
    "package_weight",
    "product_attributes",
    "size_chart",
    "skus",
    "status",
    "title",
    "update_time",
    "video"
})
public class Data implements Serializable
{

    @JsonProperty("audit_failed_reasons")
    private List<AuditFailedReason> auditFailedReasons;
    @JsonProperty("brand")
    private Brand brand;
    @JsonProperty("category_chains")
    private List<CategoryChain> categoryChains;
    @JsonProperty("certifications")
    private List<Certification> certifications;
    @JsonProperty("create_time")
    private Long createTime;
    @JsonProperty("delivery_options")
    private List<DeliveryOption> deliveryOptions;
    @JsonProperty("description")
    private String description;
    @JsonProperty("external_product_id")
    private String externalProductId;
    @JsonProperty("id")
    private String id;
    @JsonProperty("is_cod_allowed")
    private Boolean isCodAllowed;
    @JsonProperty("main_images")
    private List<MainImage> mainImages;
    @JsonProperty("package_dimensions")
    private PackageDimensions packageDimensions;
    @JsonProperty("package_weight")
    private PackageWeight packageWeight;
    @JsonProperty("product_attributes")
    private List<ProductAttribute> productAttributes;
    @JsonProperty("size_chart")
    private SizeChart sizeChart;
    @JsonProperty("skus")
    private List<Sku> skus;
    @JsonProperty("status")
    private String status;
    @JsonProperty("title")
    private String title;
    @JsonProperty("update_time")
    private Long updateTime;
    @JsonProperty("video")
    private Video video;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -1259519645563188895L;

    @JsonProperty("audit_failed_reasons")
    public List<AuditFailedReason> getAuditFailedReasons() {
        return auditFailedReasons;
    }

    @JsonProperty("audit_failed_reasons")
    public void setAuditFailedReasons(List<AuditFailedReason> auditFailedReasons) {
        this.auditFailedReasons = auditFailedReasons;
    }

    @JsonProperty("brand")
    public Brand getBrand() {
        return brand;
    }

    @JsonProperty("brand")
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @JsonProperty("category_chains")
    public List<CategoryChain> getCategoryChains() {
        return categoryChains;
    }

    @JsonProperty("category_chains")
    public void setCategoryChains(List<CategoryChain> categoryChains) {
        this.categoryChains = categoryChains;
    }

    @JsonProperty("certifications")
    public List<Certification> getCertifications() {
        return certifications;
    }

    @JsonProperty("certifications")
    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }

    @JsonProperty("create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @JsonProperty("create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @JsonProperty("delivery_options")
    public List<DeliveryOption> getDeliveryOptions() {
        return deliveryOptions;
    }

    @JsonProperty("delivery_options")
    public void setDeliveryOptions(List<DeliveryOption> deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("external_product_id")
    public String getExternalProductId() {
        return externalProductId;
    }

    @JsonProperty("external_product_id")
    public void setExternalProductId(String externalProductId) {
        this.externalProductId = externalProductId;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("is_cod_allowed")
    public Boolean getIsCodAllowed() {
        return isCodAllowed;
    }

    @JsonProperty("is_cod_allowed")
    public void setIsCodAllowed(Boolean isCodAllowed) {
        this.isCodAllowed = isCodAllowed;
    }

    @JsonProperty("main_images")
    public List<MainImage> getMainImages() {
        return mainImages;
    }

    @JsonProperty("main_images")
    public void setMainImages(List<MainImage> mainImages) {
        this.mainImages = mainImages;
    }

    @JsonProperty("package_dimensions")
    public PackageDimensions getPackageDimensions() {
        return packageDimensions;
    }

    @JsonProperty("package_dimensions")
    public void setPackageDimensions(PackageDimensions packageDimensions) {
        this.packageDimensions = packageDimensions;
    }

    @JsonProperty("package_weight")
    public PackageWeight getPackageWeight() {
        return packageWeight;
    }

    @JsonProperty("package_weight")
    public void setPackageWeight(PackageWeight packageWeight) {
        this.packageWeight = packageWeight;
    }

    @JsonProperty("product_attributes")
    public List<ProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    @JsonProperty("product_attributes")
    public void setProductAttributes(List<ProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
    }

    @JsonProperty("size_chart")
    public SizeChart getSizeChart() {
        return sizeChart;
    }

    @JsonProperty("size_chart")
    public void setSizeChart(SizeChart sizeChart) {
        this.sizeChart = sizeChart;
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

    @JsonProperty("video")
    public Video getVideo() {
        return video;
    }

    @JsonProperty("video")
    public void setVideo(Video video) {
        this.video = video;
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
