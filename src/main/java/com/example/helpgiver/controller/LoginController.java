package com.example.helpgiver.controller;

import com.example.helpgiver.mongo.SessionRepository;
import com.example.helpgiver.mongo.UserRepository;
import com.example.helpgiver.objects.Session;
import com.example.helpgiver.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1")
public class LoginController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("login")
    public ResponseEntity<Object> logIn(@RequestBody User user) {
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
        if (foundUser.isPresent() && foundUser.get().getPassword().equals(user.getPassword())) {
            Optional<Session> session = sessionRepository.getByUserId(user.getId());
            if (!session.isPresent()) {
                Session newSession = new Session();
                newSession.setUserId(foundUser.get().getId());
                session = Optional.of(sessionRepository.save(newSession));
            }
            return ResponseEntity.ok(session.get());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("logout/{sessionId}")
    public ResponseEntity<Object> logout(@PathVariable String sessionId) {
        sessionRepository.deleteById(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("sessions")
    public List<Session> getSessions() {
        return sessionRepository.findAll();
    }
}
