package com.example.playgame.repository;

import com.example.playgame.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findByOwnerId(Long accountIdm, Pageable pageable);
    List<Purchase> findByOwnerId(Long accountIdm);
}
