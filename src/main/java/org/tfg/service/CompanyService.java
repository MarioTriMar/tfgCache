package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.repository.CompanyDAO;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private CompanyDAO companyDAO;

    public void save(String name, String cif, String contactEmail){
        Company company=new Company();
        company.setName(name);
        company.setCif(cif);
        company.setContactEmail(contactEmail);
        this.companyDAO.save(company);
    }

    public Company findCompanyById(String id) {
        Optional<Company> optCompany=this.companyDAO.findById(id);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        return optCompany.get();
    }
    public List<Company> getAll(){
        return this.companyDAO.findAll();
    }

    public void update(Company company) {
        this.companyDAO.save(company);
    }

    public void deleteById(String companyId) {
        this.companyDAO.deleteById(companyId);
    }


}
