package online.k12code.core.controller;

import online.k12code.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Carl
 * @description:
 * @since 1.0.0
 */
@SpringBootTest
public class UserController {

    @Autowired
    private  UserService userService;


    @Test
    public void addUser(){
        userService.addUser("carl","123");
    }
}
