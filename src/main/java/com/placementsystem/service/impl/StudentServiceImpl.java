package com.placementsystem.service.impl;

import com.placementsystem.dto.request.StudentProfileRequest;
import com.placementsystem.dto.response.ApplicationResponse;
import com.placementsystem.dto.response.JobDriveResponse;
import com.placementsystem.dto.response.StudentResponse;
import com.placementsystem.entity.Application;
import com.placementsystem.entity.JobDrive;
import com.placementsystem.entity.Student;
import com.placementsystem.entity.User;
import com.placementsystem.exception.BadRequestException;
import com.placementsystem.exception.ResourceNotFoundException;
import com.placementsystem.repository.ApplicationRepository;
import com.placementsystem.repository.JobDriveRepository;
import com.placementsystem.repository.StudentRepository;
import com.placementsystem.repository.UserRepository;
import com.placementsystem.service.StudentService;
import com.placementsystem.util.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final JobDriveRepository jobDriveRepository;
    private final ApplicationRepository applicationRepository;

    // ═══════════════════════════════════════════════════════════
    // GET PROFILE
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return convertToResponse(student, user);
    }

    // ═══════════════════════════════════════════════════════════
    // UPDATE PROFILE
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional
    public StudentResponse updateStudentProfile(Long userId, StudentProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create student profile if it doesn't exist yet
        Student student = studentRepository.findByUser(user).orElseGet(() -> {
            Student s = new Student();
            s.setUser(user);
            return s;
        });

        student.setDepartment(request.getDepartment());
        student.setCgpa(request.getCgpa());
        student.setGraduationYear(request.getGraduationYear());
        student.setSkills(request.getSkills());

        Student saved = studentRepository.save(student);
        return convertToResponse(saved, user);
    }

    // ═══════════════════════════════════════════════════════════
    // GET ELIGIBLE JOB DRIVES
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponse> getEligibleJobDrives(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please complete your profile."));

        if (student.getCgpa() == null) {
            throw new BadRequestException("Please set your CGPA in your profile first");
        }

        List<JobDrive> eligibleDrives = jobDriveRepository.findEligibleJobDrives(
                student.getCgpa(),
                LocalDate.now()
        );

        return eligibleDrives.stream()
                .map(this::convertToJobDriveResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════
    // APPLY FOR JOB
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional
    public void applyForJob(Long userId, Long jobDriveId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Complete your profile before applying"));

        JobDrive jobDrive = jobDriveRepository.findById(jobDriveId)
                .orElseThrow(() -> new ResourceNotFoundException("Job drive not found"));

        if (jobDrive.getDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Application deadline has passed");
        }

        if (student.getCgpa() == null) {
            throw new BadRequestException("Please set your CGPA before applying");
        }

        if (student.getCgpa() < jobDrive.getRequiredCgpa()) {
            throw new BadRequestException("You are not eligible. Required CGPA: "
                    + jobDrive.getRequiredCgpa() + ", Your CGPA: " + student.getCgpa());
        }

        if (applicationRepository.existsByStudentAndJobDrive(student, jobDrive)) {
            throw new BadRequestException("You have already applied for this job");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJobDrive(jobDrive);
        application.setStatus(ApplicationStatus.APPLIED);

        applicationRepository.save(application);
    }

    // ═══════════════════════════════════════════════════════════
    // GET MY APPLICATIONS ✅ @Transactional
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<Application> applications = applicationRepository.findRecentApplicationsByStudent(student);

        return applications.stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════
    // GET APPLICATION STATUS ✅ @Transactional
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationStatus(Long userId, Long applicationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getStudent().getId().equals(student.getId())) {
            throw new BadRequestException("This application does not belong to you");
        }

        return convertToApplicationResponse(application);
    }

    // ═══════════════════════════════════════════════════════════
    // CONVERTERS
    // ═══════════════════════════════════════════════════════════
    private StudentResponse convertToResponse(Student student, User user) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setDepartment(student.getDepartment());
        response.setCgpa(student.getCgpa());
        response.setGraduationYear(student.getGraduationYear());
        response.setSkills(student.getSkills());
        response.setCreatedAt(student.getCreatedAt());
        response.setUpdatedAt(student.getUpdatedAt());
        return response;
    }

    private JobDriveResponse convertToJobDriveResponse(JobDrive jobDrive) {
        JobDriveResponse response = new JobDriveResponse();
        response.setId(jobDrive.getId());
        response.setCompanyId(jobDrive.getCompany().getId());
        response.setCompanyName(jobDrive.getCompany().getCompanyName());
        response.setJobRole(jobDrive.getJobRole());
        response.setPackageAmount(jobDrive.getPackageAmount());
        response.setRequiredCgpa(jobDrive.getRequiredCgpa());
        response.setRequiredSkills(jobDrive.getRequiredSkills());
        response.setDeadline(jobDrive.getDeadline());
        response.setLocation(jobDrive.getLocation());
        response.setCreatedAt(jobDrive.getCreatedAt());
        response.setUpdatedAt(jobDrive.getUpdatedAt());
        Long count = applicationRepository.countByJobDrive(jobDrive);
        response.setApplicationCount(count);
        return response;
    }

    private ApplicationResponse convertToApplicationResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setStudentId(application.getStudent().getId());
        response.setStudentName(application.getStudent().getUser().getName());
        response.setJobDriveId(application.getJobDrive().getId());
        response.setJobRole(application.getJobDrive().getJobRole());
        response.setCompanyName(application.getJobDrive().getCompany().getCompanyName());
        response.setStatus(application.getStatus().name());
        response.setAppliedDate(application.getAppliedDate());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        return response;
    }
}