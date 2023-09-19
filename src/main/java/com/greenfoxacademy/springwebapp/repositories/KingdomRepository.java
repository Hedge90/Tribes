package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.entities.Kingdom;
import com.greenfoxacademy.springwebapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KingdomRepository extends JpaRepository<Kingdom, Long> {
    Optional<Kingdom> getKingdomByUser(User user);

    Optional<Kingdom> getKingdomById(Long id);
}
