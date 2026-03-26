package com.example.crud2.repository;

import com.example.crud2.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long>,
        QueryByExampleExecutor<ClientEntity> {

    boolean existsByEmail(String email);
}
