package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Everything in here will require authentication.
 *
 * Created by pallav.kothari on 7/15/17.
 */
@RestController
public class RestEndpoints {

    @RequestMapping("/users")
    public @ResponseBody String getUsers() {
        return "{\"users\":[{\"firstname\":\"Richard\", \"lastname\":\"Feynman\"}," +
                "{\"firstname\":\"Marie\",\"lastname\":\"Curie\"}]}";
    }
}
