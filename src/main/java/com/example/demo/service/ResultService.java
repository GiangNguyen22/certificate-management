package com.example.demo.service;

import com.example.demo.dto.ResultDTO;
import com.example.demo.dto.request.ResultRequest;
import com.example.demo.entity.Course;
import com.example.demo.entity.Result;
import com.example.demo.entity.ResultId;
import com.example.demo.entity.Student;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ResultRepository;
import com.example.demo.repository.StudentRepositoryI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResultService {
    private final StudentRepositoryI studentRepositoryI;
    private final CourseRepository courseRepository;
    private final ResultRepository resultRepository;

    public ResultService(StudentRepositoryI studentRepositoryI, CourseRepository courseRepository,
            ResultRepository resultRepository) {
        this.studentRepositoryI = studentRepositoryI;
        this.courseRepository = courseRepository;
        this.resultRepository = resultRepository;
    }

    public Page<ResultDTO> getResults(Pageable pageable) {
        return resultRepository.getResultList(pageable);
    }

    public List<Result> getAllResults() {
        return (List<Result>) resultRepository.findAll();
    }

    public Result createResult(ResultRequest resultRequest) {
        if (resultRequest.getStudentCode() == null || resultRequest.getCourseCode() == null) {
            throw new IllegalArgumentException("Student code and Course code must not be null");
        }

        Student student = studentRepositoryI.findByStudentCode(resultRequest.getStudentCode()).orElseThrow(
                () -> new ResourceNotFoundEx("Student not found with code: " + resultRequest.getStudentCode()));
        Course course = courseRepository.findByCourseCode(resultRequest.getCourseCode()).orElseThrow(
                () -> new ResourceNotFoundEx("Course not found with code: " + resultRequest.getCourseCode()));

        if (resultRepository.existsByCourseCodeAndStudentCode(resultRequest.getCourseCode(),
                resultRequest.getStudentCode())) {
            throw new IllegalArgumentException("Course code and student code already exists");
        }

        try {
            Result result = new Result();
            result.setStudent(student);
            result.setCourse(course);
            result.setScore(resultRequest.getScore());
            result.setGrade(resultRequest.getGrade());
            result.setSemester(resultRequest.getSemester());
            result.setTimeStudied(resultRequest.getTimeStudied());
            return resultRepository.save(result);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating Result: " + e.getMessage());

        }

    }

    public Result updateResult(Long resultId, ResultRequest resultRequest) {
        Result existingResult = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundEx("Result not found with id: " + resultId));

        if (resultRequest.getScore() != null) {
            existingResult.setScore(resultRequest.getScore());
        }
        if (resultRequest.getGrade() != null) {
            existingResult.setGrade(resultRequest.getGrade());
        }
        if (resultRequest.getSemester() != null) {
            existingResult.setSemester(resultRequest.getSemester());
        }

        return resultRepository.save(existingResult);
    }

    public void deleteResult(Long resultId) {
        Result existingResult = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundEx("Result not found with id: " + resultId));
        resultRepository.delete(existingResult);
    }

    // public List<ResultDTO> getResultsByStudentCode(String studentCode) {
    //     return resultRepository.findByStudentStudentCode(studentCode)
    //             .stream()
    //             .map(this::convertToDTO)
    //             .collect(Collectors.toList());
    // }

     public List<ResultDTO> getCoursesByStudentCode(String studentCode) {
        return resultRepository.findByStudentStudentCode(studentCode);
    }
    public List<ResultDTO> getStudentsByCourseCode(String courseCode) {
    return resultRepository.findByCourseCourseCode(courseCode);
}
}