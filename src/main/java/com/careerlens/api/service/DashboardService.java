package com.careerlens.api.service;

import com.careerlens.api.entity.Application;
import com.careerlens.api.entity.Job;
import com.careerlens.api.entity.Resume;
import com.careerlens.api.entity.User;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final UserService userService;
    private final ResumeService resumeService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final JobMatchingService jobMatchingService;

    public DashboardService(
            UserService userService,
            ResumeService resumeService,
            JobService jobService,
            ApplicationService applicationService,
            JobMatchingService jobMatchingService) {

        this.userService = userService;
        this.resumeService = resumeService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.jobMatchingService = jobMatchingService;
    }

    public Map<String, Object> getDashboard(String email) {

        // USER
        User user = userService.findByEmail(email);

        // RESUMES
        List<Resume> resumes =
                resumeService.getUserResumes(email);

        Resume latestResume = null;

        if (!resumes.isEmpty()) {
            latestResume = resumes.get(0);
        }

        // JOBS
        List<Job> activeJobs =
                jobService.getActiveJobs();

        // RECOMMENDED JOBS
        List<Map<String, Object>> recommendedJobs =
                new ArrayList<>();

        if (latestResume != null &&
                latestResume.getExtractedSkills() != null &&
                !latestResume.getExtractedSkills().trim().isEmpty()) {

            String skills =
                    latestResume.getExtractedSkills();

            for (Job job : activeJobs) {

                Map<String, Object> result =
                        jobMatchingService.matchJob(
                                skills,
                                job
                        );

                recommendedJobs.add(result);
            }

            // Highest match first
            recommendedJobs.sort(
                    (a, b) ->
                            Integer.compare(
                                    (Integer) b.get("matchPercentage"),
                                    (Integer) a.get("matchPercentage")
                            )
            );
        }

        // APPLICATIONS
        List<Application> applications =
                applicationService.getUserApplications(email);

        int applied = 0;
        int shortlisted = 0;
        int selected = 0;
        int rejected = 0;

        for (Application application : applications) {

            String status =
                    application.getStatus();

            if ("APPLIED".equalsIgnoreCase(status)) {
                applied++;
            }

            else if ("SHORTLISTED".equalsIgnoreCase(status)) {
                shortlisted++;
            }

            else if ("SELECTED".equalsIgnoreCase(status)) {
                selected++;
            }

            else if ("REJECTED".equalsIgnoreCase(status)) {
                rejected++;
            }
        }

        // RESPONSE
        Map<String, Object> dashboard =
                new LinkedHashMap<>();

        Map<String, Object> userData =
                new LinkedHashMap<>();

        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());

        dashboard.put("user", userData);

        dashboard.put(
                "latestResume",
                latestResume
        );

        dashboard.put(
                "totalJobs",
                activeJobs.size()
        );

        dashboard.put(
                "recommendedJobs",
                recommendedJobs
        );

        dashboard.put(
                "totalApplications",
                applications.size()
        );

        dashboard.put(
                "applied",
                applied
        );

        dashboard.put(
                "shortlisted",
                shortlisted
        );

        dashboard.put(
                "selected",
                selected
        );

        dashboard.put(
                "rejected",
                rejected
        );

        return dashboard;
    }
}