package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.*;
import com.greenfoxacademy.springwebapp.entities.*;

public interface MapperService {
    public UserDTO convertUserToUserDTO(User user);

    RegisteredUserDTO convertUserToRegisteredUserDTO(User user);

    public User convertUserDTOtoUser(UserDTO userDTO);

    public BuildingDTO convertBuildingToBuildingDTO(Building building);

    public KingdomDTO convertKingdomToKingdomDTO(Kingdom kingdom);

    public ResourceDTO convertResourceToResourceDTO(Resource resource);

    public TroopDTO convertTroopToTroopDTO(Troop troop);
}
