package com.jonim.grades_manager.repository;

import com.jonim.grades_manager.models.Professor;
import com.jonim.grades_manager.repositories.ProfessorRepository;
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
public class ProfessorRepositoryTest {

    private final ProfessorRepository professorRepository;
    private Professor professor1;

    @Autowired
    public ProfessorRepositoryTest(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @BeforeEach
    public void setUp() {
        professor1 = Professor.builder()
                .name("Joni")
                .surname("Monetti")
                .build();
    }

    @Test
    public void shouldReturnAProfessorById() {
        //given
        professorRepository.save(professor1);

        //when
        Professor professor = professorRepository.findById(professor1.getId()).get();

        //then
        assertThat(professor).isNotNull();
        assertThat(professor.getName()).isEqualTo(professor1.getName());
        assertThat(professor.getSurname()).isEqualTo(professor1.getSurname());
    }

    @Test
    public void shouldReturnProfessorList() {
        //given
        Professor professor2 = Professor.builder()
                .name("Abel")
                .surname("Monetti")
                .build();

        //when
        professorRepository.save(professor1);
        professorRepository.save(professor2);

        List<Professor> professorList = professorRepository.findAll();

        //then
        assertThat(professorList).isNotNull();
        assertThat(professorList).hasSize(2);
        assertThat(professorList).extracting(Professor::getName)
                .containsExactlyInAnyOrder("Joni", "Abel");
    }

    @Test
    public void shouldSaveAProfessor() {
        //when
        professorRepository.save(professor1);

        Professor savedProfessor = professorRepository.findById(professor1.getId()).get();

        //then
        assertThat(savedProfessor).isNotNull();
        assertThat(savedProfessor.getName()).isEqualTo(professor1.getName());
        assertThat(savedProfessor.getSurname()).isEqualTo(professor1.getSurname());
        assertThat(savedProfessor.getId()).isGreaterThan(0);
    }

    @Test
    public void shouldUpdateAProfessor() {
        //given
        professorRepository.save(professor1);

        //when
        Professor savedProfessor = professorRepository.findById(professor1.getId()).get();
        savedProfessor.setName("Jonathan");
        savedProfessor.setSurname("Monettii");
        Professor updatedProfessor = professorRepository.save(savedProfessor);

        //then
        assertThat(updatedProfessor).isNotNull();
        assertThat(updatedProfessor.getName()).isEqualTo("Jonathan");
        assertThat(updatedProfessor.getSurname()).isEqualTo("Monettii");
    }

    @Test
    public void shouldDeleteAProfessorById() {
        //given
        professorRepository.save(professor1);

        //when
        professorRepository.deleteById(professor1.getId());
        Optional<Professor> optionalProfessor = professorRepository.findById(professor1.getId());

        //then
        assertThat(optionalProfessor).isEmpty();
    }

}
