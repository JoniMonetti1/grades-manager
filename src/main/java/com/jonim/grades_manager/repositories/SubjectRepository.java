package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    @Query("SELECT s FROM Subject s JOIN s.students st WHERE s.id = :subjectId AND st.studentId = :studentId")
    Optional<Subject> findSubjectByIdAndStudentId(@Param("subjectId") Integer subjectId, @Param("studentId") Integer studentId);
}
