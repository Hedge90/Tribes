package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}