package com.nexuscart.inventory.repository;

import com.nexuscart.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByCode(String code);

    List<Warehouse> findByActiveTrue();

    Optional<Warehouse> findByIsPrimaryTrue();

    boolean existsByCode(String code);
}
