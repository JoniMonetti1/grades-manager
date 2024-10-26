package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Subject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubjectService {
    ResponseEntity<List<Subject>> getSubjectList();

    ResponseEntity<Subject> getSubjectById(Integer id);

    ResponseEntity<Subject> saveSubject(Subject subject);

    ResponseEntity<Subject> modifySubject(Integer id, Subject subject);

    ResponseEntity<Void> deleteSubjectById(Integer id);

    ResponseEntity<Subject> findSubjectByIdAndStudentId(Integer id, Integer studentId);
}
