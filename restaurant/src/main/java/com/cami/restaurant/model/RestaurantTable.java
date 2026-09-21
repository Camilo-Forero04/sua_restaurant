package com.cami.restaurant.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "restaurant_table")
@Getter
//el setter no debe ir a nivel clase si no solo en los atributos que lo necesitan,
// para evitar que por ejemplo se modifique el id
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantTable {

    //Se utiliza como PK o FK porque hace mucho mas rapido las busquedas
    // en la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Se agrega UUID para mayor seguirdad al momento de que una mesa ordene,
    //ya que alguien podria simplemente acceder a menu?table=1 y hacer un pedido falso
    // no se pone como PK porque ocupa mas espacio 128 bits
    // @GeneratedValue(strategy = GenerationType.UUID) solo se utiliza
    // con el id
    //añadir UUID (indetificador alfanumerico e.g. 12b31-2a3123-2g21)
    @Column(nullable = false, updatable = false)
    private UUID qrToken = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String name;

    public RestaurantTable(String name){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("El nombre de la mesa no puede estar vacio");
        }
        this.name = name;
        this.qrToken = UUID.randomUUID();
    }
}
