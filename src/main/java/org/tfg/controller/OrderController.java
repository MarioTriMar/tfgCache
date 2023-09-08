package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.tfg.model.Order;
import org.tfg.service.OrderSevice;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderSevice orderService;

    @PostMapping("/saveOrder")
    public void saveOrder(@RequestBody Map<String, Object> info){

        String companyId=info.get("companyId").toString();
        String customerId=info.get("customerId").toString();
        double price=(Double)info.get("price");

        this.orderService.saveOrder(companyId, customerId, price);
    }

    @GetMapping("/getOrders")
    public List<Order> getAll(){  return this.orderService.getAll();}

    @GetMapping("/getOrdersOfCompany/{companyId}")
    public List<Order> getOrdersOfCompany(@PathVariable String companyId){
        return this.orderService.findByCompanyId(companyId);
    }

    @GetMapping("/getOrdersOfCustomer/{customerId}")
    public List<Order> getOrdersOfCustomer(@PathVariable String customerId){
        return this.orderService.findByCustomerId(customerId);
    }
}
