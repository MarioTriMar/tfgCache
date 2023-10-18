package org.tfg.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger= LoggerFactory.getLogger(OrderController.class);

    /*
     Este método es llamado mediante petición POST. Toma como entrada un MAP
    con la siguiente estructura: {
                                    "companyId":string,
                                    "customerId":string,
                                    "products":list<String>
                                 }
    Esto se lo pasa al OrderService para comprobar si se puede validar y guardarlo.
     */
    @PostMapping("/saveOrder")
    public void saveOrder(@RequestBody Map<String, Object> info){
        logger.info("SAVING ORDER: "+info.toString());

        String companyId=info.get("companyId").toString();
        String customerId=info.get("customerId").toString();
        List<String> products=(List<String>)info.get("products");

        this.orderService.saveOrder(companyId, customerId, products);
    }

    /*
    Este método es llamado mediante petición GET. Su función es devolver
    una lista con todos los pedidos.
     */
    @GetMapping("/getOrders")
    public List<Order> getAll(){
        logger.info("GETTING ALL ORDERS");
        return this.orderService.getAll();
    }

    /*
    Este método es llamado mediante petición GET y se le pasa como PathVariable
    el id de un pedido. Su función es llamar al OrderService y devolver dicho pedido.
     */
    @GetMapping("/getOrderById/{id}")
    public Order getOrderById(@PathVariable String id){
        logger.info("GETTING ORDER BY ID: "+id);
        return this.orderService.findOrderById(id);
    }

    /*
    Este método es llamado mediante petición GET y se le pasa como PathVariable
    el id de una compañía. Su función es llamar al OrderService y devolver los pedidos
    de dicha compañía.
     */
    @GetMapping("/getOrdersOfCompany/{companyId}")
    public List<Order> getOrdersOfCompany(@PathVariable String companyId){
        logger.info("GETTING ALL COMPANY ORDERS BY ID: "+companyId);
        return this.orderService.findByCompanyId(companyId);
    }

    /*
    Este método es llamado mediante petición GET y se le pasa como PathVariable
    el id de un cliente. Su función es llamar al OrderService y devolver los pedidos
    de dicho cliente.
     */
    @GetMapping("/getOrdersOfCustomer/{customerId}")
    public List<Order> getOrdersOfCustomer(@PathVariable String customerId){
        logger.info("GETTING ALL CUSTOMER ORDERS BY ID: "+customerId);
        return this.orderService.findByCustomerId(customerId);
    }

    /*
    Este método es llamado mediante petición GET y se le pasa como PathVariable
    el id de un cliente. Su función es llamar al OrderService y devolver la cantidad de
    dinero que ha gastado el cliente.
    */
    @GetMapping("/totalMoney/{customerId}")
    public double getTotalMoney(@PathVariable String customerId){
        logger.info("GETTING TOTAL MONEY EXPEND BY CUSTOMER ID: "+customerId);
        return this.orderService.getTotalMoney(customerId);
    }
}
