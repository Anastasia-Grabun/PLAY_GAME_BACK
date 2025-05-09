package com.example.playgame.repository;

import com.example.playgame.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long> {
    Optional<Bucket> findBucketByAccount_Id(Long id);

    @Query(value = """
            SELECT * FROM buckets b 
            WHERE b.account_id = :accountId 
            AND b.type = CAST(:type AS bucket_type)
            """, nativeQuery = true)
    Optional<Bucket> findByAccountIdAndBucketType(@Param("accountId") Long accountId, @Param("type") String type);
}
