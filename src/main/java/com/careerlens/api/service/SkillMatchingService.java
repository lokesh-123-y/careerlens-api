package com.careerlens.api.service;

import com.careerlens.api.entity.Job;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillMatchingService {

    public int calculateMatchPercentage(
            String userSkills,
            String requiredSkills) {

        if (userSkills == null ||
                requiredSkills == null ||
                userSkills.trim().isEmpty() ||
                requiredSkills.trim().isEmpty()) {

            return 0;
        }

        Set<String> userSkillSet =
                normalizeSkills(userSkills);

        Set<String> requiredSkillSet =
                normalizeSkills(requiredSkills);

        if (requiredSkillSet.isEmpty()) {
            return 0;
        }

        int matchedSkills = 0;

        for (String skill : requiredSkillSet) {

            if (userSkillSet.contains(skill)) {
                matchedSkills++;
            }
        }

        return (matchedSkills * 100)
                / requiredSkillSet.size();
    }

    private Set<String> normalizeSkills(String skills) {

        Set<String> skillSet = new HashSet<>();

        String[] skillArray =
                skills.toLowerCase()
                        .split(",");

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