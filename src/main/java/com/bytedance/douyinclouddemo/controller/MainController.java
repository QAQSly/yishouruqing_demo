package com.bytedance.douyinclouddemo.controller;

import com.bytedance.douyinclouddemo.help.SignatureHelper;
import com.bytedance.douyinclouddemo.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
// import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.util.*;

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
    
   /* @Value("${app.private_key_pem}")
    private String private_Key;*/
    
    
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
    
    private String accessToken = "080112184639717a442f524166705146746d51757050362f30513d3d";
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
    private String temp = "";
    @RequestMapping(value = "/api/post", method = {RequestMethod.HEAD, RequestMethod.POST})
    public String receiveAndProcessJsonData(@RequestBody String jsonData)
    {
        
        System.out.println("---推送数据:---" + jsonData);
        if (jsonData == null) {
            return temp;
        }
        if (jsonData != null) {
            temp = jsonData;
        }
        System.out.println("---缓存数据---" + temp);
        
        return temp;
    }
    @RequestMapping(value = "/v1/ping", method = {RequestMethod.HEAD, RequestMethod.GET})
    public String getPing(@RequestBody(required = false) String jsonData)
    {

        String str = jsonData;
        //System.out.println(jsonData);
        return str;
    }
    
    // private String temp;

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
        headers.set("access-token", accessToken);
        TaskStartRequest taskStartRequest = new TaskStartRequest();
        taskStartRequest.setAppid(appId);
        taskStartRequest.setRoomid(appRoomId);
        taskStartRequest.setMsg_type("live_comment");
        HttpEntity<TaskStartRequest> requestHttpEntity = new HttpEntity<>(taskStartRequest, headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestHttpEntity, String.class);

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
        headers.set("access-token", accessToken);
        TaskStartRequest taskStartRequest = new TaskStartRequest();
        taskStartRequest.setAppid(appId);
        taskStartRequest.setRoomid(appRoomId);
        taskStartRequest.setMsg_type("live_comment");
        HttpEntity<TaskStartRequest> requestHttpEntity = new HttpEntity<>(taskStartRequest, headers);
        Map<String, String> map = new HashMap<>();
        map.put("app_id", appId);
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestHttpEntity, String.class);
        
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
        headers.set("x-token", accessToken);
        
        //  Map<String, String> map = new HashMap<>();
        
        Top_GiftRequest top_gift_request =  new Top_GiftRequest();
        top_gift_request.setApp_id(appId);
        top_gift_request.setRoom_id(appRoomId);
        top_gift_request.setSec_gift_id_list(appSecGiftIdList);
        
        HttpEntity<Top_GiftRequest> request = new HttpEntity<>(top_gift_request, headers);
        
        String responseBody = null;
        try {
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST,  request, String.class);
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
        taskStartRequest.setAppid(appId);
        taskStartRequest.setRoomid(appRoomId);
        taskStartRequest.setMsg_type("live_comment");
        HttpEntity<TaskStartRequest> requestHttpEntity = new HttpEntity<>(taskStartRequest, headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestHttpEntity, String.class);

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
        String baseUrl = "https://webcast.bytedance.com/api/live_data/task/get";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("appid", appId)
                .queryParam("roomid", appRoomId)
                .queryParam("msg_type", "live_comment");
        String url = builder.toUriString();
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access-token", accessToken);
        TaskStatusRequest taskStatusRequest = new TaskStatusRequest();
        taskStatusRequest.setAppid(appId);
        taskStatusRequest.setRoomid(appRoomId);
        taskStatusRequest.setMsg_type("live_comment");
        HttpEntity<?> requestHttpEntity = new HttpEntity<>( headers);
        // System.out.println("------requestHttpEntity------" + requestHttpEntity);
        TaskStatusResponse response = null;
        String str = null;
        try {
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, requestHttpEntity, String.class);
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
        String baseUrl = "https://webcast.bytedance.com/api/live_data/task/fail_data/get";
       UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
               .queryParam("roomid", appRoomId)
               .queryParam("appid", appId)
               .queryParam("msg_type", "live_comment")
               .queryParam("page_num", 1)
               .queryParam("page_size", 5);
       String url = builder.toUriString();
       
       RestTemplate restTemplate = new RestTemplate();
       HttpHeaders headers = new HttpHeaders();
       headers.clear();
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.set("access-token", accessToken );
      /* Fail_DataRequest fail_DataRequest = new Fail_DataRequest();
       fail_DataRequest.setRoomid(appRoomId);
       fail_DataRequest.setAppid(appId);
       fail_DataRequest.setPage_num(1);
       fail_DataRequest.setPage_size(5);
       fail_DataRequest.setMsg_type("live_comment");*/
       HttpEntity<?> requestHttpEntity = new HttpEntity<>( headers);
       System.out.println("------requestHttpEntity------" + requestHttpEntity);
     
       
       Fail_DataResponse response = null;
       String str = null;
       try {
           ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, requestHttpEntity, String.class);
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
       String baseUrl = "https://webcast.bytedance.com/api/live_data/fans_club/get_info";
       UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
               .queryParam("roomid", appRoomId)
               .queryParam("anchor_opneid", "1001")
               .queryParam("user_openids", "1, 2");
       String url = builder.toUriString();
       RestTemplate restTemplate = new RestTemplate();
       HttpHeaders headers = new HttpHeaders();
       headers.clear();
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.set("access-token", accessToken);
       Fans_ClubRequest fans_ClubRequest = new Fans_ClubRequest();
       fans_ClubRequest.setRoomid(appRoomId);
       fans_ClubRequest.setAnchor_openid("1001");
       fans_ClubRequest.setUser_openids("1,2");
       
       HttpEntity<?> requestHttpEntity = new HttpEntity<>(headers);
       // System.out.println("------requestHttpEntity------" + requestHttpEntity);
       Fans_ClubResponse response = null;
       String str = null;
       try {
           ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, requestHttpEntity, String.class);
           str = result.getBody();
           response = new ObjectMapper().readValue(str, Fans_ClubResponse.class);
           System.out.println("------task status -------" + result.getBody());
       } catch (IOException e) {
           e.printStackTrace();
       }
       return str;
   }
   
   // ---请求签名
    @RequestMapping(value = "/api/business/diamond/query", method = {RequestMethod.HEAD, RequestMethod.POST})
    public String Signature() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String Path = "https://webcast.bytedance.com/api/business/diamond/query";
        String SignatureUrl = "/api/business/diamond/query";
        // ---请求时间戳
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        // ---请求随机串
        byte[] randomBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        String nonce = Hex.encode(randomBytes).toString();
        // String nonce = UUID.randomUUID().toString();
        // ---请求报文主体
        String body = "{\"appid\": " + appId + ",\"order_id\": " + "1xxx" + "}";
        System.out.println(body);
        // ---构造签名串
        // String signatureStr = "POST\n" + SignatureUrl + "\n" + timestamp + "\n" + nonce + "\n" + body + "\n";
        String signatureStr = String.format("%s\n%s\n%s\n%s\n%s\n", "POST", SignatureUrl, timestamp, nonce, body);
        System.out.println("------signatureStr------" + signatureStr);
        // ---使用应用私钥对签名串进行SHA256-RSA2048签名并对签名结果进行Base64编码
        
        // PrivateKey privateKey = loadPrivateKeyFromPem("classpath:private_key.pem");
        
        
        // --- 签名字符串
     /*   String signatureStrV2 = String.format("{x-nonce-str:%s,x-timestamp:%s,x-roomid:%s,x-msg-type:%s}",
                nonce, timestamp, appRoomId, "live_comment");
        Map<String, String> h = new HashMap<>();
        h.put("x-nonce-str", nonce);
        h.put("x-timestamp", timestamp);
        h.put("x-roomid", appRoomId);
        h.put("x-msg-type", "live_comment");
        String secret = "124";
        String boydStr = "hello";
        
        String signature = null;
        try {
            signature = addSignature(h, boydStr, secret);
            System.out.println("------signature------" + signature);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        
        // ---使用私钥签名
        /*ContentSigner signer = null;
        try {
            signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey); 
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        
        // -- 输出流
        /*OutputStream signatureOutputStream = signer.getOutputStream();
        signatureOutputStream.write(signatureStr.getBytes(StandardCharsets.UTF_8));
        
        byte[] signatureBytes = signer.getSignature();
        signatureOutputStream.flush();
        signatureOutputStream.close();
        
        // ---对签名结果进行Base64编码
        String signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes);
        System.out.println("------signatureBase64------" + signatureBase64);*/
        
        // ---另一种方式
        String signatureBase64 = null;
        
        try {
            signatureBase64 = createSignature(signatureStr, "classpath:private_key.pem");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String version = "1";
        // 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String byte_Authorization = "SHA256-RSA2048 " + String.format("appid=\"%s\",nonce_str=\"%s\",timestamp=\"%s\",key_version=\"%s\",signature=\"%s\"",
                appId, nonce, timestamp, version, signatureBase64);
        System.out.println("------byte_Authorization------" + byte_Authorization);
        
        byte_Authorization = "SHA256-RSA2048 appid=\"tt654b13f6fa7d5b6910\",nonce_str=\"DC10180A100073E70A48F195DA2AF2E6\",timestamp=\"1718957342\",key_version=\"1\",signature=\"KBfswPYy1mOvgkwy6lDb2kLm0QyjrhjhCSjYTJDhAwH7FZhHJGn+8wTxyKFSQzelkqTA0VfJVvxpVof+87Ab/o0XWY8yNry1QQOFTP8x1JYXY5M/SGoai94IoHgAubT6I+4gOAgnxU2eGOUvc4IY7e+wIddn+N1QLngFrFvGXZWMoqWhzXcQMeLqvxM6Q9RzbmFG+gucLRHnAgV3JcutPfUJkwGLDr3PrkSs0gx9Io41DCWJJuX38Nh5oAannX+gA1O8yVs84VyNDdavjlJr/Jg+RbM+tUqcIxLVkWZtONpWQNGv5eZI86ql+AiEd1TFShwRZs7iQrO59zVRjPEr1g==\"";
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Byte-Authorization", byte_Authorization);
        
        // 构造请求体
        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("order_id", "1xxx");
        // 发送POST 请求
        HttpEntity<Map<String, String>> requestHttpEntity = new HttpEntity<>(map, headers);
        System.out.println("------requestHttpEntity------" + requestHttpEntity);
        ResponseEntity<String> result = restTemplate.exchange(Path, HttpMethod.POST, requestHttpEntity, String.class);
        System.out.println("------result------" + result.getBody());
        return result.getBody();
    }
    
    
    // ---签名方法
   /* @Autowired
    private ResourceLoader resourceLoader;
    public PrivateKey loadPrivateKeyFromPem(String pemResourcePath) throws IOException {
        // InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(pemResourcePath);
        try (InputStream resourceStream = resourceLoader.getResource(pemResourcePath).getInputStream()) {
            
            if (resourceStream == null) {
                System.out.println("------pemResourcePath------" + pemResourcePath + " not found");
            }
            PEMParser pemParser = new PEMParser(new InputStreamReader(resourceStream));
            Object object = pemParser.readObject();
            if (!(object instanceof PrivateKeyInfo)) {
                throw new IOException("pem not want file");
            }
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKey privateKey = converter.getPrivateKey(privateKeyInfo);
            pemParser.close();
            return privateKey; 
        } catch (Exception e) {
            throw new IOException("pem not want file", e);
        }
        
    }*/
    // ---资源获取
    @Autowired
    private ResourceLoader resourceLoader;
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
    // ---另一种签名方法
    /*@Autowired
    private ResourceLoader resourceLoader;*/
   /* public PrivateKey loadPrivateKeyFromPemV2(String privateKeyPath) throws Exception {
        Resource resource = resourceLoader.getResource(privateKeyPath);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] keyBytes = StreamUtils.copyToByteArray(inputStream);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }*/

    public PrivateKey loadPrivateKeyFromPemFile(String privateKeyPath) throws IOException {
        Resource resource = getResourceLoader().getResource(privateKeyPath);
        try (PemReader pemReader = new PemReader(new InputStreamReader(resource.getInputStream()))) {
            PemObject pemObject = pemReader.readPemObject();
            if (pemObject == null || !"PRIVATE KEY".equals(pemObject.getType())) {
                throw new IllegalArgumentException("Expected a PRIVATE KEY in PEM format");
            }
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // --- 另一种读取
    public PrivateKey readPrivateKey(String privateKeyPath) throws Exception {

        Resource resource = getResourceLoader().getResource(privateKeyPath);
        StringBuilder keyBuilder = new StringBuilder();    
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                keyBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key = keyBuilder.toString();        
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (PrivateKey) keyFactory.generatePrivate(keySpec);
    }
    public String createSignature(String signatureStr, String privateKeyPath) throws Exception {
        PrivateKey privateKey = loadPrivateKeyFromPemFile(privateKeyPath);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(signatureStr.getBytes());
        byte[] encodedSig = sig.sign();
        return Base64.getEncoder().encodeToString(encodedSig);
    }
    
 

    /**
     * @jdk >= 1.8
     * @param header = {
    "x-nonce-str": "123456",
    "x-timestamp": "456789",
    "x-roomid":    "268",
    "x-msg-type":  "live_gift",
     *                 }
     * @param bodyStr = "abc123你好"
     * @param secret = "123abc"
     * @return PDcKhdlsrKEJif6uMKD2dw==
     */
    public static String addSignature(Map<String, String> header, String bodyStr, String secret) throws Exception {
        List<String> keyList = new ArrayList<>(4);
        header.forEach((key, val) -> keyList.add(key));
        Collections.sort(keyList, String::compareTo);

        List<String> kvList = new ArrayList<>(4);
        for (String key : keyList) {
            kvList.add(key + "=" + header.get(key));
        }
        String urlParams = String.join("&", kvList);
        String rawData = urlParams + bodyStr + secret;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(rawData.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(md.digest());
    }
    
    
}
