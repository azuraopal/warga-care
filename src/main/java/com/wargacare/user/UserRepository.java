package com.wargacare.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(CAST(:keyword AS string) IS NULL OR LOWER(u.fullName) LIKE CONCAT('%', LOWER(CAST(:keyword AS string)), '%') OR LOWER(u.email) LIKE CONCAT('%', LOWER(CAST(:keyword AS string)), '%') OR LOWER(u.rt) LIKE CONCAT('%', LOWER(CAST(:keyword AS string)), '%'))")
    Page<User> findAllFiltered(@Param("role") UserRole role, @Param("keyword") String keyword, Pageable pageable);
}
