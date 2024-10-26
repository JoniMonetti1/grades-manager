package com.jonim.grades_manager.controllers;

import com.jonim.grades_manager.models.Professor;
import com.jonim.grades_manager.models.ProfessorDTO;
import com.jonim.grades_manager.services.ProfessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professors")
public class ProfessorController {

    private ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }


    @GetMapping
    @CrossOrigin
    private ResponseEntity<List<Professor>> getProfessorList() {
        return professorService.getProfessorList();
    }

    @GetMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<ProfessorDTO> getProfessorById(@PathVariable Integer id) {
        return professorService.getProfessorById(id);
    }

    @PostMapping
    @CrossOrigin
    private ResponseEntity<Professor> saveProfessor(@RequestBody Professor professor) {
        return professorService.saveProfessor(professor);
    }

    @PutMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<Professor> modifyProfessor(@PathVariable Integer id, @RequestBody Professor professor) {
        return professorService.modifyProfessor(id, professor);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<Void> deleteProfessorById(@PathVariable Integer id) {
        return professorService.deleteProfessorById(id);
    }

    @PutMapping("/{id}/subjects/{subjectId}")
    @CrossOrigin
    private ResponseEntity<Void> assignSubjectToProfessor(@PathVariable Integer id, @PathVariable Integer subjectId) {
        return professorService.assignSubjectToProfessor(id, subjectId);
    }
}
