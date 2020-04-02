package com.example.helpgiver.mongo;

import com.example.helpgiver.objects.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessionRepository extends MongoRepository<Session, String> {
    Optional<Session> getById(String id);
    Optional<Session> getByUserId(String userId);
}
