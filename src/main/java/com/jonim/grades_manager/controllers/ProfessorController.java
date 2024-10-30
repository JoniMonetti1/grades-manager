package com.jonim.grades_manager.controllers;

import com.jonim.grades_manager.models.ProfessorCreateUpdateDTO;
import com.jonim.grades_manager.models.ProfessorDTO;
import com.jonim.grades_manager.services.ProfessorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professors")
public class ProfessorController {

    private ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }


    @GetMapping
    @CrossOrigin
    private ResponseEntity<Page<ProfessorDTO>> getProfessorList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return professorService.getProfessorList(page, size);
    }

    @GetMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<ProfessorDTO> getProfessorById(@PathVariable Integer id) {
        return professorService.getProfessorById(id);
    }

    @PostMapping
    @CrossOrigin
    private ResponseEntity<ProfessorDTO> saveProfessor(@RequestBody ProfessorCreateUpdateDTO createDTO) {
        return professorService.saveProfessor(createDTO);
    }

    @PutMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<ProfessorDTO> modifyProfessor(@PathVariable Integer id, @RequestBody ProfessorCreateUpdateDTO updateDTO) {
        return professorService.modifyProfessor(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<Void> deleteProfessorById(@PathVariable Integer id) {
        return professorService.deleteProfessorById(id);
    }

    @PutMapping("/{id}/subjects/{subjectId}")
    @CrossOrigin
    private ResponseEntity<String> assignSubjectToProfessor(@PathVariable Integer id, @PathVariable Integer subjectId) {
        return professorService.assignSubjectToProfessor(id, subjectId);
    }
}
