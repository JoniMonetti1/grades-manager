package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Grade;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GradeService {
    ResponseEntity<List<Grade>> getGradesList();
    ResponseEntity<Grade> getGradeById(Integer id);
    ResponseEntity<Grade> saveGrade(Grade grade);
    ResponseEntity<Grade> modifyGrade(Integer id, Grade grade);
    ResponseEntity<Void> deleteGradeById(Integer id);
}
