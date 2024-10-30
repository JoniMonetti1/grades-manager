package com.jonim.grades_manager.repository;

import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class StudentRepositoryTest {

    private final StudentRepository studentRepository;
    private Student student0;

    @Autowired
    public StudentRepositoryTest(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @BeforeEach
    public void setUp() {
        student0 = Student.builder()
                .name("joni")
                .surname("monetti")
                .build();
    }

    @Test
    public void shouldSaveStudent() {
        //Given - dado o condicion previa
        Student student1 = Student.builder()
                .name("Joni")
                .surname("Miller")
                .build();

        //When - accion o comportamiento a probar
        Student savedStudent = studentRepository.save(student1);

        //Then - se espera el resultado esperado
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getStudentId()).isGreaterThan(0);
    }

    @Test
    public void shouldReturnStudentList() {
        Student student1 = Student.builder()
                .name("Joni")
                .surname("Miller")
                .build();

        Student student2 = Student.builder()
                .name("Alice")
                .surname("Johnson")
                .build();

        studentRepository.save(student1);
        studentRepository.save(student2);

        //When - accion o comportamiento a probar
        List<Student> studentList = studentRepository.findAll();

        assertThat(studentList).isNotNull();
        assertThat(studentList.size()).isEqualTo(2);
        assertThat(studentList).extracting(Student::getName)
                .containsExactlyInAnyOrder("Joni", "Alice");
    }

    @Test
    public void shouldReturnAStrudentById() {
        //given
        studentRepository.save(student0);

        //when
        Student student = studentRepository.findById(student0.getStudentId()).get();

        //Then
        assertThat(student).isNotNull();
        assertThat(student.getName()).isEqualTo(student0.getName());
        assertThat(student.getSurname()).isEqualTo(student0.getSurname());
    }

    @Test
    public void shouldUpdateAStudent() {
        //given
        studentRepository.save(student0);

        //when
        Student savedStudent = studentRepository.findById(student0.getStudentId()).get();
        savedStudent.setName("abel");
        savedStudent.setSurname("Monettii");
        Student updatedStudent = studentRepository.save(savedStudent);

        //then
        assertThat(updatedStudent).isNotNull();
        assertThat(updatedStudent.getName()).isEqualTo("abel");
        assertThat(updatedStudent.getSurname()).isEqualTo("Monettii");
    }

    @Test
    public void shouldDeleteAStudentById() {
        //given
        studentRepository.save(student0);

        //when
        studentRepository.deleteById(student0.getStudentId());
        Optional<Student> optionalStudent = studentRepository.findById(student0.getStudentId());

        //then
        assertThat(optionalStudent).isEmpty();
    }
}
