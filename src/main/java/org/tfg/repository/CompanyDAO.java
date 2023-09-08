package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.Company;

public interface CompanyDAO extends JpaRepository<Company, String> {
}
