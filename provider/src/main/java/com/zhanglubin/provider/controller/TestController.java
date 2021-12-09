package com.zhanglubin.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("testprovider")
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("test1")
    public void test1() {
        System.out.println("11111-----------------------------1111------------------------------1111");
    }

    @GetMapping("test2")
    public void test2() {
        restTemplate.getForObject("http://nocos-provider/testprovider/test1",String.class);
    }

}
