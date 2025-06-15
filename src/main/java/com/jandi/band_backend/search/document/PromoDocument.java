package com.jandi.band_backend.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(indexName = "promos", createIndex = true)
public class PromoDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String teamName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String location;

    @Field(type = FieldType.Keyword)
    private String address;

    @Field(type = FieldType.Double)
    private BigDecimal latitude;

    @Field(type = FieldType.Double)
    private BigDecimal longitude;

    @Field(type = FieldType.Double)
    private BigDecimal admissionFee;

    @Field(type = FieldType.Date)
    private LocalDateTime eventDatetime;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Integer)
    private Integer likeCount;

    @Field(type = FieldType.Keyword)
    private String imageUrl;

    // 기본 생성자
    public PromoDocument() {}

    // 전체 생성자
    public PromoDocument(String id, String title, String teamName, String description,
                        String location, String address, BigDecimal latitude, BigDecimal longitude,
                        BigDecimal admissionFee, LocalDateTime eventDatetime, LocalDateTime createdAt,
                        LocalDateTime updatedAt, Integer likeCount, String imageUrl) {
        this.id = id;
        this.title = title;
        this.teamName = teamName;
        this.description = description;
        this.location = location;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.admissionFee = admissionFee;
        this.eventDatetime = eventDatetime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
        this.imageUrl = imageUrl;
    }

    // Getter와 Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public BigDecimal getAdmissionFee() { return admissionFee; }
    public void setAdmissionFee(BigDecimal admissionFee) { this.admissionFee = admissionFee; }

    public LocalDateTime getEventDatetime() { return eventDatetime; }
    public void setEventDatetime(LocalDateTime eventDatetime) { this.eventDatetime = eventDatetime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 