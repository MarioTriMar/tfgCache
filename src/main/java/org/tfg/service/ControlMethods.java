package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Customer;
import org.tfg.model.Product;
import org.tfg.repository.ProductDAO;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.CustomerDAO;

import java.util.List;
import java.util.Optional;

@Service
public class ControlMethods {
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private ProductDAO productDAO;

    /*
    Este método recibe por parámetros el id de una compañía y un boolean a modo de flag.
    Primero comprueba si dicha compañía existe, si no existe lanza un 404.
    La segunda comprobación se usa a la hora de hacer un pedido, donde el flag checkState será true.
    Esta comprobación sirve para saber si la compañía sigue dada de alta. Si no está dada de alta
    se lanza un 406.
    El flag checkState en el resto de casos será falso, ya que, en operaciones como la de dar la compañía
    por un id de pedido es indiferente si la empresa está o no activada.
     */
    public Company existCompany(String companyId, boolean checkState){
        Optional<Company> optCompany=this.companyDAO.findById(companyId);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        if(!optCompany.get().isEnabled() && checkState){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Company is disabled");
        }
        return optCompany.get();
    }

    /*
    Este método es idéntico al anterior pero para los clientes.
     */
    public Customer existCustomer(String customerId, boolean checkState){
        Optional<Customer> optCustomer=this.customerDAO.findById(customerId);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        if(!optCustomer.get().isEnabled() && checkState){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Customer is disabled");
        }
        return optCustomer.get();
    }

    /*
    Este método es idéntico al anterior pero para los productos. Aquí se comprueba el
    stock en vez de si está activado.
     */
    public Product existProduct(String productId, boolean checkStock){
        Optional<Product> optionalProduct=this.productDAO.findById(productId);
        if(optionalProduct.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product doesn't exist");
        }
        if(!optionalProduct.get().isStock() && checkStock){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Product out of stock");
        }
        return optionalProduct.get();
    }


    /*
    Este método recibe por parámetros el id de la compañía y la lista de productos
    del pedido. Comprueba si esos pedidos pertenecen a la compañía a la que se quiere hacer
    el pedido.
     */
    public boolean belongsProductToCompany(String companyId, List<Product> productList) {
        List<Product> companyProduct=this.productDAO.findByCompanyId(companyId);
        return (companyProduct.containsAll(productList));
    }
}
