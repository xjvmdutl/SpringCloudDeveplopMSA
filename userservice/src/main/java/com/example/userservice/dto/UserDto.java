package com.example.userservice.dto;

import com.example.userservice.vo.ResponseOrder;
import java.util.List;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createdAt;

    private String encryptedPwd; //중간 단계 암호화

    private List<ResponseOrder> orders;

}
