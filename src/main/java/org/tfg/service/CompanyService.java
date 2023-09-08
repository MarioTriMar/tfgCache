package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tfg.model.Company;
import org.tfg.repository.CompanyDAO;

import java.util.List;

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
