package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    @Query("SELECT s FROM Subject s JOIN s.grades g WHERE g.student.studentId = :studentId AND s.id = :subjectId")
    List<Subject> findSubjectWithGradesByStudentIdAndSubjectId(@Param("studentId") Integer studentId, @Param("subjectId") Integer subjectId);

    @Query("SELECT s FROM Subject s JOIN s.grades g WHERE g.student.studentId = :studentId AND s.id = :subjectId")
    Optional<Subject> findSubjectByIdAndStudentId(@Param("subjectId") Integer subjectId, @Param("studentId") Integer studentId);

}
