package com.lifelink.lifelink.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lifelink.lifelink.entity.AffectedPerson;
import com.lifelink.lifelink.entity.EmergencyNeed;
import com.lifelink.lifelink.entity.Incident;
import com.lifelink.lifelink.entity.Resource;
import com.lifelink.lifelink.entity.ResourceAllocation;
import com.lifelink.lifelink.repository.AffectedPersonRepository;
import com.lifelink.lifelink.repository.EmergencyNeedRepository;
import com.lifelink.lifelink.repository.IncidentRepository;
import com.lifelink.lifelink.repository.ResourceAllocationRepository;
import com.lifelink.lifelink.repository.ResourceRepository;
import com.lifelink.lifelink.service.EmergencyPriorityService;
import com.lifelink.lifelink.service.EmergencyRankingService;
import com.lifelink.lifelink.service.ResourceMatchingService;

@RestController
public class LifelinkController {

    private final IncidentRepository incidentRepository;
    private final AffectedPersonRepository affectedPersonRepository;
    private final EmergencyNeedRepository emergencyNeedRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceAllocationRepository resourceAllocationRepository;
    private final ResourceMatchingService resourceMatchingService;
    private final EmergencyPriorityService emergencyPriorityService;
    private final EmergencyRankingService emergencyRankingService;

    public LifelinkController(
            IncidentRepository incidentRepository,
            AffectedPersonRepository affectedPersonRepository,
            EmergencyNeedRepository emergencyNeedRepository,
            ResourceRepository resourceRepository,
            ResourceAllocationRepository resourceAllocationRepository,
            ResourceMatchingService resourceMatchingService,
            EmergencyPriorityService emergencyPriorityService,
            EmergencyRankingService emergencyRankingService) {

        this.incidentRepository = incidentRepository;
        this.affectedPersonRepository = affectedPersonRepository;
        this.emergencyNeedRepository = emergencyNeedRepository;
        this.resourceRepository = resourceRepository;
        this.resourceAllocationRepository = resourceAllocationRepository;
        this.resourceMatchingService = resourceMatchingService;
        this.emergencyPriorityService = emergencyPriorityService;
        this.emergencyRankingService = emergencyRankingService;
    }

    // =========================================================
    // GENERAL ENDPOINTS
    // =========================================================

    @GetMapping("/backend-status")
     public String backendStatus() {
          return "LIFELINK backend is running successfully.";
}

    @GetMapping("/api/status")
    public String status() {
        return "LIFELINK API is running.";
    }

    // =========================================================
    // INCIDENT APIs
    // =========================================================

    @GetMapping("/api/incidents")
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    @GetMapping("/api/incidents/{id}")
    public Incident getIncidentById(@PathVariable Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Incident not found"));
    }

    @PostMapping("/api/incidents")
    public Incident createIncident(
            @RequestBody Incident incident) {

        return incidentRepository.save(incident);
    }

    @PutMapping("/api/incidents/{id}")
    public Incident updateIncident(
            @PathVariable Long id,
            @RequestBody Incident updatedIncident) {

        Incident existingIncident =
                incidentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Incident not found"));

        existingIncident.setTitle(
                updatedIncident.getTitle());

        existingIncident.setType(
                updatedIncident.getType());

        existingIncident.setLocation(
                updatedIncident.getLocation());

        existingIncident.setSeverity(
                updatedIncident.getSeverity());

        return incidentRepository.save(
                existingIncident);
    }

    @DeleteMapping("/api/incidents/{id}")
    public String deleteIncident(
            @PathVariable Long id) {

        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException(
                    "Incident not found");
        }

        incidentRepository.deleteById(id);

        return "Incident deleted successfully";
    }

    // =========================================================
    // AFFECTED PERSON APIs
    // =========================================================

    @GetMapping("/api/affected-people")
    public List<AffectedPerson> getAllAffectedPeople() {
        return affectedPersonRepository.findAll();
    }

    @GetMapping("/api/affected-people/{id}")
    public AffectedPerson getAffectedPersonById(
            @PathVariable Long id) {

        return affectedPersonRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Affected person not found"));
    }

    @PostMapping("/api/affected-people")
    public AffectedPerson createAffectedPerson(
            @RequestBody AffectedPerson affectedPerson) {

        return affectedPersonRepository.save(
                affectedPerson);
    }

    // =========================================================
    // EMERGENCY NEED APIs
    // =========================================================

    @GetMapping("/api/emergency-needs")
    public List<EmergencyNeed> getAllEmergencyNeeds() {
        return emergencyNeedRepository.findAll();
    }

    @GetMapping("/api/emergency-needs/{id}")
    public EmergencyNeed getEmergencyNeedById(
            @PathVariable Long id) {

        return emergencyNeedRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Emergency need not found"));
    }

    @PostMapping("/api/emergency-needs")
    public EmergencyNeed createEmergencyNeed(
            @RequestBody EmergencyNeedRequest request) {

        AffectedPerson affectedPerson =
                affectedPersonRepository.findById(
                        request.getAffectedPersonId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Affected person not found"));

        EmergencyNeed emergencyNeed =
                new EmergencyNeed(
                        request.getNeedType(),
                        request.getDescription(),
                        request.getUrgency(),
                        request.getStatus(),
                        request.getLocation(),
                        affectedPerson
                );

        return emergencyNeedRepository.save(
                emergencyNeed);
    }

    @PutMapping("/api/emergency-needs/{id}")
    public EmergencyNeed updateEmergencyNeed(
            @PathVariable Long id,
            @RequestBody EmergencyNeedRequest request) {

        EmergencyNeed existingNeed =
                emergencyNeedRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Emergency need not found"));

        existingNeed.setNeedType(
                request.getNeedType());

        existingNeed.setDescription(
                request.getDescription());

        existingNeed.setUrgency(
                request.getUrgency());

        existingNeed.setStatus(
                request.getStatus());

        existingNeed.setLocation(
                request.getLocation());

        if (request.getAffectedPersonId() != null) {

            AffectedPerson affectedPerson =
                    affectedPersonRepository.findById(
                            request.getAffectedPersonId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Affected person not found"));

            existingNeed.setAffectedPerson(
                    affectedPerson);
        }

        return emergencyNeedRepository.save(
                existingNeed);
    }

    @DeleteMapping("/api/emergency-needs/{id}")
    public String deleteEmergencyNeed(
            @PathVariable Long id) {

        if (!emergencyNeedRepository.existsById(id)) {
            throw new RuntimeException(
                    "Emergency need not found");
        }

        emergencyNeedRepository.deleteById(id);

        return "Emergency need deleted successfully";
    }

    // =========================================================
    // EMERGENCY PRIORITY INTELLIGENCE
    // =========================================================

    @GetMapping("/api/emergency-priority/{incidentId}/{needId}")
    public EmergencyPriorityResponse calculateEmergencyPriority(
            @PathVariable Long incidentId,
            @PathVariable Long needId) {

        Incident incident =
                incidentRepository.findById(incidentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Incident not found"));

        EmergencyNeed emergencyNeed =
                emergencyNeedRepository.findById(needId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Emergency need not found"));

        AffectedPerson affectedPerson =
                emergencyNeed.getAffectedPerson();

        int priorityScore =
                emergencyPriorityService.calculatePriorityScore(
                        incident,
                        emergencyNeed,
                        affectedPerson);

        String priorityLevel =
                emergencyPriorityService.determinePriorityLevel(
                        priorityScore);

        String explanation =
                emergencyPriorityService
                        .generatePriorityExplanation(
                                incident,
                                emergencyNeed,
                                affectedPerson);

        return new EmergencyPriorityResponse(
                incident.getId(),
                emergencyNeed.getId(),
                affectedPerson != null
                        ? affectedPerson.getId()
                        : null,
                priorityScore,
                priorityLevel,
                explanation
        );
    }

    // =========================================================
    // EMERGENCY PRIORITY RANKING
    // =========================================================

    @GetMapping("/api/emergency-priority/ranking")
    public List<EmergencyRankingService.EmergencyPriorityResult>
            rankEmergencies() {

        List<EmergencyNeed> emergencyNeeds =
                emergencyNeedRepository.findAll();

        List<Incident> incidents =
                incidentRepository.findAll();

        return emergencyRankingService.rankEmergencies(
                emergencyNeeds,
                incidents);
    }

    // =========================================================
    // RESOURCE APIs
    // =========================================================

    @GetMapping("/api/resources")
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @GetMapping("/api/resources/{id}")
    public Resource getResourceById(
            @PathVariable Long id) {

        return resourceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resource not found"));
    }

    @PostMapping("/api/resources")
    public Resource createResource(
            @RequestBody Resource resource) {

        return resourceRepository.save(resource);
    }

    @PutMapping("/api/resources/{id}")
    public Resource updateResource(
            @PathVariable Long id,
            @RequestBody Resource updatedResource) {

        Resource existingResource =
                resourceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        existingResource.setName(
                updatedResource.getName());

        existingResource.setType(
                updatedResource.getType());

        existingResource.setQuantity(
                updatedResource.getQuantity());

        existingResource.setAvailabilityStatus(
                updatedResource.getAvailabilityStatus());

        existingResource.setLocation(
                updatedResource.getLocation());

        existingResource.setPriority(
                updatedResource.getPriority());

        return resourceRepository.save(
                existingResource);
    }

    @DeleteMapping("/api/resources/{id}")
    public String deleteResource(
            @PathVariable Long id) {

        if (!resourceRepository.existsById(id)) {
            throw new RuntimeException(
                    "Resource not found");
        }

        resourceRepository.deleteById(id);

        return "Resource deleted successfully";
    }

    // =========================================================
    // RESOURCE ALLOCATION APIs
    // =========================================================

    @GetMapping("/api/resource-allocations")
    public List<ResourceAllocation> getAllResourceAllocations() {
        return resourceAllocationRepository.findAll();
    }

    @GetMapping("/api/resource-allocations/{id}")
    public ResourceAllocation getResourceAllocationById(
            @PathVariable Long id) {

        return resourceAllocationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resource allocation not found"));
    }

    // =========================================================
    // MANUAL RESOURCE ALLOCATION
    // =========================================================

    @PostMapping("/api/resource-allocations")
    public ResourceAllocation createResourceAllocation(
            @RequestBody ResourceAllocationRequest request) {

        EmergencyNeed emergencyNeed =
                emergencyNeedRepository.findById(
                        request.getEmergencyNeedId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Emergency need not found"));

        Resource resource =
                resourceRepository.findById(
                        request.getResourceId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        ResourceAllocation allocation =
                new ResourceAllocation(
                        request.getRecommendationReason(),
                        request.getMatchScore(),
                        request.getStatus(),
                        emergencyNeed,
                        resource
                );

        return resourceAllocationRepository.save(
                allocation);
    }

    // =========================================================
    // SPECIFIC RESOURCE RECOMMENDATION
    // =========================================================

    @PostMapping(
            "/api/resource-allocations/recommend")
    public ResourceAllocation recommendResourceAllocation(
            @RequestBody ResourceAllocationRequest request) {

        EmergencyNeed emergencyNeed =
                emergencyNeedRepository.findById(
                        request.getEmergencyNeedId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Emergency need not found"));

        Resource resource =
                resourceRepository.findById(
                        request.getResourceId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        int matchScore =
                resourceMatchingService.calculateMatchScore(
                        emergencyNeed,
                        resource);

        String recommendationReason =
                resourceMatchingService
                        .generateRecommendationReason(
                                emergencyNeed,
                                resource);

        ResourceAllocation allocation =
                new ResourceAllocation();

        allocation.setEmergencyNeed(
                emergencyNeed);

        allocation.setResource(
                resource);

        allocation.setMatchScore(
                matchScore);

        allocation.setRecommendationReason(
                recommendationReason);

        allocation.setStatus(
                "PENDING_HUMAN_VERIFICATION");

        return resourceAllocationRepository.save(
                allocation);
    }

    // =========================================================
    // AUTOMATIC BEST RESOURCE RECOMMENDATION
    // =========================================================

    @PostMapping(
        "/api/resource-allocations/recommend-best/{needId}")
public ResourceAllocation recommendBestResource(
        @PathVariable Long needId) {

    EmergencyNeed emergencyNeed =
            emergencyNeedRepository.findById(needId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Emergency need not found"));

    List<Resource> resources =
            resourceRepository.findAll();

    ResourceAllocation allocation =
            resourceMatchingService.findBestResource(
                    emergencyNeed,
                    resources);

    if (allocation == null) {

        throw new RuntimeException(
                "No suitable resource found for this emergency need");
    }

    ResourceAllocation existingAllocation =
            resourceAllocationRepository
                    .findByEmergencyNeedAndResource(
                            allocation.getEmergencyNeed(),
                            allocation.getResource())
                    .orElse(null);

    if (existingAllocation != null) {
        return existingAllocation;
    }

    return resourceAllocationRepository.save(
            allocation);
}

    // =========================================================
    // UPDATE RESOURCE ALLOCATION
    // =========================================================

    @PutMapping(
            "/api/resource-allocations/{id}")
    public ResourceAllocation updateResourceAllocation(
            @PathVariable Long id,
            @RequestBody ResourceAllocationRequest request) {

        ResourceAllocation existingAllocation =
                resourceAllocationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource allocation not found"));

        existingAllocation.setRecommendationReason(
                request.getRecommendationReason());

        existingAllocation.setMatchScore(
                request.getMatchScore());

        existingAllocation.setStatus(
                request.getStatus());

        if (request.getEmergencyNeedId() != null) {

            EmergencyNeed emergencyNeed =
                    emergencyNeedRepository.findById(
                            request.getEmergencyNeedId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Emergency need not found"));

            existingAllocation.setEmergencyNeed(
                    emergencyNeed);
        }

        if (request.getResourceId() != null) {

            Resource resource =
                    resourceRepository.findById(
                            request.getResourceId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Resource not found"));

            existingAllocation.setResource(
                    resource);
        }

        return resourceAllocationRepository.save(
                existingAllocation);
    }

    // =========================================================
    // DELETE RESOURCE ALLOCATION
    // =========================================================

    @DeleteMapping(
            "/api/resource-allocations/{id}")
    public String deleteResourceAllocation(
            @PathVariable Long id) {

        if (!resourceAllocationRepository.existsById(id)) {
            throw new RuntimeException(
                    "Resource allocation not found");
        }

        resourceAllocationRepository.deleteById(id);

        return "Resource allocation deleted successfully";
    }

    // =========================================================
    // EMERGENCY PRIORITY RESPONSE DTO
    // =========================================================

    public static class EmergencyPriorityResponse {

        private Long incidentId;
        private Long emergencyNeedId;
        private Long affectedPersonId;
        private int priorityScore;
        private String priorityLevel;
        private String explanation;

        public EmergencyPriorityResponse(
                Long incidentId,
                Long emergencyNeedId,
                Long affectedPersonId,
                int priorityScore,
                String priorityLevel,
                String explanation) {

            this.incidentId = incidentId;
            this.emergencyNeedId = emergencyNeedId;
            this.affectedPersonId = affectedPersonId;
            this.priorityScore = priorityScore;
            this.priorityLevel = priorityLevel;
            this.explanation = explanation;
        }

        public Long getIncidentId() {
            return incidentId;
        }

        public Long getEmergencyNeedId() {
            return emergencyNeedId;
        }

        public Long getAffectedPersonId() {
            return affectedPersonId;
        }

        public int getPriorityScore() {
            return priorityScore;
        }

        public String getPriorityLevel() {
            return priorityLevel;
        }

        public String getExplanation() {
            return explanation;
        }
    }

    // =========================================================
    // EMERGENCY NEED REQUEST DTO
    // =========================================================

    public static class EmergencyNeedRequest {

        private String needType;
        private String description;
        private int urgency;
        private String status;
        private String location;
        private Long affectedPersonId;

        public EmergencyNeedRequest() {
        }

        public String getNeedType() {
            return needType;
        }

        public void setNeedType(
                String needType) {

            this.needType = needType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(
                String description) {

            this.description = description;
        }

        public int getUrgency() {
            return urgency;
        }

        public void setUrgency(
                int urgency) {

            this.urgency = urgency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(
                String status) {

            this.status = status;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(
                String location) {

            this.location = location;
        }

        public Long getAffectedPersonId() {
            return affectedPersonId;
        }

        public void setAffectedPersonId(
                Long affectedPersonId) {

            this.affectedPersonId =
                    affectedPersonId;
        }
    }

    // =========================================================
    // RESOURCE ALLOCATION REQUEST DTO
    // =========================================================

    public static class ResourceAllocationRequest {

        private String recommendationReason;
        private int matchScore;
        private String status;
        private Long emergencyNeedId;
        private Long resourceId;

        public ResourceAllocationRequest() {
        }

        public String getRecommendationReason() {
            return recommendationReason;
        }

        public void setRecommendationReason(
                String recommendationReason) {

            this.recommendationReason =
                    recommendationReason;
        }

        public int getMatchScore() {
            return matchScore;
        }

        public void setMatchScore(
                int matchScore) {

            this.matchScore = matchScore;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(
                String status) {

            this.status = status;
        }

        public Long getEmergencyNeedId() {
            return emergencyNeedId;
        }

        public void setEmergencyNeedId(
                Long emergencyNeedId) {

            this.emergencyNeedId =
                    emergencyNeedId;
        }

        public Long getResourceId() {
            return resourceId;
        }

        public void setResourceId(
                Long resourceId) {

            this.resourceId =
                    resourceId;
        }
    }
}