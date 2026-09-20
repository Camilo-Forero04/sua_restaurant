package com.cami.restaurant.dto;

import com.cami.restaurant.model.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

//el dto es el que interactua con el frontend, se diseña a partir del fe y lo valida, no de la bd, el que interactua con la bd y el dto es el servicio
public interface OrderDto {

    //se usa record porque es inmutable, sus valores no pueden cambiar al no poseer setter,
    // sintaxis mas corta que una clase, genera de forma invisible al compilar un constructor, getters, equals() y toString().
    //1. lo que entra (desde el celular del cliente)
    record OrderRequest(
        @NotNull Long restaurantTableId,
        @NotEmpty List<ItemRequest> items
    ){}

    record ItemRequest(
        @NotNull Long dishId,
        @NotNull @Min(1) int quantity
    ){}
    //Lo que se envia al cliente despues de crear la orden
    record OrderCreationResponse(
            Long orderId,
            OrderStatus status,
            Long total
    ) {}

    //2. Lo que sale para el cliente (confirmacion)
    record CustomerResponse(
            Long orderId,
            String tablename,
            OrderStatus status, //Pending
            Long total,
            List<CustomerItemResponse> items
    ){}

    record CustomerItemResponse(
            String dishName,
            int quantity,
            Long unitPrice,
            Long subtotal //unitPrice * quantity
    ){}

    //3. Lo que sale para la cocina (la pantalla del chef)
    record KitchenResponse(
            Long orderId,
            String tableName,
            OrderStatus status,
            List<KitchenItemResponse> items
    ) {}

    record KitchenItemResponse(
            String dishName,
            int quantity
    ){}
}
