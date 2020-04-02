package com.example.helpgiver.controller.nohateoas;

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("vx")
public class HelpRequestControllerVX {

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("helpRequests")
    public ResponseEntity<Collection<HelpRequest>> getHelpRequests() {
        Collection<HelpRequest> helpRequests = helpRequestRepository.findAll();

        return ResponseEntity.ok(helpRequests);
    }

    @GetMapping("helpRequest/{id}")
    public ResponseEntity<HelpRequest> getHelpRequest(@PathVariable String id) {
        return helpRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("nearbyHelpRequests")
    public ResponseEntity<Collection<GeoResult<HelpRequest>>> getHelpRequestsGeo(@RequestParam @NotNull double x, @RequestParam @NotNull double y, @RequestParam @NotNull double distanceKm) {
        Collection<GeoResult<HelpRequest>> helpRequests = helpRequestRepository.findByAddressCoordinatesNear(new Point(x, y), new Distance(distanceKm, Metrics.KILOMETERS)).getContent();

        return ResponseEntity.ok(helpRequests);
    }

    @GetMapping("user/{id}/nearbyHelpRequests")
    public ResponseEntity<Collection<GeoResult<HelpRequest>>> getHelpRequestsNearUser(@PathVariable String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User foundUser = optionalUser.get();
        if (!"Helper".equals(foundUser.getRiskGroup())) {
            return ResponseEntity.badRequest().build(); // TODO message
        }

        Collection<GeoResult<HelpRequest>> helpRequests =
                helpRequestRepository.findByAddressCoordinatesNear(foundUser.getAddressCoordinates(),
                        new Distance(foundUser.getHelpRadiusKm(), Metrics.KILOMETERS)).getContent();

        return ResponseEntity.ok(helpRequests);
    }

    @PostMapping("helpRequest")
    public ResponseEntity<HelpRequest> createHelpRequest(@RequestBody HelpRequest helpRequest) {
        HelpRequest savedHelpRequest = helpRequestRepository.save(helpRequest);

        return ResponseEntity.ok(savedHelpRequest);
    }

    @PutMapping("helpRequest")
    public ResponseEntity<HelpRequest> updateHelpRequest(@RequestBody HelpRequest helpRequest) {
        HelpRequest savedHelpRequest = helpRequestRepository.save(helpRequest);

        return ResponseEntity.ok(savedHelpRequest);
    }

    @PutMapping("helpRequest/{id}/handler")
    public ResponseEntity<HelpRequest> addHandler(@PathVariable String id, @RequestBody User handler) {
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

        return ResponseEntity.ok(foundHelpRequest);
    }

    @PutMapping("helpRequest/{id}/status")
    public ResponseEntity<HelpRequest> addHandler(@PathVariable String id, @RequestBody HelpRequest newStatusHelpRequest) {
        Optional<HelpRequest> optionalHelpRequest = helpRequestRepository.findById(id);

        if (!optionalHelpRequest.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Help request not found");
        }

        // TODO check status type, or create enum?

        HelpRequest foundHelpRequest = optionalHelpRequest.get();
        foundHelpRequest.setStatus(newStatusHelpRequest.getStatus());
        helpRequestRepository.save(foundHelpRequest);

        return ResponseEntity.ok(foundHelpRequest);
    }

    @DeleteMapping("helpRequest/{id}")
    public ResponseEntity<Object> deleteHelpRequest(@PathVariable String id) {
        helpRequestRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("user/{requesterId}/helpWantedRequests")
    public ResponseEntity<Collection<HelpRequest>> getByRequesterId(@PathVariable String requesterId) {
        Collection<HelpRequest> helpRequestEntities = helpRequestRepository.findByRequesterId(requesterId);

        return ResponseEntity.ok(helpRequestEntities);
    }

    @GetMapping("user/{helperId}/helpGivenRequests")
    public ResponseEntity<Collection<HelpRequest>> getByHelperId(@PathVariable String helperId) {
        Collection<HelpRequest> helpRequestEntities = helpRequestRepository.findByHelperId(helperId);

        return ResponseEntity.ok(helpRequestEntities);
    }
}
