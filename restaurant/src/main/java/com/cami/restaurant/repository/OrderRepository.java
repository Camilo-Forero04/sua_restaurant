package com.cami.restaurant.repository;

import com.cami.restaurant.model.Order;
import com.cami.restaurant.model.RestaurantTable;
import com.cami.restaurant.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    //Optional porque puede devolver un objeto vacio si lo guardaramos como Order solamente
    // y luego intentaramos utilizarlo, entonces nos daria un NullPointerException
    Optional<Order> findByRestaurantTable_IdAndStatusIdAndStatus(RestaurantTable restaurantTable, OrderStatus status);

    Optional<Order> findAllByStatus(OrderStatus status);
}
