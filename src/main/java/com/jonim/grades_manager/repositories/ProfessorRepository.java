package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
}
