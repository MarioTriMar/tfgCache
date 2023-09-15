package org.tfg.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.tfg.model.Product;
import org.tfg.service.ProductService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("products")
@CrossOrigin("*")
public class ProductController {
    @Autowired
    private ProductService productService;

    private Logger logger= LoggerFactory.getLogger(ProductController.class);


    /*
    Este método es llamado mediante petición POST. Toma como entrada un MAP
    con la siguiente estructura: {
                                    "name":string,
                                    "details":string,
                                    "price":double,
                                    "companyId":string
                                 }
    Esto se lo pasa al ProductService para registrarlo y guardarlo.
     */
    @PostMapping("/saveProduct")
    public void saveProduct(@RequestBody Map<String, Object> info){
        logger.info("SAVING PRODUCT: "+info.toString());

        String name=info.get("name").toString();
        String details=info.get("details").toString();
        double price=(double)info.get("price");
        String companyId=info.get("companyId").toString();

        this.productService.save(name, details, price, companyId);
    }

    /*
    Este método es llamado mediante petición PUT. Toma como entrada un MAP
    con la siguiente estructura: {
                                    "id": string,
                                    "name":string,
                                    "details":string,
                                    "price":double,
                                    "stock":boolean,
                                    "companyId":string
                                 }
    Esto se lo pasa al ProductService para actualizar dicho producto con los nuevos valores.
     */
    @PutMapping("/updateProduct")
    public void updateProduct(@RequestBody Map<String,Object> product){
        logger.info("UPDATING PRODUCT: "+product.toString());

        String id=product.get("id").toString();
        String name=product.get("name").toString();
        String details=product.get("details").toString();
        double price=(double)product.get("price");
        boolean stock=(boolean) product.get("stock");
        String companyId=product.get("company").toString();
        this.productService.updateProduct(id,name,details,price,stock,companyId);
    }

    /*
    Este método es llamado mediante petición GET. Su función es devolver
    una lista que contenga todos los productos.
     */
    @GetMapping("/getProducts")
    public List<Product> getAllProducts(){
        logger.info("GETTING ALL PRODUCTS");
        return this.productService.getAll();
    }

    /*
    Este método es llamado mediante una petición PUT. Se le pasa como PathVariable
    el id del producto y llama al ProductService para cambiar su stock.
     */
    @PutMapping("/changeStock/{productId}")
    public void changeStock(@PathVariable String productId){
        logger.info("CHANGING STOCK OF PRODUCT: "+productId);
        this.productService.changeStock(productId);
    }
}
