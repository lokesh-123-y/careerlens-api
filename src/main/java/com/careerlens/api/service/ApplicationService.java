package com.careerlens.api.service;

import com.careerlens.api.entity.Application;
import com.careerlens.api.entity.Job;
import com.careerlens.api.entity.User;
import com.careerlens.api.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final JobService jobService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserService userService,
            JobService jobService) {

        this.applicationRepository = applicationRepository;
        this.userService = userService;
        this.jobService = jobService;
    }

    // Apply for a job
    public Application applyForJob(
            String email,
            Long jobId) {

        User user = userService.findByEmail(email);

        Job job = jobService.getJobById(jobId);

        // Prevent duplicate application
        if (applicationRepository
                .existsByUserIdAndJobId(user.getId(), job.getId())) {

            throw new RuntimeException(
                    "You have already applied for this job");
        }

        Application application = new Application();

        application.setUser(user);
        application.setJob(job);
        application.setStatus("APPLIED");
        application.setAppliedDate(
                LocalDateTime.now().toString());

        return applicationRepository.save(application);
    }

    // Get all applications of a user
    public List<Application> getUserApplications(
            String email) {

        User user = userService.findByEmail(email);

        return applicationRepository
                .findByUserId(user.getId());
    }

    // Get all applicants for a job
    public List<Application> getJobApplications(
            Long jobId) {

        // Check whether job exists
        jobService.getJobById(jobId);

        return applicationRepository
                .findByJobId(jobId);
    }

    // Update application status
    public Application updateStatus(
            Long applicationId,
            String status) {

        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        application.setStatus(status);

        return applicationRepository.save(application);
    }
}