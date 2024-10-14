package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Professor;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProfessorService {

    ResponseEntity<Professor> getProfessorById(Integer id);
    ResponseEntity<List<Professor>> getProfessorList();
    ResponseEntity<Professor> saveProfessor(Professor professor);
    ResponseEntity<Professor> modifyProfessor(Integer id, Professor professor);
    ResponseEntity<Void> deleteProfessorById(Integer id);
}
