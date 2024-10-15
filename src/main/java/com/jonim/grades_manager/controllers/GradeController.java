package com.jonim.grades_manager.controllers;

import com.jonim.grades_manager.models.Grade;
import com.jonim.grades_manager.services.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grades")
public class GradeController {
    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    @CrossOrigin
    public ResponseEntity<List<Grade>> getGradesList() {
        return gradeService.getGradesList();
    }

    @GetMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<Grade> getGradeById(@PathVariable Integer id) {
        return gradeService.getGradeById(id);
    }

    @PostMapping
    @CrossOrigin
    public ResponseEntity<Grade> saveGrade(@RequestBody Grade grade) {
        return gradeService.saveGrade(grade);
    }

    @PutMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<Grade> modifyGrade(@PathVariable Integer id, @RequestBody Grade grade) {
        return gradeService.modifyGrade(id, grade);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<Void> deleteGradeById(@PathVariable Integer id) {
        return gradeService.deleteGradeById(id);
    }
}
