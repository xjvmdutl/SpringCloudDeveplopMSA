package com.example.userservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUserId(String userId);

    @Query("delete from UserEntity u where u.userId = :userId")
    void deleteByUserId(String userId);

    UserEntity findByEmail(String email);
}
