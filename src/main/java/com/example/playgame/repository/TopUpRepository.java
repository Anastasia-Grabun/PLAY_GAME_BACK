package com.example.playgame.repository;

import com.example.playgame.entity.TopUp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopUpRepository extends JpaRepository<TopUp, Long> {
    Page<TopUp> findByAccountId(Long id, Pageable pageable);
}
