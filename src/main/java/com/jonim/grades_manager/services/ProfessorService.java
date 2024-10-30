package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.ProfessorCreateUpdateDTO;
import com.jonim.grades_manager.models.ProfessorDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ProfessorService {

    ResponseEntity<ProfessorDTO> getProfessorById(Integer id);

    ResponseEntity<Page<ProfessorDTO>> getProfessorList(int page, int size);

    ResponseEntity<ProfessorDTO> saveProfessor(ProfessorCreateUpdateDTO createDTO);

    ResponseEntity<ProfessorDTO> modifyProfessor(Integer id, ProfessorCreateUpdateDTO updateDTO);

    ResponseEntity<Void> deleteProfessorById(Integer id);

    ResponseEntity<String> assignSubjectToProfessor(Integer professorId, Integer subjectId);
}
