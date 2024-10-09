package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
