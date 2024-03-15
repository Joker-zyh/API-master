package com.heng.hengapiinterface.controller;


import com.heng.hengapiinterface.model.User;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/name")
public class NameController {


    @GetMapping("/get")
    public String getNameByGET(String name){
        return "getNameByGET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPOST(@RequestParam String name){
        return "getNameByPOST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPOST(@RequestBody User user){

        return "getUsernameByPOST 你的名字是" + user.getUserName();
    }

}
