package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Professor;
import com.jonim.grades_manager.models.ProfessorDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProfessorService {

    ResponseEntity<ProfessorDTO> getProfessorById(Integer id);

    ResponseEntity<List<Professor>> getProfessorList();

    ResponseEntity<Professor> saveProfessor(Professor professor);

    ResponseEntity<Professor> modifyProfessor(Integer id, Professor professor);

    ResponseEntity<Void> deleteProfessorById(Integer id);

    ResponseEntity<Void> assignSubjectToProfessor(Integer professorId, Integer subjectId);
}
