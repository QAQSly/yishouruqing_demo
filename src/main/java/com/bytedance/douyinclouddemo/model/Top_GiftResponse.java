package com.bytedance.douyinclouddemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Top_GiftResponse {
    @JsonFormat
    @JsonProperty("errcode")
    Integer err_code;
    @JsonProperty("errmsg")
    String err_msg;
    @JsonProperty("error")
    Integer logid;
    @JsonProperty("message")
    String sec_gift_id_list;
    
    public void StringToOwn(Top_GiftResponse own)
    {
        this.err_code = own.err_code;
        this.err_msg = own.err_msg;
        this.logid = own.logid;
        this.sec_gift_id_list = own.sec_gift_id_list;
    }
}
