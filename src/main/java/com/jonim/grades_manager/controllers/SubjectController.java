package com.jonim.grades_manager.controllers;

import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.services.SubjectService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    @CrossOrigin
    ResponseEntity<Page<Subject>> getSubjectList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return subjectService.getSubjectList(page, size);
    }

    @GetMapping("/{id}")
    @CrossOrigin
    ResponseEntity<Subject> getSubjectById(@PathVariable Integer id) {
        return subjectService.getSubjectById(id);
    }

    @PostMapping
    @CrossOrigin
    ResponseEntity<Subject> saveSubject(@RequestBody Subject subject) {
        return subjectService.saveSubject(subject);
    }

    @PutMapping("/{id}")
    @CrossOrigin
    ResponseEntity<Subject> modifySubject(@PathVariable Integer id, @RequestBody Subject subject) {
        return subjectService.modifySubject(id, subject);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin
    ResponseEntity<Void> deleteSubjectById(@PathVariable Integer id) {
        return subjectService.deleteSubjectById(id);
    }

    @GetMapping("/{id}/students/{studentId}")
    @CrossOrigin
    ResponseEntity<Subject> getSubjectByIdAndStudentId(@PathVariable Integer id, @PathVariable Integer studentId) {
        return subjectService.findSubjectByIdAndStudentId(id, studentId);
    }
}
