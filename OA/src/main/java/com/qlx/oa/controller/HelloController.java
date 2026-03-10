package com.qlx.oa.controller;

import com.qlx.oa.entity.User;
import com.qlx.oa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {
    @GetMapping
    public String hello(){
        return "Hello World";
    }
    @Autowired
    private UserService userService;
    @GetMapping("/list")
    public List<User> list(){
        return userService.list();
    }
}
