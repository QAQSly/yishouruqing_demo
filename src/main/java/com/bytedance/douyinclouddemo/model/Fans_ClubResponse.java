package com.bytedance.douyinclouddemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Fans_ClubResponse {
    @JsonFormat
    String err_msg;
    Integer err_no;
    String logid;
    Fans_ClubData data;
}
