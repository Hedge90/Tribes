package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.entities.Troop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TroopRepository extends JpaRepository<Troop, Long> {
}

