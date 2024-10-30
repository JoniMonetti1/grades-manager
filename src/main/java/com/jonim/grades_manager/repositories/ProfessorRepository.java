package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    boolean existsByNameAndSurnameAndSubjectId(String name, String surname, Integer subjectId);
}
