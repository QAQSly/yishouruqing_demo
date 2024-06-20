package com.bytedance.douyinclouddemo.controller;

import com.bytedance.douyinclouddemo.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping
public class MainController {
    // ------ 平台验证access token ------
    @Value("${app.app_id}")
    private String appId;
    @Value("${app.app_secret}")
    private String appSecret;
    @Value("${app.app_grant_type}")
    private String appGrant_Type;
    
    // ------ 置顶礼物 ------
    @Value("${app.app_room_id}")
    private String appRoomId;
    @Value("${app.app_sec_gift_id_list}")
    private String[] appSecGiftIdList;
    
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // --------从请求头获取openid-------
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
    
    private String accessToken = "0801121846397069314539614255336d5278303275624e4a54673d3d";
    // ------获取access token-------
    // @PostMapping("api/getAccessToken")
    @RequestMapping(value = "/api/getAccessToken", method = {RequestMethod.HEAD, RequestMethod.POST})
    public String getAccessToken() throws JsonProcessingException {
        String url = "https://minigame.zijieapi.com/mgplatform/api/apps/v2/token";
        // 构建发送响应等
        AccessTokenResponse own = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> map = new HashMap<>();
        // 构建请求体
        System.out.println("------请求体" + appId + " and " + appSecret + " and " + appGrant_Type + " and " + headers.getContentType());
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("grant_type", appGrant_Type);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
        
        // 发送请求
        String responseBody = null;
        try {
            ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
            // 解析响应
            responseBody = result.getBody();
            System.out.println("------响应体" + responseBody);
            own = new ObjectMapper().readValue(responseBody, AccessTokenResponse.class);
            own.StringToOwn(own);
        } catch (IOException e) {
            e.printStackTrace();
        }
        accessToken = own.getData().getAccess_token();
        // System.out.println("accessToken = " + accessToken);
        return responseBody;
      
    }
    // ------ 接收post请求 测试用------
    @RequestMapping(value = "/api/post", method = {RequestMethod.HEAD, RequestMethod.POST})
    public AccessTokenResponse receiveAndProcessJsonData(@RequestBody(required = false) String jsonData)
    {
        
        ObjectMapper mapper = new ObjectMapper();
        AccessTokenResponse own = null;
        try {
            own = mapper.readValue(jsonData, AccessTokenResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        own.StringToOwn(own);
        //System.out.println(jsonData);
        return own;
    }
    @RequestMapping(value = "/v1/ping", method = {RequestMethod.HEAD, RequestMethod.GET})
    public String getPing(@RequestBody(required = false) String jsonData)
    {

        String str = jsonData;
        //System.out.println(jsonData);
        return str;
    }
    
    private String temp;

    // ------ 直播数据回调 ------
    @RequestMapping(value = "/live_data_callback", method = {RequestMethod.HEAD, RequestMethod.POST})
    public String liveDataCallBack(@RequestBody(required = false) String jsonData)
    {
        String url = "https://webcast.bytedance.com/api/live_data/task/start";
        RestTemplate restTemplate = new RestTemplate();
        // JsonResponse response = new JsonResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access-token", accessToken = "0801121846397069314539614255336d5278303275624e4a54673d3d");
        TaskStartRequest taskStartRequest = new TaskStartRequest();
        taskStartRequest.setApp_id(appId);
        taskStartRequest.setRoomid(appRoomId);
        taskStartRequest.setMsg_type("live_comment");
        HttpEntity<TaskStartRequest> requestHttpEntity = new HttpEntity<>(taskStartRequest, headers);

        ResponseEntity<String> result = restTemplate.postForEntity(url, requestHttpEntity, String.class);

        TaskStartResponse response = null;
        // System.out.println("------task start -------" + result.getBody());
        String responseBody = null;
        try {
            responseBody = result.getBody();
            response = new ObjectMapper().readValue(responseBody, TaskStartResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(jsonData);
        return responseBody;
            // 客户端
    }
    
    // ------ 开启任务推送 ------
    // @PostMapping("/live_data/task/start")
    @RequestMapping(value = "/api/live_data/task/start", method = {RequestMethod.HEAD, RequestMethod.POST}) 
    public String startTask(@RequestBody(required = false) String jsonData) {
        // String url = "https://webcast.bytedance.com/api/live_data/task/start";
        String url = "https://webcast.bytedance.com/api/live_data/task/start";
        RestTemplate restTemplate = new RestTemplate();
        // JsonResponse response = new JsonResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access-token", accessToken = "0801121846397069314539614255336d5278303275624e4a54673d3d");
        TaskStartRequest taskStartRequest = new TaskStartRequest();
        taskStartRequest.setApp_id(appId);
        taskStartRequest.setRoomid(appRoomId);
        taskStartRequest.setMsg_type("live_comment");
        HttpEntity<TaskStartRequest> requestHttpEntity = new HttpEntity<>(taskStartRequest, headers);
        Map<String, String> map = new HashMap<>();
        map.put("app_id", appId);
        ResponseEntity<String> result = restTemplate.postForEntity(url, requestHttpEntity, String.class);
        
        TaskStartResponse response = null;
        // System.out.println("------task start -------" + result.getBody());
        String responseBody = null;
        try {
            responseBody = result.getBody();
            response = new ObjectMapper().readValue(responseBody, TaskStartResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(jsonData);
        return responseBody;
    }
   
    private String top_gift_temp;
    // ------ 置顶礼物 ------
    //  @PostMapping("/api/gift/top_gift")
    @RequestMapping(value = "/api/gift/top_gift", method = {RequestMethod.HEAD, RequestMethod.POST})
    public String topGift(@RequestBody(required = false) String jsonData) {
        String url = "https://webcast.bytedance.com/api/gift/top_gift";
        // JsonResponse response = new JsonResponse();
        Top_GiftResponse response = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-token", accessToken = "0801121846397069314539614255336d5278303275624e4a54673d3d");
        
        //  Map<String, String> map = new HashMap<>();
        
        Top_GiftRequest top_gift_request =  new Top_GiftRequest();
        top_gift_request.setApp_id(appId);
        top_gift_request.setRoom_id(appRoomId);
        top_gift_request.setSec_gift_id_list(appSecGiftIdList);
        
        HttpEntity<Top_GiftRequest> request = new HttpEntity<>(top_gift_request, headers);
        
        String responseBody = null;
        try {
            ResponseEntity<String> result = restTemplate.postForEntity(url,  request, String.class);
            responseBody = result.getBody();
            // System.out.println("top_gift 响应体 ------ " + responseBody);
            response = new ObjectMapper().readValue(responseBody, Top_GiftResponse.class);
            response.StringToOwn(response);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(jsonData);
        return responseBody;
    }
    

    // ------ web_socket ------
    // @PostMapping("/web_socket/on_connect/v2")
    @RequestMapping(value = "/web_socket/on_connect/v2", method = {RequestMethod.HEAD, RequestMethod.POST})
    public JsonResponse ws(@RequestBody(required = false) String jsonData) {
        JsonResponse response = new JsonResponse();
        response.success(jsonData);
        //System.out.println(jsonData);
        return response;
    }
    
    
    
    // ------脏文本------
    // @PostMapping("/api/text/antidirt")
    @RequestMapping(value = "/api/text/antidirt", method = {RequestMethod.HEAD, RequestMethod.POST})
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
   
    // ------停止推送------
    // @PostMapping("/live_data/task/stop")
    @RequestMapping(value = "/api/live_data/task/stop", method = {RequestMethod.HEAD, RequestMethod.POST})
    public String TaskStop() {
        String url = "https://webcast.bytedance.com/api/live_data/task/stop";
        RestTemplate restTemplate = new RestTemplate();
        // JsonResponse response = new JsonResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access-token", accessToken);
        TaskStartRequest taskStartRequest = new TaskStartRequest();
        taskStartRequest.setApp_id(appId);
        taskStartRequest.setRoomid(appRoomId);
        taskStartRequest.setMsg_type("live_comment");
        HttpEntity<TaskStartRequest> requestHttpEntity = new HttpEntity<>(taskStartRequest, headers);

        ResponseEntity<String> result = restTemplate.postForEntity(url, requestHttpEntity, String.class);

        TaskStopResponse response = null;
        // System.out.println("------task start -------" + result.getBody());
        String str = null;
        try {
            str = result.getBody();
            response = new ObjectMapper().readValue(str, TaskStopResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(jsonData);
        return str; 
    }
    
    // ------获取任务状态------
   // @GetMapping("/live_data/task/status")
   @RequestMapping(value = "/api/live_data/task/get", method = {RequestMethod.HEAD, RequestMethod.GET})
   public String TaskStatus() {
        String url = "https://webcast.bytedance.com/api/live_data/task/get";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access-token", accessToken = "0801121846397069314539614255336d5278303275624e4a54673d3d");
        TaskStatusRequest taskStatusRequest = new TaskStatusRequest();
        taskStatusRequest.setAppid(appId);
        taskStatusRequest.setRoomid(appRoomId);
        taskStatusRequest.setMsg_type("live_comment");
        HttpEntity<TaskStatusRequest> requestHttpEntity = new HttpEntity<>(taskStatusRequest, headers);
        // System.out.println("------requestHttpEntity------" + requestHttpEntity);
        TaskStatusResponse response = null;
        String str = null;
        try {
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class, requestHttpEntity);
            str = result.getBody();
            response = new ObjectMapper().readValue(str, TaskStatusResponse.class);
            System.out.println("------task status -------" + result.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
   }
   
   // ------获取失败数据------
  //  @GetMapping("/live_data/task/fail_data/get")
   @RequestMapping(value = "/api/live_data/task/fail_data/get", method = {RequestMethod.HEAD, RequestMethod.GET})
    public String fail_data() {
        String url = "https://webcast.bytedance.com/api/live_data/task/fail_data/get";
       RestTemplate restTemplate = new RestTemplate();
       HttpHeaders headers = new HttpHeaders();
       headers.clear();
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.set("access-token", accessToken = "0801121846397069314539614255336d5278303275624e4a54673d3d");
       Fail_DataRequest fail_DataRequest = new Fail_DataRequest();
       fail_DataRequest.setRoomid(appRoomId);
       fail_DataRequest.setAppid(appId);
       fail_DataRequest.setPage_num(1);
       fail_DataRequest.setPage_size(5);
       fail_DataRequest.setMsg_type("live_comment");
       HttpEntity<Fail_DataRequest> requestHttpEntity = new HttpEntity<>(fail_DataRequest, headers);
       // System.out.println("------requestHttpEntity------" + requestHttpEntity);
       Fail_DataResponse response = null;
       String str = null;
       try {
           ResponseEntity<String> result = restTemplate.getForEntity(url, String.class, requestHttpEntity);
           str = result.getBody();
           response = new ObjectMapper().readValue(str, Fail_DataResponse.class);
           System.out.println("------task status -------" + result.getBody());
       } catch (IOException e) {
           e.printStackTrace();
       }
       return str;
   }
   
   // ------粉丝团------
   // @GetMapping("/live_data/fans_club/get_info")
   @RequestMapping(value = "/api/live_data/fans_club/get_info", method = {RequestMethod.HEAD, RequestMethod.GET})
   public String Fans_ClubInfo() {
       String url = "https://webcast.bytedance.com/api/live_data/fans_club/get_info";
       RestTemplate restTemplate = new RestTemplate();
       HttpHeaders headers = new HttpHeaders();
       headers.clear();
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.set("access-token", accessToken = "0801121846397069314539614255336d5278303275624e4a54673d3d");
       Fans_ClubRequest fans_ClubRequest = new Fans_ClubRequest();
       fans_ClubRequest.setRoomid(appRoomId);
       fans_ClubRequest.setAnchor_openid("1001");
       fans_ClubRequest.setUser_openids("1,2");
       
       HttpEntity<Fans_ClubRequest> requestHttpEntity = new HttpEntity<>(fans_ClubRequest, headers);
       // System.out.println("------requestHttpEntity------" + requestHttpEntity);
       Fans_ClubResponse response = null;
       String str = null;
       try {
           ResponseEntity<String> result = restTemplate.getForEntity(url, String.class, requestHttpEntity);
           str = result.getBody();
           response = new ObjectMapper().readValue(str, Fans_ClubResponse.class);
           System.out.println("------task status -------" + result.getBody());
       } catch (IOException e) {
           e.printStackTrace();
       }
       return str;
   }
   

   
        
}
