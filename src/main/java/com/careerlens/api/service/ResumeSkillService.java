package com.careerlens.api.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ResumeSkillService {

    private static final List<String> KNOWN_SKILLS = Arrays.asList(

            // Programming Languages
            "java",
            "python",
            "c",
            "c++",
            "c#",
            "javascript",
            "typescript",

            // Database
            "sql",
            "mysql",
            "postgresql",
            "mongodb",
            "oracle",

            // Backend
            "spring boot",
            "spring",
            "rest api",
            "rest",
            "hibernate",
            "servlet",
            "jsp",
            "node.js",
            "express.js",
            "flask",
            "django",

            // Frontend
            "html",
            "css",
            "react",
            "angular",
            "bootstrap",

            // AI / ML
            "machine learning",
            "deep learning",
            "artificial intelligence",
            "natural language processing",
            "computer vision",

            // Python Libraries
            "pandas",
            "numpy",
            "scikit-learn",
            "matplotlib",
            "tensorflow",
            "pytorch",

            // Tools
            "git",
            "github",
            "docker",
            "kubernetes",
            "maven",
            "postman",

            // Cloud
            "aws",
            "azure",
            "google cloud"
    );

    public List<String> extractSkills(String resumeText) {

        List<String> foundSkills = new ArrayList<>();

        if (resumeText == null || resumeText.trim().isEmpty()) {
            return foundSkills;
        }

        String text = resumeText.toLowerCase();

        // Normalize symbols
        text = text
                .replace("-", " ")
                .replace("_", " ")
                .replace("/", " ");

        for (String skill : KNOWN_SKILLS) {

            String normalizedSkill = skill
                    .toLowerCase()
                    .replace("-", " ")
                    .replace("_", " ")
                    .replace("/", " ");

            /*
             * Use word boundaries so that:
             *
             * "c" does NOT match every word containing c
             * "rest" does NOT match unrelated words
             * "java" does NOT match "javascript"
             */

            String regex = "(?i)(?<![a-z0-9])"
                    + Pattern.quote(normalizedSkill)
                    + "(?![a-z0-9])";

            if (Pattern.compile(regex).matcher(text).find()) {

                // Avoid duplicate/less specific skills
                if (skill.equals("rest") &&
                        Pattern.compile("(?i)\\brest\\s+api\\b")
                                .matcher(text)
                                .find()) {
                    continue;
                }

                if (skill.equals("spring") &&
                        Pattern.compile("(?i)\\bspring\\s+boot\\b")
                                .matcher(text)
                                .find()) {
                    continue;
                }

                foundSkills.add(skill);
            }
        }

        return foundSkills;
    }
}