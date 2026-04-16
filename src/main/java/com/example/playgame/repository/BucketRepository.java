package com.example.playgame.repository;

import com.example.playgame.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long> {
    Optional<Bucket> findBucketByAccount_Id(Long id);
}
