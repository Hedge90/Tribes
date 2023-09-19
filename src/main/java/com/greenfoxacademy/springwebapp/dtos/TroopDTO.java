package com.greenfoxacademy.springwebapp.dtos;

public class TroopDTO {
    private Long id;

    private int level;

    private int hp;

    private int attack;

    private int defence;

    private Long startedAt;

    private Long finishedAt;

    public TroopDTO() {
    }

    public TroopDTO(Long id, int level, int hp, int attack, int defence, Long startedAt, Long finishedAt) {
        this.id = id;
        this.level = level;
        this.hp = hp;
        this.attack = attack;
        this.defence = defence;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Long finishedAt) {
        this.finishedAt = finishedAt;
    }
}
