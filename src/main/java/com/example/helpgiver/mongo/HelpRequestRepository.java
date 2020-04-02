package com.example.helpgiver.mongo;

import com.example.helpgiver.objects.HelpRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;

public interface HelpRequestRepository extends MongoRepository<HelpRequest, String> {
    GeoResults<HelpRequest> findByAddressCoordinatesNear(Point location, Distance distance);

    Collection<HelpRequest> findByRequesterId(String requesterId);
    Collection<HelpRequest> findByHelperId(String helperId);
}
