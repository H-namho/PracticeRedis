package com.example.swaggerprac.repository;

import com.example.swaggerprac.dto.auth.UserSummaryResponseDto;
import com.example.swaggerprac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryCustom {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("""
            SELECT new com.example.swaggerprac.dto.auth.UserSummaryResponseDto(u.id,u.username,u.email)
            FROM User u
            WHERE u.username <> :username
            """)
    List<UserSummaryResponseDto> findAllUsers(@Param("username") String username);
}
