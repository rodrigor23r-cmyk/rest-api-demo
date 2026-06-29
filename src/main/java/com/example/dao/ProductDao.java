package com.example.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entities.Product;

public interface ProductDao extends JpaRepository<Product, Integer> {

    // recuperar productos paginados. con JPQL
    @Query(value = "select p from Product p left join fetch p.presentation", countQuery = "select count(p) from Product p left join p.presentation")
    public Page<Product> findAll(Pageable pageable);

    // recupera los productos ordenados sin paginación
    @Query(value = "select p from Product p left join fetch p.presentation")
    public List<Product> findAll(Sort sort);

    // recupera el producto dado un id 
    @Query(value = "select p from Product p left join fetch p.presentation where p.id = :id")
    public Product findById(int id);


    
}
