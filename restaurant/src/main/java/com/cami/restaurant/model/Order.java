package com.cami.restaurant.model;

import com.cami.restaurant.model.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "orders") //se agregar orders para que no haya problema con las consultas
@Getter
//el setter no debe ir a nivel clase si no solo en los atributos que lo necesitan,
// para evitar que por ejemplo se modifique el id
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long total;

    private OffsetDateTime createdAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    //se cambio la clase Table por RestaurantTable porque la anotacion @Table generaba conflicto
    // se pudo haber llamado todo el paquete para definir la clase e.g. com.cami.restaurant.model.Table pero era menos prolijo
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="waiter_id")
    private Waiter waiter;
}
