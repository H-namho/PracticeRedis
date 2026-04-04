package com.example.swaggerprac.repository;

import com.example.swaggerprac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long>,UserRepositryCustom {
}
