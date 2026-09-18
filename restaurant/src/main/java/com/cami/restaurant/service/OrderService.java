package com.cami.restaurant.service;

import com.cami.restaurant.dto.OrderDto;
import com.cami.restaurant.model.Dish;
import com.cami.restaurant.model.Order;
import com.cami.restaurant.model.OrderItem;
import com.cami.restaurant.model.RestaurantTable;
import com.cami.restaurant.model.enums.OrderStatus;
import com.cami.restaurant.repository.DishRepository;
import com.cami.restaurant.repository.OrderItemRepository;
import com.cami.restaurant.repository.OrderRepository;
import com.cami.restaurant.repository.RestaurantTableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional; // ✅ Usar este
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    /*Hacemos inyeccion de dependencias que basicamente es pasarle los superpoderes de los repositorios al servicio
    los repositorios quedan declarados como atributos del servicio*/
    private final OrderRepository orderRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final DishRepository dishRepository;
    //private final OrderItemRepository orderItemRepository; cambiamos la relacion a Cascade entonces ya no se necesita

    //====================================================================
    //                           FORM CLIENTE
    //====================================================================
    @Transactional //Transactional sirve para que si hay un error en medio de la persistencia
    //se haga un rollback y no queden cosas guardadas de manera incompleta
    public OrderDto.OrderCreationResponse createOrder(OrderDto.OrderRequest request){

        RestaurantTable table = restaurantTableRepository.findById(request.restaurantTableId())
                .orElseThrow(()-> new EntityNotFoundException("La mesa no existe"));


        if(orderRepository.existsByTable_IdAndStatus(table.getId(), OrderStatus.PENDING)){
            throw new IllegalStateException("La mesa ya tiene una orden activa.");
        }

        //inicializamos una nueva orden y le asignamos la mesa
        Order newOrder = new Order(table);

        //long totalCalculado = 0L;

        //ESTO ES UN PROBLEMA (ROUND-TRIP) SUMA ENTRE 2 Y 20 MILISEGUNDOS DE LATENCIA DE RED, YA QUE AUNQUE FUNCIONA, VA A ABRIR LA RED HACIA LA BASE DE DATOS
        //POR CADA PLATO DE MANERA INDIVIDUAL. SI MULTIPLICAMOS ESTO POR EL NUMERO DE CLIENTES PIDIENDO AL MISMO TIEMPO, EL SERVIDOR COLAPSARIA

        //Del request se extraen los platos de la orden uno a uno con un foreach
        /*for(OrderDto.ItemRequest itemReq : request.items()){

            Dish dish = dishRepository.findById(itemReq.dishId())
                    .orElseThrow(()-> new EntityNotFoundException("El plato no existe"));

            OrderItem orderItem = new OrderItem(savedOrder,dish, itemReq.quantity(),dish.getPrice());

            orderItemRepository.save(orderItem);

            totalCalculado += (dish.getPrice() * itemReq.quantity());
        }
            savedOrder.setTotal(totalCalculado); */

        //PARA EVITAR LO DE ARRIBA, PRIMERO EXTRAEMOS TODOS LOS PLATOS (OMITIENDO LOS REPETIDOS) Y LUEGO EJECUTAMOS UNA SOLA CONSULTA EN UN MAP PARA TRAER EL OBJETO COMPLETO
        Set<Long> dishIds = request.items().stream()
                .map(OrderDto.ItemRequest::dishId)//Se utiliza method reference :: que equivale a una funcion flecha -> que basicamente se uitliza para traer un metodo especifico de x objeto
                .collect(Collectors.toSet());

        Map<Long, Dish> dishMap = dishRepository.findAllById(dishIds).stream()
                .collect(Collectors.toMap(
                        Dish::getId,
                        Function.identity() //Esto le dice a java guarda el mismo objeto (Dish) que estas recorriendo es como decir dish -> dish
                ));

        if(dishMap.size() != dishIds.size()){
            throw new EntityNotFoundException("Uno o más platos solicitados no existen en el menú");
        }

        //Llenamos el objeto dish con el valor de nuestro map por cuantos items hayan en la request; una vez llenamos el objeto dish,
        for(OrderDto.ItemRequest itemReq : request.items()){
            Dish dish = dishMap.get(itemReq.dishId());
            newOrder.addItem(dish, itemReq.quantity());
        }

        //Se crea un saveOrder para persistir la data y que ahora el objeto este completo ya que se le asigna el id
        Order savedOrder = orderRepository.save(newOrder);


        return new OrderDto.OrderCreationResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotal()
        );
    }

    //====================================================================
    //                           VISTA COCINA
    //====================================================================
    @Transactional(readOnly = true) //Optimiza rendimiento: Hibernate no vigila cambios o sea NO DIRTY CHECKING
    public OrderDto.KitchenResponse readOrderKitchen(Long orderId){

        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(()->new EntityNotFoundException("La orden no puede ser tomada porque no se encontro"));

        /*EL STREAM() FUNCIONA COMO UNA CINTA TRANSPORTADORA (RECORRE LA LISTA OBJETO POR OBJETO COMO UN FOR) MIENTRAS QUE
        EL MAP SIRVE PARA RECOGER UNICAMENTE LOS DATOS NECESARIOS Y CONVERTILOS EN EL OBJETO QUE NECESITAMOS
        Y EL TO LIST NUEVAMENTE PARA EMPACAR
        BASICAMENTE AGARRAMOS LA LISTA Y SOLO TOMAMOS LOS DATOS QUE NECESITAMOS
        UTILIZA STREAM PARA TRANSFORMAR, FILTRAR O ACUMULAR DATOS
        UTILIZAR FOR O FOR-EACH PARA LLAMAR LA BASE DE DATOS, ESCRIBIR LOGS, INVOCAR APIS EXTERNAS O MODIFICAR EL ESTADO DE OTRA VARIABLE*/
        List<OrderDto.KitchenItemResponse> kitchenItemResponseList = order.getItems().stream()
                .map(item-> new OrderDto.KitchenItemResponse(
                        item.getDish().getName(),
                        item.getQuantity()
                        ))
                .toList();

        return new OrderDto.KitchenResponse(
                order.getId(),
                order.getTable().getName(),
                order.getStatus(),
                kitchenItemResponseList
        );
    }

    //====================================================================
    //                           VISTA CLIENTE
    //====================================================================
    @Transactional
    public OrderDto.CustomerResponse readOrderClient(Long orderId){

        //con una sola consulta se trae orden, mesa, items y platos
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(()-> new EntityNotFoundException("La orden no puede mostrarse porque no se encontró"));


        List<OrderDto.CustomerItemResponse> customerItemList =order.getItems().stream()
                .map(item -> new OrderDto.CustomerItemResponse(
                        item.getDish().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice() * item.getQuantity()
                ))
                .toList();

        return new OrderDto.CustomerResponse(
                order.getId(),
                order.getTable().getName(),
                order.getStatus(),
                order.getTotal(),
                customerItemList
        );
    }


    //====================================================================
    //               CANCELAR ORDEN: CLIENTE, MESERO
    //====================================================================
    @Transactional
    public void cancelOrder(Long orderId){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new EntityNotFoundException("la orden no puede ser cancelada porque no se encontró"));

        order.cancel();

        //DIRTY CHECKING
        //NO SE NECESITA .save() es redundante porque el transactional hace que
        //hibernate este SUPERVISANDO constantemente el tiempo y detecte el cambio que ocurre en la memoria ram y quede auto-guardado o updated
    }


}
