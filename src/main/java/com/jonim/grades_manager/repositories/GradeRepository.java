package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    @Query("SELECT g FROM Grade g WHERE g.student.studentId = :studentId AND g.subject.id = :subjectId")
    List<Grade> findGradesByStudentIdAndSubjectId(@Param("studentId") Integer studentId, @Param("subjectId") Integer subjectId);
}
