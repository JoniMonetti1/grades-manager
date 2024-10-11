package com.jonim.grades_manager.repository;

import static org.assertj.core.api.Assertions.*;

import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.repositories.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

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
}
