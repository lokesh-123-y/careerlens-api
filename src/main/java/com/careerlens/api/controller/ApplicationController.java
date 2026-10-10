package com.careerlens.api.controller;

import com.careerlens.api.entity.Application;
import com.careerlens.api.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    // Apply for a job
    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(
            @RequestParam String email,
            @RequestParam Long jobId) {

        try {

            Application application =
                    applicationService.applyForJob(
                            email, jobId);

            return ResponseEntity.ok(application);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // Get applications of a user
    @GetMapping("/user")
    public ResponseEntity<?> getUserApplications(
            @RequestParam String email) {

        try {

            List<Application> applications =
                    applicationService
                            .getUserApplications(email);

            return ResponseEntity.ok(applications);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // Get applicants for a job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJobApplications(
            @PathVariable Long jobId) {

        try {

            List<Application> applications =
                    applicationService
                            .getJobApplications(jobId);

            return ResponseEntity.ok(applications);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    // Update application status
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request) {

        try {

            String status = request.get("status");

            if (status == null ||
                    status.trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Status is required");
            }

            Application application =
                    applicationService.updateStatus(
                            applicationId,
                            status);

            return ResponseEntity.ok(application);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}