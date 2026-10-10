package com.careerlens.api.controller;

import com.careerlens.api.entity.Job;
import com.careerlens.api.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Create a new job
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {

        try {
            Job createdJob = jobService.createJob(job);
            return ResponseEntity.ok(createdJob);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // Get all jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(
                jobService.getAllJobs()
        );
    }

    // Get job by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(
            @PathVariable Long id) {

        try {
            return ResponseEntity.ok(
                    jobService.getJobById(id)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete job
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(
            @PathVariable Long id) {

        try {
            jobService.deleteJob(id);

            return ResponseEntity.ok(
                    "Job deleted successfully"
            );

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}