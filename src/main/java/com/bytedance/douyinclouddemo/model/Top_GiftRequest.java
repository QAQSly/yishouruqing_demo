package com.bytedance.douyinclouddemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Top_GiftRequest {
    @JsonFormat
    String room_id;
    String app_id;
    String[] sec_gift_id_list;
}
