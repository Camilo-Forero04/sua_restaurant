package com.cami.restaurant.controller;

import com.cami.restaurant.dto.OrderDto;
import com.cami.restaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")//Endpoint base
@RequiredArgsConstructor
public class OrderController {
    //inyeccion de dependencias, no dependemos de todo escrito en la misma entidad si no de una externa
    private final OrderService orderService;

    //@PostMapping es para hacer una peticion POST algo que recibe, y crea
    //@Valid activa las validaciones (@NotNull, @NotEmpty) declaradas en la clase OrderDto.OrderRequest antes de que los datos toquen el servicio
    //@RequestBody convierte el texto del JSON y lo convierte en un objeto OrderDto.OrderRequest
    @PostMapping
    public ResponseEntity<OrderDto.OrderCreationResponse> createOrder(
            @Valid @RequestBody OrderDto.OrderRequest request){

        OrderDto.OrderCreationResponse response = orderService.createOrder(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()// toma la ULR actual, e.g http://localhost:8080/api/v1/orders
                .path("/{id}")//le dice que reserve el espacio para el identificador http://localhost:8080/api/v1/orders/{id} ademas de que estructura la ruta de manera mas facil de leer (en caso de que fueran mas identificaroes seria un caos)
                .buildAndExpand(response.orderId())//aplica Percent-Econding (URL encode) lo que garantiza que la direccion siempre sea valida y segura remplaza {id} por el id correspondiente ya sea alfanumerico o numerico e.g. http://localhost:8080/api/v1/orders/15"
                .toUri(); //toma la cadena previamente construida (string) y la convierte en un objeto formal de Java: URI (UNIFORM RESOURCE IDENTIFIER)

        return ResponseEntity.created(location).body(response); //Devolvemos el response Entity que es el objeto que representa la respuesta HTTP el codigo del estado en este caso 201 CREATED,
        // luego la cabecera (location)que indica la direccion (URI) donde quedo guardado el objeto
        // y finalmente se pasa el body que es el JSON o los datos del objeto unicamente necesarios (DTO)
    }

    // =========================================================================
    // CONSULTAR POR ID: GET /api/v1/orders/{id}
    // Retorna 200 OK con los datos necesarios unicamente para el cliente(usuario)
    // =========================================================================
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.CustomerResponse> readOrderClient(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.readOrderClient(id));
    }

    // =========================================================================
    // CANCELAR ORDEN: PATCH /api/v1/orders/{id}/cancel
    // Transición de estado puntual -> Retorna 204 No Content (sin body)
    // =========================================================================
    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);/**NOOOOO devolvemos un mensaje como "order cancelada" porque la intefraz y el idioma pertenecen unicamente al FRONTEND, es solo perder ancho de banda, ademas que HTTP de por si ya es un mensaje por si solo*/
    }

    // =========================================================================
    // PAGAR ORDEN: PATCH /api/v1/orders/{id}/pay
    // Transición de estado puntual -> Retorna 204 No Content (sin body)
    // =========================================================================
    @PatchMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void payOrder(@PathVariable Long id){
        orderService.payOrder(id);
    }
}
