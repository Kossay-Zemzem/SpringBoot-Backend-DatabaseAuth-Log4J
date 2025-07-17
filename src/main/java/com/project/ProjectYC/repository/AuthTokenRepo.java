package com.project.ProjectYC.repository;

import com.project.ProjectYC.models.AuthToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokenRepo extends CrudRepository<AuthToken,String> {
    // This interface extends CrudRepository, which provides methods for CRUD operations.
    // You can define custom query methods here if needed.
}
