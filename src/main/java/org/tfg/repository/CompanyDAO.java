package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.Company;

@Repository
public interface CompanyDAO extends JpaRepository<Company, String> {
}
