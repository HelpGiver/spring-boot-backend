package com.example.helpgiver;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.helpgiver.controller.UserController;
import com.example.helpgiver.mongo.UserRepository;
import com.example.helpgiver.objects.User;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class HelpGiverApplicationTests extends AbstractMockMvcBase {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvcSetup();

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
        ResponseEntity<EntityModel<User>> users = null;

        when(userController.getUserByByEmailOrPhone(email, null)).thenReturn(users);
    }

    @Test
    public void testEndPoint() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

}
