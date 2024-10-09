package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
