package com.greenfoxacademy.springwebapp.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/game_logic_config.properties")
public class GameLogicConfiguration {

    @Value("${townhall.cost}")
    private int townhallCost;

    @Value("${townhall.time}")
    private int townhallTime;

    @Value("${townhall.hp}")
    private int townhallHp;

    @Value("${farm.cost}")
    private int farmCost;

    @Value("${farm.time}")
    private int farmTime;

    @Value("${farm.hp}")
    private int farmHp;

    @Value("${mine.cost}")
    private int mineCost;

    @Value("${mine.time}")
    private int mineTime;

    @Value("${mine.hp}")
    private int mineHp;

    @Value("${academy.cost}")
    private int academyCost;

    @Value("${academy.time}")
    private int academyTime;

    @Value("${academy.hp}")
    private int academyHp;

    @Value("${troop.cost}")
    private int troopCost;

    @Value("${troop.time}")
    private int troopTime;

    @Value("${troop.hp}")
    private int troopHp;

    @Value("${resource.goldincreaser}")
    private int goldResourceIncreaser;

    @Value("${resource.foodincreaser}")
    private int foodResourceIncreaser;

    @Value("${resource.goldstarter}")
    private int goldResourceStarter;

    @Value("${resource.foodstarter}")
    private int foodResourceStarter;

    public int getTownhallCost() {
        return townhallCost;
    }

    public int getTownhallTime() {
        return townhallTime;
    }

    public int getTownhallHp() {
        return townhallHp;
    }

    public int getFarmCost() {
        return farmCost;
    }

    public int getFarmTime() {
        return farmTime;
    }

    public int getFarmHp() {
        return farmHp;
    }

    public int getMineCost() {
        return mineCost;
    }

    public int getMineTime() {
        return mineTime;
    }

    public int getMineHp() {
        return mineHp;
    }

    public int getAcademyCost() {
        return academyCost;
    }

    public int getAcademyTime() {
        return academyTime;
    }

    public int getAcademyHp() {
        return academyHp;
    }

    public int getTroopCost() {
        return troopCost;
    }

    public int getTroopTime() {
        return troopTime;
    }

    public int getTroopHp() {
        return troopHp;
    }

    public int getGoldResourceIncreaser() {
        return goldResourceIncreaser;
    }

    public int getFoodResourceIncreaser() {
        return foodResourceIncreaser;
    }

    public int getGoldResourceStarter() {
        return goldResourceStarter;
    }

    public int getFoodResourceStarter() {
        return foodResourceStarter;
    }
}
