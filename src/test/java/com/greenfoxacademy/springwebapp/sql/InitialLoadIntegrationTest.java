package com.greenfoxacademy.springwebapp.sql;

import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.NoSuchElementException;

@SpringBootTest
@ActiveProfiles("test")
public class InitialLoadIntegrationTest {

    private UserRepository userRepository;

    private KingdomRepository kingdomRepository;

    @Autowired
    public InitialLoadIntegrationTest(UserRepository userRepository, KingdomRepository kingdomRepository) {
        this.userRepository = userRepository;
        this.kingdomRepository = kingdomRepository;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    @Sql("/db/test/insert_players.sql")
    public void whenLoadInsertPlayersSQLData_AllPlayersInserted() {
        Assertions.assertEquals(5, userRepository.findAll().size());
        Assertions.assertEquals("Sanyi", userRepository.findById(1L).get().getUsername());
        Assertions.assertEquals("Orange", userRepository.findById(4L).get().getUsername());
        Assertions.assertThrows(NoSuchElementException.class, () -> userRepository.findById(9898L).get().getUsername());
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    @Sql("/db/test/insert_kingdoms.sql")
    public void whenLoadInsertKingdomsSQLData_AllKingdomsInserted() {
        Assertions.assertEquals(5, kingdomRepository.findAll().size());
        Assertions.assertEquals("World", kingdomRepository.findById(2L).get().getName());
        Assertions.assertEquals("Table", kingdomRepository.findById(3L).get().getName());
        Assertions.assertThrows(NoSuchElementException.class, () -> kingdomRepository.findById(8344L).get().getName());
    }
}
