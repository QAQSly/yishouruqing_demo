package com.bytedance.douyinclouddemo.controller;

import com.bytedance.douyinclouddemo.model.JsonResponse;
import com.bytedance.douyinclouddemo.model.TextAntidirt;
import com.bytedance.douyinclouddemo.model.TextAntidirtRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping
public class HelloController {
    @Value("${app.app_id}")
    private String appId;
    @Value("${app.app_secret}")
    private String appSecret;
    @Value("${app.app_grant_type}")
    private String appGrant_Type;

    @GetMapping("/api/get_open_id")
    public JsonResponse getOpenID(@RequestHeader("X-TT-OPENID") String openID) {
        JsonResponse response = new JsonResponse();
        if(openID.isEmpty()){
            response.failure("openid is empty");
        }else{
            response.success(openID);
        }
        return response;
    }
    
    @RequestMapping(value = "api/getAccessToken", method = {RequestMethod.HEAD, RequestMethod.POST} )
    public JsonResponse getAccessToken() throws JsonProcessingException {
        String url = "https://developer.toutiao.com/api/apps/v2/token";
        // 构建发送响应等
        JsonResponse response = new JsonResponse();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        // 构建请求体
        System.out.println(appId + " and " + appSecret + " and " + appGrant_Type + " and " + headers.getContentType());
        map.add("appid", appId);
        map.add("secret", appSecret);
        map.add("grant_type", appGrant_Type);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(map);
        HttpEntity<String> request = new HttpEntity<>(jsonString, headers);
        
        // 发送请求
        try {
            ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
            // 解析响应
            String responseBody = result.getBody();
            if (result.getStatusCode() == HttpStatus.OK) {
                response.success(responseBody);
            } else {
                response.failure("Failed");
            }
        } catch (Exception e) {
            response.setErrNo(500);
            response.setErrMsg("Failed to fetch access token: " + e.getMessage());
        }
        return response;
      
    }
    @RequestMapping(value = "/api/post", method = {RequestMethod.HEAD, RequestMethod.POST})
    public JsonResponse receiveAndProcessJsonData(@RequestBody(required = false) String jsonData)
    {
        JsonResponse response = new JsonResponse();
        response.success(jsonData);
        //System.out.println(jsonData);
        return response;
    }

    @RequestMapping(value = "/live_data_callback", method = {RequestMethod.HEAD, RequestMethod.POST})
    public JsonResponse liveDataCallBack(@RequestBody(required = false) String jsonData)
    {
        JsonResponse response = new JsonResponse();
        response.success(jsonData);
        //System.out.println(jsonData);
        return response;
    }
    
    @PostMapping("/live_data/task/start")
    public JsonResponse startTask(@RequestBody(required = false) String jsonData) {
        JsonResponse response = new JsonResponse();
        response.success(jsonData);
        //System.out.println(jsonData);
        return response;
    }
    
    @PostMapping("/api/gift/top_gift")
    public JsonResponse topGift(@RequestBody(required = false) String jsonData) {
        JsonResponse response = new JsonResponse();
        response.success(jsonData);
        //System.out.println(jsonData);
        return response;
    }

    @PostMapping("/web_socket/on_connect/v2")
    public JsonResponse ws(@RequestBody(required = false) String jsonData) {
        JsonResponse response = new JsonResponse();
        response.success(jsonData);
        //System.out.println(jsonData);
        return response;
    }
    
    
    

    @PostMapping("/api/text/antidirt")
    public JsonResponse textAntidirt(@RequestBody TextAntidirtRequest textAntidirtRequest) throws JsonProcessingException {

        TextAntidirt textAntidirt = new TextAntidirt(textAntidirtRequest.getContent());

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonString = objectMapper.writeValueAsString(textAntidirt);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

        String url = "http://developer.toutiao.com/api/v2/tags/text/antidirt";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JsonResponse response = new JsonResponse();
        response.success(responseBody);
        return response;
    }
}
