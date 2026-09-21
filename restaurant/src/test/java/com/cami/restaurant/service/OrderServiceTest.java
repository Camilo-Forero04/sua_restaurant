package com.cami.restaurant.service;

import com.cami.restaurant.model.Order;
import com.cami.restaurant.model.RestaurantTable;
import com.cami.restaurant.model.enums.OrderStatus;
import com.cami.restaurant.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


// REGLA DE ORO DE LOS TEST: DEBEN SER 100% INDEPENDIENTES*/
@ExtendWith(MockitoExtension.class) // JUnit 5 inicializa el test y trae las herramientas de mockito para que funcione
@DisplayName("Pruebas unitarias para OrderService")
public class OrderServiceTest {

    @Mock
    // @Mock es para las dependencias que se quieran SIMULAR (el extra) es un CLON falso. Le dice a Mockito que cree un cascaron, con sus metodos, pero sordo y mudo, es decir no puede persistir en la base de datos
    private OrderRepository orderRepository;

    @InjectMocks // @InjectMocks es para la clase REAL, la cual se quiere poner a PRUEBA (el protagonista)
    private OrderService orderService;

    // TEST HELPER: La orden necesita una mesa para poder existir, ya que como elegimos DDD, inicializar una orden vacia es un error y a esta entidad solo le interesa probar la orden, entonces para no ensuciar el test utilizamos este metodo
    private Order createTestOrder() {
        RestaurantTable table = new RestaurantTable("Mesa 1");
        return new Order(table);
    }

    @Nested
    @DisplayName("Pruebas para payOrder()")
    class payOrderTests {

        @Test
        @DisplayName("Debe cambiar el estado a PAID cuando la orden existe y esta en PENDING")
        void shouldPayOrderSuccessfully_WhenOrderIsPending() { /**Patron estandar ROY OSHEROVE/BDD (Qué se espera)_(En que escenario)*/

            // 1. GIVEN: Preparamos el escenario simulado
            Long orderId = 1L;
            Order order = createTestOrder();

            // Entrenamos al mock: devuelve la orden simulada cuando busquen por orderId
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // 2. WHEN: Ejecutamos el servicio para luego auditarlo
            orderService.payOrder(orderId);

            // 3. THEN: Audita los datos, verificamos que el estado de la orden haya cambiado a PAID
            assertEquals(OrderStatus.PAID, order.getStatus());

            // Audita la interaccion, es decir se asegura de que el metodo findById se utilizo una vez dentro de la ejecucion del servicio
            verify(orderRepository, org.mockito.Mockito.times(1)).findById(orderId);
        }
    }


}
