package com.example.authDemo.controller;

import com.example.authDemo.model.User;
import com.example.authDemo.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserController {

    @Autowired
    CustomUserDetailService userDetailService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> sendDictation() {
        User user = userDetailService.findCurrentUser();

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
