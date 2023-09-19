package com.greenfoxacademy.springwebapp.configurations;

import com.greenfoxacademy.springwebapp.dtos.*;
import com.greenfoxacademy.springwebapp.entities.*;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;
import com.greenfoxacademy.springwebapp.enums.ResourceType;
import com.greenfoxacademy.springwebapp.services.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestConfiguration {

    private final String fakeUserName = "fakeUser";

    private final String fakeUserEmail = "fake_user@email.com";

    private final String fakeUserPassword = "password";

    private final String fakeUserKingdomName = "Fake Kingdom";

    private final int fakeUserGoldStarter = 1000;

    private final int fakeUserFoodStarter = 1000;

    private GameLogicConfiguration configuration;

    private MapperService mapperService;

    @Autowired
    public TestConfiguration(GameLogicConfiguration configuration, MapperService mapperService) {
        this.configuration = configuration;
        this.mapperService = mapperService;
    }


    @Bean(name = "fakeUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeUser() {
        User fakeUser = new User(fakeUserName, fakeUserEmail, fakeUserPassword, fakeUserKingdomName);
        return fakeUser;
    }

    @Bean(name = "fullFakeUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFullFakeUser() {
        User fakeUser = getFakeUser();
        new Building(fakeUser.getKingdom(), BuildingTypes.TOWNHALL, configuration.getTownhallHp());
        new Building(fakeUser.getKingdom(), BuildingTypes.FARM, configuration.getFarmHp());
        new Building(fakeUser.getKingdom(), BuildingTypes.MINE, configuration.getMineHp());
        new Building(fakeUser.getKingdom(), BuildingTypes.ACADEMY, configuration.getAcademyHp());
        for (Building building : fakeUser.getKingdom().getBuildings()) {
            building.setStartedAt(System.currentTimeMillis());
            building.setFinishedAt(System.currentTimeMillis());
        }
        new Resource(ResourceType.GOLD, fakeUserGoldStarter, 0, fakeUser.getKingdom());
        new Resource(ResourceType.FOOD, fakeUserFoodStarter, 0, fakeUser.getKingdom());
        new Troop(fakeUser.getKingdom(), fakeUser.getKingdom().getBuildings().get(3).getLevel());
        new Troop(fakeUser.getKingdom(), fakeUser.getKingdom().getBuildings().get(3).getLevel());
        return fakeUser;
    }

    @Bean(name = "fakeUserWithIds")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeUserWithIds() {
        User fakeUser = getFakeUser();
        fakeUser.setId(1L);
        fakeUser.getKingdom().setId(1L);
        return fakeUser;
    }

    @Bean(name = "fullFakeUserWithIds")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFullFakeUserWithIds() {
        User fakeUser = getFullFakeUser();
        fakeUser.setId(1L);
        fakeUser.getKingdom().setId(1L);
        fakeUser.getKingdom().getBuildings().get(0).setId(1L);
        fakeUser.getKingdom().getBuildings().get(1).setId(2L);
        fakeUser.getKingdom().getBuildings().get(2).setId(3L);
        fakeUser.getKingdom().getBuildings().get(3).setId(4L);
        fakeUser.getKingdom().getResources().get(0).setId(1L);
        fakeUser.getKingdom().getResources().get(1).setId(2L);
        fakeUser.getKingdom().getTroops().get(0).setId(1L);
        fakeUser.getKingdom().getTroops().get(1).setId(2L);
        return fakeUser;
    }

    @Bean(name = "fakeUserDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    UserDTO getFakeUserDTO() {
        return mapperService.convertUserToUserDTO(getFakeUserWithIds());
    }

    @Bean(name = "fakeTownHallBuildingDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    BuildingDTO getFakeTownHallBuildingDTO() {
        return mapperService.convertBuildingToBuildingDTO(getFullFakeUserWithIds().getKingdom().getBuildings().get(0));
    }

    @Bean(name = "fakeFarmBuildingDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    BuildingDTO getFakeFarmBuildingDTO() {
        return mapperService.convertBuildingToBuildingDTO(getFullFakeUserWithIds().getKingdom().getBuildings().get(1));
    }

    @Bean(name = "fakeMineBuildingDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    BuildingDTO getFakeMineBuildingDTO() {
        return mapperService.convertBuildingToBuildingDTO(getFullFakeUserWithIds().getKingdom().getBuildings().get(2));
    }

    @Bean(name = "fakeAcademyBuildingDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    BuildingDTO getFakeAcademyBuildingDTO() {
        return mapperService.convertBuildingToBuildingDTO(getFullFakeUserWithIds().getKingdom().getBuildings().get(3));
    }

    @Bean(name = "fakeGoldResourceDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    ResourceDTO getFakeGoldResourceDTO() {
        return mapperService.convertResourceToResourceDTO(getFullFakeUserWithIds().getKingdom().getResources().get(0));
    }

    @Bean(name = "fakeFoodResourceDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    ResourceDTO getFakeFoodResourceDTO() {
        return mapperService.convertResourceToResourceDTO(getFullFakeUserWithIds().getKingdom().getResources().get(1));
    }

    @Bean(name = "fakeTroopDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    TroopDTO getFakeTroopDTO() {
        return mapperService.convertTroopToTroopDTO(getFullFakeUserWithIds().getKingdom().getTroops().get(0));
    }

    @Bean(name = "fakeKingdomDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    KingdomDTO getFakeKingdomDTO() {
        return mapperService.convertKingdomToKingdomDTO(getFullFakeUser().getKingdom());
    }
}
