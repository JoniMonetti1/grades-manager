package com.jonim.grades_manager.controllers;

import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Implement CRUD operations for Students
    @GetMapping
    @CrossOrigin
    private ResponseEntity<List<Student>> getStudentList() {
        return studentService.getStudentList();
    }

    @GetMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        return studentService.getStudentById(id);
    }


    @PostMapping
    @CrossOrigin
    private ResponseEntity<Student> saveStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @PutMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<Student> modifyStudent(@PathVariable Integer id, @RequestBody Student student) {
        return studentService.modifyStudent(id, student);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin
    private ResponseEntity<Void> deleteStudentById(@PathVariable Integer id) {
        return studentService.deleteStudentById(id);
    }
}
