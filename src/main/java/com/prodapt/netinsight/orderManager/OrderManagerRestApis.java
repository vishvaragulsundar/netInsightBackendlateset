package com.prodapt.netinsight.orderManager;

import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.customerManager.CustomerRepository;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * This class holds all the REST API endpoints for handling Order
 */
@RestController
public class OrderManagerRestApis {
    Logger logger = LoggerFactory.getLogger(OrderManagerRestApis.class);

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @PostMapping("/createOrder")
    public JSONObject createOrder(@RequestParam(name = "customerId", required = false) Long customerId, @RequestBody OrderEntity order) {
        JSONObject response = new JSONObject();
        OrderEntity orderDetails = null;
        try {
            if (customerId == null) {
                orderDetails = orderRepository.createOrder(order.getStatus(), order.getCategory(), order.getDescription());
            } else {
                Customer existingCustomer = customerRepository.findCustomerById(customerId);
                if (existingCustomer == null) {
                    appExceptionHandler.raiseException("Given CustomerId " + customerId + " is not Found");
                } else {
                    orderDetails = orderRepository.createOrderWithCustomer(customerId, order.getStatus(), order.getCategory(), order.getDescription());
                }
            }

            response.put("status", "Success");
            response.put("order", orderDetails);

        } catch (ServiceException e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @PutMapping("/updateOrderById")
    public JSONObject updateOrderById(@RequestParam(name = "orderId") Long orderId, @RequestBody OrderEntity order) {
        logger.info("Inside updateOrderById, order name value received: {}", orderId);
        JSONObject response = new JSONObject();
        try {

            OrderEntity findOrder = orderRepository.findOrderById(orderId);
            if (findOrder == null) {
                appExceptionHandler.raiseException("Given Order Id" + orderId + "is not found");
            }
            OrderEntity orderDetails = orderRepository.updateOrderById(orderId, order.getStatus());
            response.put("status", "Success");
            response.put("order", orderDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/getOrderById")
    public OrderEntity getOrderById(@RequestParam(name = "orderId") Long orderId) {
        logger.info("Inside getOrderById, order name value received: {}", orderId);
        OrderEntity order = null;
        try {
            order = orderRepository.findOrderById(orderId);
            if (order == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return order;
    }

    @GetMapping("/getAllOrders")
    public ArrayList<OrderEntity> getAllOrders() {
        logger.info("Inside getAllOrders");
        ArrayList<OrderEntity> orderList = null;
        try {
            orderList = orderRepository.getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return orderList;
    }

    @DeleteMapping("/deleteOrderById")
    public JSONObject deleteOrderById(@RequestParam(name = "orderId") Long orderId) {
        logger.info("Inside getOrderById, order name value received: {}", orderId);
        JSONObject response = new JSONObject();
        OrderEntity order = null;
        try {
            order = orderRepository.findOrderById(orderId);
            if (order == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            orderRepository.delete(order);
            response.put("order", order);
            response.put("status", "Deleted");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/findOrderById")
    public ArrayList<Long> findOrderByIdContaining(@RequestParam("orderId") Long orderId) {
        ArrayList<Long> findOrderById = new ArrayList<>();
        try {
            String order = String.valueOf(orderId);
            findOrderById = orderRepository.findOrderByIdContaining(order);
            logger.info("inside findOrderById :{}", findOrderById);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return findOrderById;
    }

}
