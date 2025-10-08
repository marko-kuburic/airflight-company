package com.aircompany.hr.repository;

import com.aircompany.hr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);
    
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    List<User> findAllByEmail(@Param("email") String email);
    
    boolean existsByEmail(String email);
}
