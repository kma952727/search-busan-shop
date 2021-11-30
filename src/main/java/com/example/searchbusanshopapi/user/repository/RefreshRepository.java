package com.example.searchbusanshopapi.user.repository;

import com.example.searchbusanshopapi.user.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshToken, Long>{
    RefreshToken findByUsername(String username);
    void deleteByUsername(String username);
}
