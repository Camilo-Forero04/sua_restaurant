package com.cami.restaurant.repository;

import com.cami.restaurant.model.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaiterRepository extends JpaRepository <Waiter,Long> {
}
