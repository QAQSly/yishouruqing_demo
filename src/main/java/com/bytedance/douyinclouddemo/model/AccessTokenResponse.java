package com.bytedance.douyinclouddemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccessTokenResponse {
    @JsonFormat
    Integer errNo;
    String errTips;
    AccessTokenData data;
    
    
    
    
   public void StringToOwn(AccessTokenResponse own) {
        
            
            this.errNo = own.getErrNo();
            this.errTips = own.getErrTips();
            this.data = own.getData();
            this.data.access_token = own.getData().getAccess_token();
            this.data.expires_in = own.getData().getExpires_in();
            this.data.expiresAt = own.getData().getExpiresAt();
            this.data.expires_at = own.getData().getExpires_at();
    }
    
   
}

