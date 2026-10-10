package com.careerlens.api.controller;

import com.careerlens.api.entity.Job;
import com.careerlens.api.entity.Resume;
import com.careerlens.api.service.JobMatchingService;
import com.careerlens.api.service.JobService;
import com.careerlens.api.service.ResumeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/matching")
public class JobMatchingController {

    private final JobMatchingService jobMatchingService;
    private final JobService jobService;
    private final ResumeService resumeService;

    public JobMatchingController(
            JobMatchingService jobMatchingService,
            JobService jobService,
            ResumeService resumeService) {

        this.jobMatchingService = jobMatchingService;
        this.jobService = jobService;
        this.resumeService = resumeService;
    }

    // MATCH ONE JOB WITH RESUME
    @GetMapping("/resume")
    public ResponseEntity<?> matchResumeWithJob(
            @RequestParam String email,
            @RequestParam Long jobId) {

        try {

            List<Resume> resumes =
                    resumeService.getUserResumes(email);

            if (resumes.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Please upload a resume first");
            }

            Resume latestResume = resumes.get(0);

            String extractedSkills =
                    latestResume.getExtractedSkills();

            if (extractedSkills == null ||
                    extractedSkills.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("No skills found in latest resume");
            }

            Job job =
                    jobService.getJobById(jobId);

            Map<String, Object> result =
                    jobMatchingService.matchJob(
                            extractedSkills,
                            job
                    );

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // RECOMMENDED JOBS
    @GetMapping("/recommended")
    public ResponseEntity<?> getRecommendedJobs(
            @RequestParam String email) {

        try {

            // Get user's resumes
            List<Resume> resumes =
                    resumeService.getUserResumes(email);

            if (resumes.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Please upload a resume first");
            }

            // Latest resume
            Resume latestResume =
                    resumes.get(0);

            String extractedSkills =
                    latestResume.getExtractedSkills();

            if (extractedSkills == null ||
                    extractedSkills.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("No skills found in latest resume");
            }

            // Get all active jobs
            List<Job> jobs =
                    jobService.getActiveJobs();

            List<Map<String, Object>> recommendedJobs =
                    new ArrayList<>();

            // Match resume with every job
            for (Job job : jobs) {

                Map<String, Object> result =
                        jobMatchingService.matchJob(
                                extractedSkills,
                                job
                        );

                recommendedJobs.add(result);
            }

            // Sort highest match first
            recommendedJobs.sort(
                    (a, b) ->
                            Integer.compare(
                                    (Integer) b.get("matchPercentage"),
                                    (Integer) a.get("matchPercentage")
                            )
            );

            return ResponseEntity.ok(recommendedJobs);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}