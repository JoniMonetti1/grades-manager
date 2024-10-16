package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.StudentRepository;
import com.jonim.grades_manager.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;
    private SubjectRepository subjectRepository;


    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public ResponseEntity<List<Student>> getStudentList() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(students);
    }

    @Override
    public ResponseEntity<Student> getStudentById(Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        return optionalStudent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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
    public ResponseEntity<Void> assignSubjectToStudent(Integer studentId, Integer subjectId) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

        if (optionalStudent.isPresent() && optionalSubject.isPresent()) {
            Student student = optionalStudent.get();
            Subject subject = optionalSubject.get();
            student.getSubjects().add(subject);
            subject.getStudents().add(student);
            studentRepository.save(student);
            subjectRepository.save(subject);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
