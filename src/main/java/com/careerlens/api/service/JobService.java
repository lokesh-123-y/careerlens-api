package com.careerlens.api.service;

import com.careerlens.api.entity.Job;
import com.careerlens.api.repository.JobRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // CREATE JOB
    public Job createJob(Job job) {

        if (job.getStatus() == null ||
                job.getStatus().trim().isEmpty()) {

            job.setStatus("ACTIVE");
        }

        return jobRepository.save(job);
    }

    // GET ALL JOBS
    public List<Job> getAllJobs() {

        return jobRepository.findAll();
    }

    // GET ACTIVE JOBS
    public List<Job> getActiveJobs() {

        return jobRepository.findByStatus("ACTIVE");
    }

    // GET JOB BY ID
    public Job getJobById(Long id) {

        return jobRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));
    }

    // DELETE JOB
    public void deleteJob(Long id) {

        if (!jobRepository.existsById(id)) {

            throw new RuntimeException("Job not found");
        }

        jobRepository.deleteById(id);
    }
}