package com.bytedance.douyinclouddemo.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.HashMap;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Fans_ClubData extends HashMap<String, Fans_ClubMemberData> {
}
