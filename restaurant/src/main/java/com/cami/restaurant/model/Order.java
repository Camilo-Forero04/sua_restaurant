package com.cami.restaurant.model;

import com.cami.restaurant.model.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;


//Rich Domain Model: la entidad debe proteger su propia invariante, encargarse de su propia logica
//Anemic Domain Model: la entidad esta vacia, solo cuenta con getters, setters y constructores
@Entity
@Table(name = "orders") //se agregar orders para que no haya problema con las consultas
@Getter //A pesar de tener un getter en toda la calse, loombok le da prioridad a los que ya tengo escritos
//el setter no debe ir a nivel clase si no solo en los atributos que lo necesitan,
// para evitar que por ejemplo se modifique el id
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long total;

    @Column(nullable = false, length = 20)
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // optional = false permite a Hibernate optimizar consulas (INNER JOIN)
    @JoinColumn(name = "table_id")
    //se cambio la clase Table por RestaurantTable porque la anotacion @Table generaba conflicto
    // se pudo haber llamado todo el paquete para definir la clase e.g. com.cami.restaurant.model.Table pero era menos prolijo
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="waiter_id")
    private Waiter waiter;

    @OneToMany(mappedBy =  "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order(RestaurantTable table){
        this.table = table;
        this.status = OrderStatus.PENDING;
        this.createdAt = OffsetDateTime.now();
        this.total = 0L;
    }

    public void addItem(Dish dish, int quantity) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("No se pueden agregar platos a una orden en estado " + this.status);
        }

        OrderItem item = new OrderItem(this, dish, quantity, dish.getPrice());
        this.items.add(item);
        this.total += (dish.getPrice() * quantity);
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID || this.status == OrderStatus.DELIVERED) {
            //el IllegalStateException se usa cuando el dato existe, pero esta en el parametro incorrecto
            throw new IllegalStateException("No se puede cancelar una orden que ya fue entregada o pagada.");
        }
        this.status = OrderStatus.CANCELLED;
    }
    //por lo general las list y set se protegen con metodos manuales
    //este getter nos blinda, sirve solo para leer y no para modificar por medio de un .add() o un .clear()
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(this.items);
    }
}
