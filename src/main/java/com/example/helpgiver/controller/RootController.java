package com.example.helpgiver.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {

    @GetMapping
    public ResponseEntity<CollectionModel<Object>> get() {
        return ResponseEntity.ok(new CollectionModel<>(Collections.emptySet(),
                linkTo(methodOn(RootController.class).getV1()).withRel("v1")));
    }

    @GetMapping("v1")
    public ResponseEntity<CollectionModel<Object>> getV1() {
        return ResponseEntity.ok(new CollectionModel<>(Collections.emptySet(),
                linkTo(methodOn(UserController.class).getUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).getUserById(null)).withRel("user"),
                linkTo(methodOn(UserController.class).getUserByByEmailOrPhone(null, null)).withRel("user"),
                linkTo(methodOn(UserController.class).getUserGeo(0, 0, 0)).withRel("nearbyUsers"),

                linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests"),
                linkTo(methodOn(HelpRequestController.class).getByHelperId(null)).withRel("helpGivenRequests"),
                linkTo(methodOn(HelpRequestController.class).getByRequesterId(null)).withRel("helpWantedRequests"),
                linkTo(methodOn(HelpRequestController.class).getHelpRequestsGeo(0, 0, 0)).withRel("nearbyHelpRequests")));
    }
}
