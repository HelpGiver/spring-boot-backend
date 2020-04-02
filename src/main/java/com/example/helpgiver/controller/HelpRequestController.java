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
public class HelpRequestController {

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @Autowired
    private UserRepository userRepository;

    // TODO help requests should link to users (but links are generally meh now)

    @GetMapping("helpRequests")
    public ResponseEntity<CollectionModel<EntityModel<HelpRequest>>> getHelpRequests() {
        List<EntityModel<HelpRequest>> helpRequestEntities = StreamSupport.stream(helpRequestRepository.findAll().spliterator(), false)
                .map(helpRequest -> new EntityModel<>(helpRequest,
                        linkTo(methodOn(HelpRequestController.class).getHelpRequest(helpRequest.getId())).withSelfRel(),
                        linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CollectionModel<>(helpRequestEntities,
                        linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withSelfRel()));
    }

    @GetMapping("helpRequest/{id}")
    public ResponseEntity<EntityModel<HelpRequest>> getHelpRequest(@PathVariable String id) {
        return helpRequestRepository.findById(id)
                .map(helpRequest -> new EntityModel<>(helpRequest,
                        linkTo(methodOn(HelpRequestController.class).getHelpRequest(helpRequest.getId())).withSelfRel(),
                        linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("nearbyHelpRequests")
    public ResponseEntity<CollectionModel<EntityModel<GeoResult<HelpRequest>>>> getHelpRequestsGeo(@RequestParam @NotNull double x, @RequestParam @NotNull double y, @RequestParam @NotNull double distanceKm) {
        List<GeoResult<HelpRequest>> helpRequests = helpRequestRepository.findByAddressCoordinatesNear(new Point(x, y), new Distance(distanceKm, Metrics.KILOMETERS)).getContent();

        List<EntityModel<GeoResult<HelpRequest>>> helpRequestEntities = StreamSupport.stream(helpRequests.spliterator(), false)
                .map(helpRequest -> new EntityModel<>(helpRequest,
                        linkTo(methodOn(HelpRequestController.class).getHelpRequest(helpRequest.getContent().getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CollectionModel<>(helpRequestEntities,
                        linkTo(methodOn(HelpRequestController.class).getHelpRequestsGeo(x, y, distanceKm)).withSelfRel()));
    }

    @PostMapping("helpRequest")
    public ResponseEntity<EntityModel<HelpRequest>> createHelpRequest(@RequestBody HelpRequest helpRequest) {
        HelpRequest savedHelpRequest = helpRequestRepository.save(helpRequest);

        return ResponseEntity.ok(new EntityModel<>(savedHelpRequest,
                linkTo(methodOn(HelpRequestController.class).getHelpRequest(savedHelpRequest.getId())).withSelfRel(),
                linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests")));
    }

    @PutMapping("helpRequest")
    public ResponseEntity<EntityModel<HelpRequest>> updateHelpRequest(@RequestBody HelpRequest helpRequest) {
        HelpRequest savedHelpRequest = helpRequestRepository.save(helpRequest);

        return ResponseEntity.ok(new EntityModel<>(savedHelpRequest,
                linkTo(methodOn(HelpRequestController.class).getHelpRequest(savedHelpRequest.getId())).withSelfRel(),
                linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests")));
    }

    @PutMapping("helpRequest/{id}/handler")
    public ResponseEntity<EntityModel<HelpRequest>> addHandler(@PathVariable String id, @RequestBody User handler) {
        Optional<HelpRequest> optionalHelpRequest = helpRequestRepository.findById(id);
        Optional<User> optionalUser = userRepository.findById(handler.getId());

        if (!optionalHelpRequest.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Help request not found");
        }
        if (!optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        HelpRequest foundHelpRequest = optionalHelpRequest.get();
        foundHelpRequest.setHelper(optionalUser.get());
        helpRequestRepository.save(foundHelpRequest);

        return ResponseEntity.ok(new EntityModel<>(foundHelpRequest,
                linkTo(methodOn(HelpRequestController.class).getHelpRequest(foundHelpRequest.getId())).withSelfRel(),
                linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests")));
    }

    @PutMapping("helpRequest/{id}/status")
    public ResponseEntity<EntityModel<HelpRequest>> addHandler(@PathVariable String id, @RequestBody HelpRequest newStatusHelpRequest) {
        Optional<HelpRequest> optionalHelpRequest = helpRequestRepository.findById(id);

        if (!optionalHelpRequest.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Help request not found");
        }

        // TODO check status type, or create enum?

        HelpRequest foundHelpRequest = optionalHelpRequest.get();
        foundHelpRequest.setStatus(newStatusHelpRequest.getStatus());
        helpRequestRepository.save(foundHelpRequest);

        return ResponseEntity.ok(new EntityModel<>(foundHelpRequest,
                linkTo(methodOn(HelpRequestController.class).getHelpRequest(foundHelpRequest.getId())).withSelfRel(),
                linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests")));
    }

    @DeleteMapping("helpRequest/{id}")
    public ResponseEntity<CollectionModel<Object>> deleteHelpRequest(@PathVariable String id) {
        helpRequestRepository.deleteById(id);

        return ResponseEntity.ok(new CollectionModel<>(Collections.emptySet(),
                linkTo(methodOn(HelpRequestController.class).getHelpRequests()).withRel("helpRequests")));
    }
}
