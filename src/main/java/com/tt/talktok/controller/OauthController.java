package com.tt.talktok.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.service.StudentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/oauth")
public class OauthController {

    private final StudentService studentService;


    /*카카오*/
    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.clientAuthenticationMethod}")
    private String kakaoClientAuthenticationMethod;

    @Value("${spring.security.oauth2.client.registration.kakao.authorizationGrantType}")
    private String kakaoAuthorizationGrantType;

    @Value("${spring.security.oauth2.client.registration.kakao.redirectUri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.clientName}")
    private String kakaoClientName;

    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoAuthorizationGrantType);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> tokenMap = new HashMap<>();

        try {
            tokenMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String accessToken = tokenMap.get("access_token").toString();

        HttpHeaders profileHeaders = new HttpHeaders();
        profileHeaders.add("Authorization", "Bearer " + accessToken);
        profileHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(profileHeaders);

        // HTTP요청
        ResponseEntity<String> profileResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoProfileRequest, String.class);

        ObjectMapper profileMapper = new ObjectMapper();
        profileMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Map<String, Object> userProfileMap = new HashMap<>();

        try {
            userProfileMap = profileMapper.readValue(profileResponse.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        StudentDto student = new StudentDto();
//        student.setStu_no(userProfileMap.get("id").toString());
        student.setStuNickname(((Map<String, Object>) userProfileMap.get("properties")).get("nickname").toString());
        student.setStuEmail(((Map<String, Object>) userProfileMap.get("kakao_account")).get("email").toString());
        student.setStuPwd(UUID.randomUUID().toString()); // 비밀번호는 임의로 생성
        System.out.println(student.getStuEmail());
        System.out.println(student.getStuPwd());

        student.setStuEmail(student.getStuEmail());
        student.setStuNickname(student.getStuNickname());
        student.setStuPwd(student.getStuPwd());
        student.setStuPhone("01000000000");
        student.setStuPwd(student.getStuPwd());
        student.setStuName("kakao");
        student.setStuSocial("social");

        if(studentService.findStudent(student.getStuEmail()).getStuEmail()==null){
            studentService.join(student);
            session.setAttribute("stuEmail", student.getStuEmail());
            model.addAttribute("student", student);
            model.addAttribute("stuNickname", student.getStuNickname());
            model.addAttribute("stuEmail", student.getStuEmail());
        }

        session.setAttribute("student", student);
        session.setAttribute("stuEmail", student.getStuEmail());
        session.setAttribute("stuNo", student.getStuNo());
        StudentDto dbStudent = studentService.findStudent(student.getStuEmail());
        session.setAttribute("studentDto", dbStudent); //장바구니용 테스트

        model.addAttribute("email", student.getStuEmail());
        model.addAttribute("nickname", student.getStuNickname());
        return "redirect:/student/myPage";
    }

}

/*
//네이버 회원가입 로그인

    //네이버 application.yaml 파일을 가져와 변수에 저장
    //네이버
    //네이버 clientid
    @Value("${spring.security.oauth2.client.registration.naver.clientId}")
    private String naverClientId;

    //네이버 clientSecret
    @Value("${spring.security.oauth2.client.registration.naver.clientSecret}")
    private String naverClientSecret;

    //네이버 clientAuthenticationMethod
    @Value("${spring.security.oauth2.client.registration.naver.clientAuthenticationMethod}")
    private String naverClientAuthenticationMethod;

    //네이버 authenticationGrantType
    @Value("${spring.security.oauth2.client.registration.naver.authorizationGrantType}")
    private String naverAuthorizationGrantType;

    //네이버 redirectUri
    @Value("${spring.security.oauth2.client.registration.naver.redirectUri}")
    private String naverRedirectUri;

    //네이버 clientName
    @Value("${spring.security.oauth2.client.registration.naver.clientName}")
    private String naverClientName;

@GetMapping("/naver/callback")
public String naverCallback(@RequestParam("code") String code, HttpSession session, Model model) { // Data를 리턴해주는 컨트롤러

    //post 방식으로 key=value  데이터를 요청(카카오쪽으로
    //Retrofit2
    //OkHttp
    //RestTemplate

    //HttpHeader 오브젝트 생성
    RestTemplate rt = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Naver-Client-Id",naverClientId);
    headers.add("X-Naver-Client-Secret", naverClientSecret);

    //HttpBody 오브젝트 생성
    MultiValueMap<String, String> params =new LinkedMultiValueMap<>();
    params.add("grant_type", naverAuthorizationGrantType);
    params.add("client_id", naverClientId);
    params.add("client_secret", naverClientSecret);
    params.add("redirect_uri", naverRedirectUri);
    params.add("code", code);
    params.add("state", "1234");


    //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
    HttpEntity<MultiValueMap<String,String>> naverTokenRequest=
            new HttpEntity<>(params,headers);

    // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답받음
    ResponseEntity<String> response = rt.exchange("https://nid.naver.com/oauth2.0/token?", HttpMethod.POST, naverTokenRequest, String.class);

    //Gson,
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> oauthToken = new HashMap();

    try {
        oauthToken = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
    } catch (JsonMappingException e) {
        e.printStackTrace();
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }

    //액세스 토큰을 파싱
    String accessToken = oauthToken.get("access_token").toString();

    //HttpHeader 오브젝트 생성 RestTemplate
    RestTemplate rt2 = new RestTemplate();
    HttpHeaders headers2 = new HttpHeaders();

    headers2.add("Authorization","Bearer "+accessToken);


    //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
    HttpEntity<MultiValueMap<String,String>> naverProfileRequest2= new HttpEntity<>(headers2);

    // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답받음 ResponseEntity<String>
    ResponseEntity<String>  response2 = rt2.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.POST, naverProfileRequest2, String.class);

    //Gson,
    ObjectMapper objectMapper2 = new ObjectMapper();
    //ObjectMapper를 사용하여 JSON 데이터를 DTO 객체로 변환할 때 FAIL_ON_UNKNOWN_PROPERTIES 기능을 비활성화하는 것은 유효한 접근 방법입니다.
    //이렇게 하면 DTO 클래스에 정의되지 않은 속성이 JSON 데이터에 포함되어 있더라도 예외가 발생하지 않고 객체 변환이 계속됩니다.

    objectMapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    Map<Object, Object>  naverProfileMap = new HashMap();


    //JSON 형식의 값을 맵에 저장
    //Response(id, nickname, profile_image,email, mobile)를 맵으로 파싱합니다.
    try {
        naverProfileMap = objectMapper2.readValue(response2.getBody(),new TypeReference<Map<Object, Object>>() {});
    } catch(JsonMappingException e) {
        e.printStackTrace();
    } catch(JsonProcessingException e) {
        e.printStackTrace(); }


    Map<String, Object>  naverResponseMap= (Map<String, Object>) naverProfileMap.get("response");

    StudentDto student = new StudentDto();

    // 아이디 구해옴
    String [] id = naverResponseMap.get("id").toString().split("(?<=\\G.{10})", -1);
    String id2 = id[0];

    //닉네임 구해옴
    String nickname = naverResponseMap.get("nickname").toString();

    //이름
    String name = naverResponseMap.get("name").toString();

    // 이메일 구해와 도메인과 메일 아이디로 구분
    String fullEmail=naverResponseMap.get("email").toString();
//        String[] emailParts=fullEmail.split("@");

    //휴대폰 번호 구해옴
    String phone = naverResponseMap.get("mobile").toString().replace("-", "");

    //비밀번호 난수화 임의 생성
    String pwd=UUID.randomUUID().toString();

    student.setStuNickname(nickname);
    student.setStuEmail(fullEmail);
    student.setStuPhone(phone);
    student.setStuPwd(pwd);


    return "myPage";
}*/
