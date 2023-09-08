package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
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
    @PostMapping("/register")
    public void saveCompany(@RequestBody Map<String, Object> info){
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");

        String name=info.get("name").toString();
        String cif=info.get("cif").toString();
        String contactEmail=info.get("contactEmail").toString();

        if(!pattern.matcher(contactEmail).find()){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Incorrect email");
        }
        this.companyService.save(name, cif, contactEmail);
    }

    @GetMapping("/getCompanies")
    public List<Company> getCompanies(){
        return this.companyService.getAll();
    }
}
