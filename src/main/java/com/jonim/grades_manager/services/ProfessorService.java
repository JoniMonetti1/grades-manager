package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.ProfessorCreateUpdateDTO;
import com.jonim.grades_manager.models.ProfessorDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProfessorService {

    ResponseEntity<ProfessorDTO> getProfessorById(Integer id);

    ResponseEntity<List<ProfessorDTO>> getProfessorList();

    ResponseEntity<ProfessorDTO> saveProfessor(ProfessorCreateUpdateDTO createDTO);

    ResponseEntity<ProfessorDTO> modifyProfessor(Integer id, ProfessorCreateUpdateDTO updateDTO);

    ResponseEntity<Void> deleteProfessorById(Integer id);

    ResponseEntity<Void> assignSubjectToProfessor(Integer professorId, Integer subjectId);
}
