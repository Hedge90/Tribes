package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.BattleResultDTO;
import com.greenfoxacademy.springwebapp.dtos.BattleTroopListDTO;

import javax.servlet.http.HttpServletRequest;

public interface BattleService {

    BattleResultDTO resolveBattle(Long enemyKingdomId, BattleTroopListDTO battleTroopListDTO, HttpServletRequest httpServletRequest);
}
