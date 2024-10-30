package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.GradeRequest;
import com.jonim.grades_manager.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface StudentService {
    ResponseEntity<Page<Student>> getStudentList(int page, int size);

    ResponseEntity<Student> getStudentById(Integer id);

    ResponseEntity<Student> saveStudent(Student student);

    ResponseEntity<Student> modifyStudent(Integer id, Student student);

    ResponseEntity<Void> deleteStudentById(Integer id);

    ResponseEntity<String> assignSubjectToStudent(Integer studentId, Integer subjectId);

    ResponseEntity<String> assignGradeToStudent(Integer studentId, Integer subjectId, GradeRequest gradeRequest);
}
