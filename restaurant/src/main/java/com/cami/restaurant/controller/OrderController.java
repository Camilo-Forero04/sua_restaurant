package com.cami.restaurant.controller;

import com.cami.restaurant.dto.OrderDto;
import com.cami.restaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //PostMapping es para hacer una peticion POST algo que recibe, y crea
    //Valid activa las anotaciones @Not NUll @Min(1) que estan dentro del metodo .CustomerResponse
    //RequestBody convierte el texto del JSON y lo convierte en un objeto OrderDto.OrderRequest
    @PostMapping
    public ResponseEntity<OrderDto.CustomerResponse> createOrder(
            @Valid @RequestBody OrderDto.OrderRequest request){



        return null;
    }
}
