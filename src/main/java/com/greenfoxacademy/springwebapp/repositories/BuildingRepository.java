package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.entities.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
}