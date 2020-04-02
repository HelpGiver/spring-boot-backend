package com.example.helpgiver.controller.nohateoas;

import com.example.helpgiver.mongo.HelpRequestRepository;
import com.example.helpgiver.mongo.UserRepository;
import com.example.helpgiver.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("vx")
public class UserControllerVX {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("users")
    public ResponseEntity<Collection<User>> getUsers() {
        Collection<User> userEntities = userRepository.findAll();

        return ResponseEntity.ok(userEntities);
    }

    @GetMapping("user")
    public ResponseEntity<User> getUserByByEmailOrPhone(@RequestParam Optional<String> email, @RequestParam Optional<String> phoneNumber) {
        // To prevent not matching emails and phones
        if (email.isPresent() && phoneNumber.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You need to provide either email or phone number but not both");
        }

        Optional<User> user = Optional.empty();

        if (email.isPresent()) {
            user = userRepository.findByEmail(email.get());
        } else if (phoneNumber.isPresent()) {
            user = userRepository.findByPhoneNumber(phoneNumber.get());
        }

        return user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("user")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("user")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("nearbyUsers")
    ResponseEntity<Collection<GeoResult<User>>> getUserGeo(@RequestParam @NotNull double x, @RequestParam @NotNull double y, @RequestParam @NotNull double distanceKm) {
        List<GeoResult<User>> users = userRepository.findByAddressCoordinatesNear(new Point(x, y), new Distance(distanceKm, Metrics.KILOMETERS)).getContent();
        
        return ResponseEntity.ok(users);
    }
}
