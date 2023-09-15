package org.tfg.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.ProductDAO;

import java.util.List;
import java.util.Optional;


@Service
public class CompanyService {
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private ControlMethods controlMethods;



    /*
    Este método recibe por parametros el nombre, el cif y el email de contacto
    de la compañía.
    Crea el objeto Company y le asigna el nombre, el cif, el email y que está activa.
    Guarda la compañía en la BBDD.
     */
    public void save(String name, String cif, String contactEmail){
        Company company=new Company();
        company.setName(name);
        company.setCif(cif);
        company.setContactEmail(contactEmail);
        company.setEnabled(true);
        this.companyDAO.save(company);
    }

    /*
    Este método recibe por parámetro el id de la compañía.
    Busca dicha compañía en la BBDD, en caso de no estar lanza un 404.
    Si la encuentra la devuelve.
     */
    public Company findCompanyById(String id) {
        Optional<Company> optCompany=this.companyDAO.findById(id);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        return optCompany.get();
    }

    /*
    Este método devuelve la lista de compañías.
     */
    public List<Company> getAll(){
        return this.companyDAO.findAll();
    }

    /*
    Este método recibe por parametro una compañía.
    Su función es actualizarla.
     */
    public void update(Company company) {
        this.companyDAO.save(company);
    }


    /*
    Este método recibe por parámetros el id de una compañía.
    Su función es listar todos los productos de dicha compañía.
    Primero comprobará la existencia de dicha compañía (llamando al método
    existCompany de la clase ControlMethods).
     */
    public List<Product> findCompanyProducts(String id) {
        Company company=this.controlMethods.existCompany(id, false);
        return this.productDAO.findByCompanyId(company.getId());
    }

    /*
    Este método recibe por parámetros el id de una compañía.
    Su función es cambiar el estado en el que esta se encuentra.
    Primero comprobará la existencia de esta.
     */
    public void changeState(String companyId) {
        Company company=this.controlMethods.existCompany(companyId, false);
        company.setEnabled(!company.isEnabled());
        this.companyDAO.save(company);
    }
}
