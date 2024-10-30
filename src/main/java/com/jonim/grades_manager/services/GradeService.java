package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Grade;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface GradeService {
    ResponseEntity<Page<Grade>> getGradesList(int page, int size);

    ResponseEntity<Grade> getGradeById(Integer id);

    ResponseEntity<Grade> saveGrade(Grade grade);

    ResponseEntity<Grade> modifyGrade(Integer id, Grade grade);

    ResponseEntity<Void> deleteGradeById(Integer id);
}
