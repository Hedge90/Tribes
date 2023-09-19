package com.greenfoxacademy.springwebapp.dtos;

import java.util.ArrayList;
import java.util.List;

public class ResourceListDTO {
    private List<ResourceDTO> resources = new ArrayList<>();

    public ResourceListDTO() {
    }

    public ResourceListDTO(List<ResourceDTO> resources) {
        this.resources = resources;
    }

    public List<ResourceDTO> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDTO> resources) {
        this.resources = resources;
    }
}
