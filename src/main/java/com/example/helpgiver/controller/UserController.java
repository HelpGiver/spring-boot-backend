package com.example.helpgiver.controller;

import com.example.helpgiver.mongo.HelpRequestRepository;
import com.example.helpgiver.mongo.UserRepository;
import com.example.helpgiver.objects.HelpRequest;
import com.example.helpgiver.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @GetMapping("/user/{id}")
    public ResponseEntity<EntityModel<User>> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> new EntityModel<>(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("users")
    public ResponseEntity<CollectionModel<EntityModel<User>>> getUsers() {
        List<EntityModel<User>> userEntities = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(user -> new EntityModel<>(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CollectionModel<>(userEntities,
                        linkTo(methodOn(UserController.class).getUsers()).withSelfRel()));
    }

    @GetMapping("user")
    public ResponseEntity<EntityModel<User>> getUserByByEmailOrPhone(@RequestParam Optional<String> email, @RequestParam Optional<String> phoneNumber) {
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
                .map(u -> new EntityModel<>(u,
                        linkTo(methodOn(UserController.class).getUserByByEmailOrPhone(email, phoneNumber)).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("user/{requesterId}/needsHelp")
    public ResponseEntity<CollectionModel<EntityModel<HelpRequest>>> getByRequesterId(@PathVariable String requesterId) {
        List<EntityModel<HelpRequest>> helpRequestEntities = StreamSupport.stream(helpRequestRepository.findByRequesterId(requesterId).spliterator(), false)
                .map(helpRequest -> new EntityModel<>(helpRequest,
                        linkTo(methodOn(UserController.class).getByRequesterId(requesterId)).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CollectionModel<>(helpRequestEntities,
                        linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withSelfRel()));
    }

    @GetMapping("user/{helperId}/helps")
    public ResponseEntity<CollectionModel<EntityModel<HelpRequest>>> getByHelperId(@PathVariable String helperId) {
        List<EntityModel<HelpRequest>> helpRequestEntities = StreamSupport.stream(helpRequestRepository.findByHelperId(helperId).spliterator(), false)
                .map(helpRequest -> new EntityModel<>(helpRequest,
                        linkTo(methodOn(UserController.class).getByHelperId(helperId)).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CollectionModel<>(helpRequestEntities,
                        linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withSelfRel()));
    }

    @PostMapping("user")
    public ResponseEntity<EntityModel<User>> addUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(new EntityModel<>(savedUser,
                linkTo(methodOn(UserController.class).getUserById(savedUser.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUsers()).withRel("users")));
    }

    @PutMapping("user")
    public ResponseEntity<EntityModel<User>> updateUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(new EntityModel<>(savedUser,
                linkTo(methodOn(UserController.class).getUserById(savedUser.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUsers()).withRel("users")));
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<CollectionModel<Object>> deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);

        return ResponseEntity.ok(new CollectionModel<>(Collections.emptySet(),
                linkTo(methodOn(UserController.class).getUsers()).withRel("helpRequests")));
    }

    @GetMapping("nearbyUsers")
    ResponseEntity<CollectionModel<EntityModel<GeoResult<User>>>> getUserGeo(@RequestParam @NotNull double x, @RequestParam @NotNull double y, @RequestParam @NotNull double distanceKm) {
        List<GeoResult<User>> users = userRepository.findByAddressCoordinatesNear(new Point(x, y), new Distance(distanceKm, Metrics.KILOMETERS)).getContent();

        List<EntityModel<GeoResult<User>>> userEntities = StreamSupport.stream(users.spliterator(), false)
                .map(user -> new EntityModel<>(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getContent().getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CollectionModel<>(userEntities,
                        linkTo(methodOn(UserController.class).getUserGeo(x, y, distanceKm)).withSelfRel()));
    }
}
