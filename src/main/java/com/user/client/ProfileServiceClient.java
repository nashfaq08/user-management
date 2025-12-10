//package com.user.client;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//import java.util.UUID;
//
//@Slf4j
//@Service
//public class ProfileServiceClient {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${service.profile-url}")
//    private String profileServiceUrl;
//
//    @Value("${service.internal-secret}")
//    private String internalServiceSecret;
//
//    public ProfileServiceClient(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public String createCustomerProfile(CustomerDTO customerDTO) {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-Internal-Secret", internalServiceSecret);
//
//        HttpEntity<CustomerDTO> entity = new HttpEntity<>(customerDTO, headers);
//
//        String url = profileServiceUrl + "/internal/customer/create";
//        log.info("Calling customer profile creation URL from profile service: {}", url);
//
//        try {
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url, HttpMethod.POST, entity, String.class
//            );
//            log.info("Received response from profile service for customer profile creation: {}", response.getBody());
//            return response.getBody();
//        } catch (HttpClientErrorException e) {
//            throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
//        }
//    }
//
//    public CustomerDTO fetchCustomerIfExists(UUID customerId) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-Internal-Secret", internalServiceSecret);
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//
//        String url = profileServiceUrl + "/internal/customer/" + customerId + "/details";
//
//        try {
//            ResponseEntity<CustomerDTO> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    requestEntity,
//                    CustomerDTO.class
//            );
//
//            log.info("Received response from profile service: {}", response.getBody());
//
//            return response.getBody();
//        } catch (HttpClientErrorException.NotFound e) {
//            return null;
//        }
//    }
//}
