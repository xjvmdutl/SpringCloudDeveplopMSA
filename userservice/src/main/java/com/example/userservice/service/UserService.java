package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);

    Iterable<UserDto> getUserByAll();

    void deleteUser(String userId);

    UserDto updateUser(UserDto userDto, String userId);

    UserDto getUserDetailByEmail(String userName);
}
