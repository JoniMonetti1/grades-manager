package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Grade;
import com.jonim.grades_manager.models.GradeRequest;
import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.GradeRepository;
import com.jonim.grades_manager.repositories.StudentRepository;
import com.jonim.grades_manager.repositories.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;
    private SubjectRepository subjectRepository;
    private GradeRepository gradeRepository;


    public StudentServiceImpl(StudentRepository studentRepository, SubjectRepository subjectRepository, GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public ResponseEntity<Page<Student>> getStudentList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentsPage = studentRepository.findAll(pageable);
        return ResponseEntity.ok(studentsPage);
    }

    @Override
    public ResponseEntity<Student> getStudentById(Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            // Cargar las notas específicas del estudiante para cada materia
            student.getSubjects().forEach(subject -> {
                List<Grade> grades = gradeRepository.findGradesByStudentIdAndSubjectId(id, subject.getId());
                subject.setGrades(new HashSet<>(grades)); // Asigna solo las notas específicas del estudiante
            });

            return ResponseEntity.ok(student);
        }

        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Student> saveStudent(Student student) {
        Student savedStudent = studentRepository.save(student);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudent.getStudentId())
                .toUri();

        return ResponseEntity.created(location).body(savedStudent);
    }

    @Override
    public ResponseEntity<Student> modifyStudent(Integer id, Student student) {
        Optional<Student> studentToModify = studentRepository.findById(id);

        if (studentToModify.isPresent()) {
            student.setStudentId(id);
            Student modifiedStudent = studentRepository.save(student);
            return ResponseEntity.ok(modifiedStudent);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteStudentById(Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            studentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<String> assignSubjectToStudent(Integer studentId, Integer subjectId) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

        if (optionalStudent.isPresent() && optionalSubject.isPresent()) {
            Student student = optionalStudent.get();
            Subject subject = optionalSubject.get();
            student.getSubjects().add(subject);
            studentRepository.save(student);
            return ResponseEntity.ok("Student subject assigned");
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<String> assignGradeToStudent(Integer studentId, Integer subjectId, GradeRequest gradeRequest) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found");
        }

        Optional<Subject> optionalSubject = subjectRepository.findSubjectByIdAndStudentId(subjectId, studentId);
        if (optionalSubject.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The subject was not found for the specified student.");
        }

        Grade grade = Grade.builder()
                .grade(gradeRequest.getGrade())
                .student(optionalStudent.get())
                .subject(optionalSubject.get())
                .build();
        gradeRepository.save(grade);
        return ResponseEntity.ok("Student grade assigned");
    }
}
