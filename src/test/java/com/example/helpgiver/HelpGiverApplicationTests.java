package com.example.helpgiver;

import com.example.helpgiver.controller.UserController;
import com.example.helpgiver.mongo.UserRepository;
import com.example.helpgiver.objects.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class HelpGiverApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void initMocks() throws Exception {
        User user = new User();
        user.setAddressText("Stockholm");
        user.setEmail("syed@kashan.ali");
        user.setFirstName("Kashan");
        user.setLastName("Ali");
        user.setPassword("yyyyyyyy");
        user.setPublicName("SK");
        user.setRiskGroup("Helper");
        user.setPhoneNumber("+488888888");
        user.setAddressCoordinates(new GeoJsonPoint(79.2345, 58.0111));
        user.setHelpRadiusKm(1);
        userRepository.save(user);

        Optional<String> email = Optional.of("syed@kashan.ali");
        ResponseEntity<CollectionModel<EntityModel<User>>> users = ResponseEntity
                .ok(new CollectionModel(Arrays.asList(new EntityModel(user))));

        when(userController.getUsers()).thenReturn(users);
    }

    @Test
    public void testEndPoint() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

}
