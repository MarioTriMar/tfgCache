package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.tfg.model.Order;
import org.tfg.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/saveOrder")
    public void saveOrder(@RequestBody Map<String, Object> info){
        String companyId=info.get("companyId").toString();
        String customerId=info.get("customerId").toString();
        List<String> products=(List<String>)info.get("products");

        this.orderService.saveOrder(companyId, customerId, products);
    }

    @GetMapping("/getOrders")
    public List<Order> getAll(){  return this.orderService.getAll();}

    @GetMapping("/getOrderById/{id}")
    public Order getOrderById(@PathVariable String id){
        return this.orderService.findOrderById(id);
    }

    @GetMapping("/getOrdersOfCompany/{companyId}")
    public List<Order> getOrdersOfCompany(@PathVariable String companyId){
        return this.orderService.findByCompanyId(companyId);
    }

    @GetMapping("/getOrdersOfCustomer/{customerId}")
    public List<Order> getOrdersOfCustomer(@PathVariable String customerId){
        return this.orderService.findByCustomerId(customerId);
    }
    /*
    @GetMapping("/duplicate")
    public void duplicate(){
        List<Order> pedidos=this.orderService.getAll();
        for(int i=0;i<pedidos.size();i++){
            this.orderService.saveOrder(pedidos.get(i).getCompany().getId(), pedidos.get(i).getCustomer().getId(), pedidos.get(i).getPrice()+43.00);
        }
    }
    */

}
