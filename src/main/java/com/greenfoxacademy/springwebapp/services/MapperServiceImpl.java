package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.*;
import com.greenfoxacademy.springwebapp.entities.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapperServiceImpl implements MapperService {

    private ModelMapper modelMapper;

    @Autowired
    public MapperServiceImpl() {
        modelMapper = new ModelMapper();
    }

    public UserDTO convertUserToUserDTO(User user) {
        modelMapper.typeMap(User.class, UserDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getKingdom().getName(),
                    UserDTO::setKingdomname);
        });
        return modelMapper.map(user, UserDTO.class);
    }

    public RegisteredUserDTO convertUserToRegisteredUserDTO(User user) {
        modelMapper.typeMap(User.class, RegisteredUserDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getKingdom().getId(), RegisteredUserDTO::setKingdomId);
        });
        return modelMapper.map(user, RegisteredUserDTO.class);
    }

    public User convertUserDTOtoUser(UserDTO userDTO) {
        User user = new User();
        modelMapper.typeMap(UserDTO.class, User.class).setPostConverter(context -> {
            context.getDestination().setKingdom(new Kingdom(context.getSource().getKingdomname(), user));
            return context.getDestination();
        }).map(userDTO, user);
        return user;
    }

    public BuildingDTO convertBuildingToBuildingDTO(Building building) {
        return modelMapper.map(building, BuildingDTO.class);
    }

    public KingdomDTO convertKingdomToKingdomDTO(Kingdom kingdom) {
        modelMapper.typeMap(Kingdom.class, KingdomDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getId(),
                    KingdomDTO::setUserId);
        });
        return modelMapper.map(kingdom, KingdomDTO.class);
    }

    public ResourceDTO convertResourceToResourceDTO(Resource resource) {
        return modelMapper.map(resource, ResourceDTO.class);
    }

    public TroopDTO convertTroopToTroopDTO(Troop troop) {
        return modelMapper.map(troop, TroopDTO.class);
    }
}
