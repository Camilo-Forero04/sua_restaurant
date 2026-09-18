package com.cami.restaurant.repository;

import com.cami.restaurant.model.Order;
import com.cami.restaurant.model.RestaurantTable;
import com.cami.restaurant.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    //Optional es un wrapper porque envuelve a otra clase; esto se hace porque la consulta
    // puede devolver un objeto vacio, si lo guardaramos en un ojeto de la clase directamente y esta vacio
    // nos va a dar un NullerPointerException
    Optional<Order> findByTable_IdAndStatus(Long table, OrderStatus status);

    boolean existsByTable_IdAndStatus(Long tableId, OrderStatus status);

    Optional<Order> findAllByStatus(OrderStatus status);


    //le ponemos @Param para que Spring sepa que el parametro que le estamos pasando, es el que tiene que usar en la Query
    @Query("""
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.table t
        LEFT JOIN FETCH i.dish d 
        WHERE o.id = :orderId
    """)
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

}
