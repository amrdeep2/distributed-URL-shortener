package com.example.url.LinkRepository;

import com.example.url.Model.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LinkRepository extends JpaRepository <Link,Long>{
    boolean existsByCode(String code);
    Optional<Link> findByCode(String code);
    long countByIsActiveTrue();

    long countByExpiresAtBefore(LocalDateTime time);

}
