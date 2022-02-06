package com.example.userservice;

import static feign.Logger.Level.FULL;

import com.example.userservice.error.FeignErrorDecoder;
import feign.Logger;
import feign.Logger.Level;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(){ //해당 어플리케이션이 기동되면서 해당 클래스 내부에 존재하는 빈을 등록시켜준다
		return new BCryptPasswordEncoder();
	}


	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate(){ //해당 어플리케이션이 기동되면서 해당 클래스 내부에 존재하는 빈을 등록시켜준다
		return new RestTemplate();
	}

	@Bean
	public Level feignLoggerLevel(){
		return FULL;
	}

	/*@Bean
	public FeignErrorDecoder getFeignErrorDecoder(){
		return new FeignErrorDecoder();
	}*/
}
