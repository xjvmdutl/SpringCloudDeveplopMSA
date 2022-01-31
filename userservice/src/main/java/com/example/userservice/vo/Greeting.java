package com.example.userservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
//@AllArgsConstructor
//@NoArgsConstructor
public class Greeting {

    @Value("${greeting.message}") //Application.yml 값을 읽어 올 것이다
    private String message;

}
