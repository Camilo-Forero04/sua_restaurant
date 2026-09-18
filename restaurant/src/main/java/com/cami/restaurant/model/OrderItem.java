package com.cami.restaurant.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA necesita un constructor vacio pero no queremos que alguien cree un item vacio a mano
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    //agregar un nullable = false es redundante porque int primitivo no permite null
    private int quantity;

    @Setter
    @Column(nullable = false)
    private Long unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem(Order order, Dish dish, int quantity, Long unitPrice) {
        if(quantity<=0){
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
