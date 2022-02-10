package com.example.userservice.service;

import static org.springframework.http.HttpMethod.GET;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import feign.FeignException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final Environment env;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString()); //랜덤 유저값 생성
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //강력한 변환규정
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        userRepository.save(userEntity);
        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);
        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found"); //없을떄 오류 로그
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        //List<ResponseOrder> orders = new ArrayList<>(); //주문이 없다고 생각하고 빈 값을 넣었다
        //Get방식으로 사용자ID를 통해 주문 정보를 읽어온다
        //하드코딩을 하는것이 아닌 yml과 같은 속성파일에 저장하는것이 변경되더라도 수정하기가 좋다
        //String orderUrl = "http://127.0.0.1:8000/order-service/%s/orders";
        /* Using as rest template */
        /*
        String orderUrl = String.format(env.getProperty("order_service.url"), userId);

        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate
            .exchange(orderUrl, GET, null, new ParameterizedTypeReference<List<ResponseOrder>>() {

            });
        List<ResponseOrder> orders = orderListResponse.getBody();
         */
        /* Using as feign client */
        /* Feign exception handling */
        /*
        List<ResponseOrder> orders = null;
        try{
            orders = orderServiceClient.getOrders(userId);
        }catch (FeignException e){
            log.error(e.getMessage());
        }
         */

        //List<ResponseOrder> orders = orderServiceClient.getOrders(userId);
        log.info("Before call orders microService");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orders = circuitbreaker
            .run(() -> orderServiceClient.getOrders(userId), //성공시
                throwable -> new ArrayList<>());//실패시
        log.info("After call orders microService");
        userDto.setOrders(orders);
        return userDto;
    }

    @Override
    public Iterable<UserDto> getUserByAll() {
        return userRepository.findAll().stream().map(o -> new ModelMapper().map(o, UserDto.class))
            .collect(
                Collectors.toList());
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteByUserId(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //강력한 변환규정
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        userEntity.setEmail(userDto.getEmail());
        userEntity.setName(userDto.getName());
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);
        return returnUserDto;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            //해당 사용자가 없다
            throw new UsernameNotFoundException(email);
        }
        return new User(userEntity.getEmail(),
            userEntity.getEncryptedPwd(),
            true,
            true,
            true,
            true,
            new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }

}
