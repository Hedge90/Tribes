package com.greenfoxacademy.springwebapp.dtos;

public class LocationDTO {
    private Long id;

    private Integer x;

    private Integer y;

    public LocationDTO() {
    }

    public LocationDTO(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
