package org.tfg.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.service.CompanyService;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("companies")
@CrossOrigin("*")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    private Logger logger= LoggerFactory.getLogger(CompanyController.class);

    /*
    Este método es llamado mediante petición POST. Toma como entrada un MAP
    con la siguiente estructura: {
                                    "name":string,
                                    "cif":string,
                                    "contactEmail":string
                                 }
    Primero comprueba si el email de contacto coincide con un formato válido de email.
    Esto se lo pasa al CompanyService para registrarla y guardarla.
     */
    @PostMapping("/register")
    public void saveCompany(@RequestBody Map<String, Object> info){
        logger.info("SAVING COMPANY: "+info.toString());

        Pattern pattern = Pattern.compile("^(.+)@(.+)$");

        String name=info.get("name").toString();
        String cif=info.get("cif").toString();
        String contactEmail=info.get("contactEmail").toString();

        if(!pattern.matcher(contactEmail).find()){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Incorrect email");
        }
        this.companyService.save(name, cif, contactEmail);
    }

    @GetMapping("/getCompanyById/{id}")
    public Company getCompanyById(@PathVariable String id){
        logger.info("GETTING COMPANY BY ID: "+id);
        return this.companyService.findCompanyById(id);
    }

    /*
    Este método es llamado mediante petición GET. Su función es devolver
    una lista que contenga todas las compañías.
     */
    @GetMapping("/getCompanies")
    public List<Company> getCompanies(){
        logger.info("GETTING ALL COMPANIES");
        return this.companyService.getAll();
    }

    /*
    Este método es llamado mediante petición GET y se le pasa como PathVariable
    el id de una compañía. Su función es devolver una lista que contenga todos
    los productos de dicha compañía.
     */
    @GetMapping("/getCompanyProducts/{id}")
    public List<Product> getCompanyProducts(@PathVariable String id){
        logger.info("GETTING COMPANY PRODUCTS BY ID: "+id);
        return this.companyService.findCompanyProducts(id);
    }

    /*
    Este método es llamado mediante petición PUT. Toma como entrada una Compañía.
    Esto se lo pasa al CompanyService para actualizar dicha compañía con los nuevos valores.
     */
    @PutMapping("/updateCompany")
    public Company update(@RequestBody Company company){
        logger.info("UPDATING COMPANY: "+company.toString());
        return this.companyService.update(company);
    }

    /*
    Este método es llamado mediante una petición PUT. Se le pasa como PathVariable
    el id de la compañía y llama al CompanyService para cambiar su estado.
     */
    @PutMapping("/changeState/{companyId}")
    public Company changeState(@PathVariable String companyId){
        logger.info("CHANGING STATE COMPANY: "+companyId);
        return this.companyService.changeState(companyId);
    }

}
