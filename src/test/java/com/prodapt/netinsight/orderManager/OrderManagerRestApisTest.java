package com.prodapt.netinsight.orderManager;

import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.customerManager.CustomerRepository;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@SpringBootTest
@ActiveProfiles("UNIT")
public class OrderManagerRestApisTest {

    @Autowired
    OrderManagerRestApis orderManagerRestApis;

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    CustomerRepository customerRepository;

    @Test
    void createOrderSuccess() {
        //Test case 1 : Creating Order
        OrderEntity orderEntity = new OrderEntity();
        Mockito.when(orderRepository.createOrder(orderEntity.getStatus(), orderEntity.getCategory(), orderEntity.getDescription())).thenReturn(orderEntity);
        JSONObject response = orderManagerRestApis.createOrder(null, orderEntity);
        assertEquals("Success", response.get("status"));
        assertEquals(orderEntity, response.get("order"));
    }

    @Test
    void createOrderWithCustomerSuccess() {
        //Test case 1 : Creating Order with Customer
        Long customerId = 3456l;
        Customer customer = new Customer();
        customer.setId(customerId);
        OrderEntity orderEntity = new OrderEntity();
        Mockito.when(customerRepository.findCustomerById(customer.getId())).thenReturn(customer);
        Mockito.when(orderRepository.createOrderWithCustomer(customerId, orderEntity.getStatus(), orderEntity.getCategory(), orderEntity.getDescription())).thenReturn(orderEntity);
        JSONObject response = orderManagerRestApis.createOrder(customerId, orderEntity);
        assertEquals("Success", response.get("status"));
        assertEquals(orderEntity, response.get("order"));
    }

    @Test
    void createOrderFailure() {
        //Test case 1 : Customer not Found
        Long customerId = 3456l;
        Customer customer = new Customer();
        customer.setId(customerId);
        OrderEntity orderEntity = new OrderEntity();
        Mockito.when(customerRepository.findCustomerById(customer.getId())).thenReturn(null);
        Exception customerException = Assertions.assertThrows(ServiceException.class, () -> {
            orderManagerRestApis.createOrder(customerId, orderEntity);
        });
        assertTrue(customerException.getMessage().contains("Given CustomerId " + customerId + " is not Found"));
    }

    @Test
    void updateOrderByIdSuccess() {
        //Test case 2 : Updating Order
        Long customerId = 3456l;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(customerId);
        Mockito.when(orderRepository.findOrderById(orderEntity.getId())).thenReturn(orderEntity);
        Mockito.when(orderRepository.updateOrderById(customerId, orderEntity.getStatus())).thenReturn(orderEntity);
        JSONObject response = orderManagerRestApis.updateOrderById(customerId, orderEntity);
        assertEquals("Success", response.get("status"));
        assertEquals(orderEntity, response.get("order"));
    }

    @Test
    void updateOrderByIdFailure() {
        //Test case 2 : Order not found
        Long orderId = 11l;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderEntity.getId())).thenReturn(null);
        Exception orderException = Assertions.assertThrows(ServiceException.class, () -> {
            orderManagerRestApis.updateOrderById(orderId, orderEntity);
        });
        assertTrue(orderException.getMessage().contains("Given Order Id" + orderId + "is not found"));
    }

    @Test
    void getOrderByIdSuccess() {
        Long orderId = 11l;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);

        Mockito.when(orderRepository.findOrderById(orderEntity.getId())).thenReturn(orderEntity);
        OrderEntity orderDetails = orderManagerRestApis.getOrderById(orderId);
        assertEquals(orderEntity, orderDetails);
    }

    @Test
    void getOrderByIdFailure() {
        Long orderId = 11l;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);

        Mockito.when(orderRepository.findOrderById(orderEntity.getId())).thenReturn(null);
        Exception orderException = Assertions.assertThrows(ServiceException.class, () -> {
            orderManagerRestApis.getOrderById(orderId);
        });
        assertTrue(orderException.getMessage().contains("The order " + orderId + " is not found"));
    }

    @Test
    void getAllOrdersSuccess() {
        ArrayList<OrderEntity> order = new ArrayList<>();
        OrderEntity orderEntity = new OrderEntity();
        order.add(orderEntity);
        Mockito.when(orderRepository.getAllOrders()).thenReturn(order);
        ArrayList<OrderEntity> orderDetails = orderManagerRestApis.getAllOrders();
        assertEquals(order, orderDetails);
    }

    @Test
    void getAllOrdersFailure() {
        Mockito.when(orderRepository.getAllOrders()).thenThrow(new RuntimeException("Failed to fetch orders"));
        Exception orderException = Assertions.assertThrows(Exception.class, () -> {
            orderManagerRestApis.getAllOrders();
        });
        assertTrue(orderException.getMessage().contains("Failed to fetch orders"));
    }

    @Test
    void deleteOrderSuccess() {
        //Test case 3 : Deleting Order
        Long orderId = 11l;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderEntity.getId())).thenReturn(orderEntity);
        JSONObject response = orderManagerRestApis.deleteOrderById(orderId);
        assertEquals(orderEntity, response.get("order"));
        assertEquals("Deleted", response.get("status"));
    }

    @Test
    void deleteOrderFailure() {
        //Test case 3 : Order not found
        Long orderId = 11l;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderEntity.getId())).thenReturn(null);
        Exception orderException = Assertions.assertThrows(ServiceException.class, () -> {
            orderManagerRestApis.deleteOrderById(orderId);
        });
        assertTrue(orderException.getMessage().contains("The order " + orderId + " is not found"));
    }
}
