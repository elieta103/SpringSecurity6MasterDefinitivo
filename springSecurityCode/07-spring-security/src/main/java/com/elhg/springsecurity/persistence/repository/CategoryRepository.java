package com.elhg.springsecurity.persistence.repository;


import com.elhg.springsecurity.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
