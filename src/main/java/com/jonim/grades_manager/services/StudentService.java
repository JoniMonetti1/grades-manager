package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.GradeRequest;
import com.jonim.grades_manager.models.Student;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StudentService {
    ResponseEntity<List<Student>> getStudentList();
    ResponseEntity<Student> getStudentById(Integer id);
    ResponseEntity<Student> saveStudent(Student student);
    ResponseEntity<Student> modifyStudent(Integer id, Student student);
    ResponseEntity<Void> deleteStudentById(Integer id);
    ResponseEntity<Void> assignSubjectToStudent(Integer studentId, Integer subjectId);
    ResponseEntity<String> assignGradeToStudent(Integer studentId, Integer subjectId, GradeRequest gradeRequest);
}
