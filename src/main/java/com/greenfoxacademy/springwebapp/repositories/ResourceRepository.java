package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
