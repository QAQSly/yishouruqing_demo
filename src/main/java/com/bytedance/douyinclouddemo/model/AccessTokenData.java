package com.bytedance.douyinclouddemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccessTokenData {
    @JsonFormat()
    String access_token;
    Integer expires_in;
    @JsonProperty("expiresAt")
    Integer expiresAt;
    @JsonProperty("expires_at")
    Integer expires_at;
    
}