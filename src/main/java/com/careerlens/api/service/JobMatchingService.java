package com.careerlens.api.service;

import com.careerlens.api.entity.Job;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobMatchingService {

    private final SkillMatchingService skillMatchingService;

    public JobMatchingService(
            SkillMatchingService skillMatchingService) {

        this.skillMatchingService = skillMatchingService;
    }

    // Match one job with user skills
    public Map<String, Object> matchJob(
            String userSkills,
            Job job) {

        int matchPercentage =
                skillMatchingService.calculateMatchPercentage(
                        userSkills,
                        job.getRequiredSkills()
                );

        Set<String> userSkillSet =
                normalizeSkills(userSkills);

        Set<String> requiredSkillSet =
                normalizeSkills(job.getRequiredSkills());

        List<String> missingSkills =
                new ArrayList<>();

        for (String skill : requiredSkillSet) {

            if (!userSkillSet.contains(skill)) {
                missingSkills.add(skill);
            }
        }

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("jobId", job.getId());
        result.put("jobTitle", job.getTitle());
        result.put("company", job.getCompany());
        result.put("location", job.getLocation());
        result.put("salary", job.getSalary());
        result.put("experience", job.getExperience());
        result.put("jobType", job.getJobType());
        result.put("workMode", job.getWorkMode());
        result.put("matchPercentage", matchPercentage);
        result.put("missingSkills", missingSkills);

        return result;
    }

    // Normalize skills
    private Set<String> normalizeSkills(String skills) {

        Set<String> skillSet =
                new HashSet<>();

        if (skills == null ||
                skills.trim().isEmpty()) {

            return skillSet;
        }

        String[] skillArray =
                skills.toLowerCase().split(",");

        for (String skill : skillArray) {

            String cleanedSkill =
                    skill.trim();

            if (!cleanedSkill.isEmpty()) {
                skillSet.add(cleanedSkill);
            }
        }

        return skillSet;
    }
}