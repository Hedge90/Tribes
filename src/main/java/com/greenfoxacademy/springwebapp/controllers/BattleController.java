package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.BattleResultDTO;
import com.greenfoxacademy.springwebapp.dtos.BattleTroopListDTO;
import com.greenfoxacademy.springwebapp.services.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/kingdom")
public class BattleController {

    private BattleService battleService;

    @Autowired
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping(path = "/{id}/battle")
    public ResponseEntity<BattleResultDTO> startBattle(@PathVariable Long id, @RequestBody BattleTroopListDTO battleTroopListDTO, HttpServletRequest request) {
        return new ResponseEntity<>(battleService.resolveBattle(id, battleTroopListDTO, request), HttpStatus.OK);
    }
}
