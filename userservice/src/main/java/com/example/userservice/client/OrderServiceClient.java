package com.example.userservice.client;

import com.example.userservice.vo.ResponseOrder;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service") //실행하고자하는 MSA이름
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    public List<ResponseOrder> getOrders(@PathVariable String userId);
}
