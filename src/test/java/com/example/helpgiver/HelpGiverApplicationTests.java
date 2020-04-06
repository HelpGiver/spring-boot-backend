package com.example.helpgiver;

import com.example.helpgiver.objects.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class HelpGiverApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void initMocks() {
        User user = new User();
        user.setAddressText("Stockholm");
        user.setEmail("test.user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPublicName("test-user");
        user.setRiskGroup("Helper");
        user.setPhoneNumber("12345");
        user.setAddressCoordinates(new GeoJsonPoint(79.2345, 58.0111));
        user.setHelpRadiusKm(1);
        mongoTemplate.save(user);
    }

    @Test
    public void testEndPoint() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"firstName\" : \"Test\"")))
                .andExpect(content().string(containsString("\"phoneNumber\" : \"12345\"")))
                .andDo(document("users"));
    }

}
