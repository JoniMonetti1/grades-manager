package com.jonim.grades_manager.repositories;

import com.jonim.grades_manager.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
