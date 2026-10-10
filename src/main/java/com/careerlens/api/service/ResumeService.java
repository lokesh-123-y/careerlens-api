package com.careerlens.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.careerlens.api.entity.Resume;
import com.careerlens.api.entity.User;
import com.careerlens.api.repository.ResumeRepository;
import com.careerlens.api.repository.UserRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeTextExtractorService textExtractorService;
    private final ResumeSkillService resumeSkillService;

    private final String uploadDirectory = "uploads/resumes";

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            ResumeTextExtractorService textExtractorService,
            ResumeSkillService resumeSkillService) {

        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.textExtractorService = textExtractorService;
        this.resumeSkillService = resumeSkillService;
    }

    public Resume uploadResume(
            MultipartFile file,
            String email) throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Please select a resume file");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new RuntimeException(
                    "Invalid resume file name");
        }

        String lowerFileName = fileName.toLowerCase();

        if (!lowerFileName.endsWith(".pdf")
                && !lowerFileName.endsWith(".docx")) {
            throw new RuntimeException(
                    "Only PDF and DOCX files are supported");
        }

        // Extract text from the uploaded resume
        String resumeText = textExtractorService.extractText(file);

        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new RuntimeException(
                    "Could not extract text. Please use a text-based PDF or DOCX file.");
        }

        // Detect skills
        List<String> detectedSkills =
                resumeSkillService.extractSkills(resumeText);

        // Create upload folder
        Path uploadPath = Paths.get(uploadDirectory);
        Files.createDirectories(uploadPath);

        // Save file with a unique name
        String uniqueFileName = user.getId() + "_"
                + System.currentTimeMillis() + "_"
                + Paths.get(fileName).getFileName();

        Path filePath = uploadPath.resolve(uniqueFileName);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING);

        // Update existing resume or create a new one
        Resume resume = resumeRepository.findByUser(user)
                .orElse(new Resume());

        resume.setFileName(fileName);
        resume.setFilePath(filePath.toString());
        resume.setFileType(
                lowerFileName.endsWith(".pdf")
                        ? "application/pdf"
                        : "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        resume.setSkills(String.join(", ", detectedSkills));
        resume.setUser(user);
        resume.setUploadedAt(LocalDateTime.now());

        return resumeRepository.save(resume);
    }
}