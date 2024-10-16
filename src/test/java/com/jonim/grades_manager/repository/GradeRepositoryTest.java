package com.jonim.grades_manager.repository;

import com.jonim.grades_manager.models.Grade;
import com.jonim.grades_manager.repositories.GradeRepository;
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
public class GradeRepositoryTest {
    public GradeRepository gradeRepository;

    private Grade grade0;

    @Autowired
    public GradeRepositoryTest(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @BeforeEach
    public void setUp() {
        grade0 = Grade.builder()
                .grade(8.8)
                .build();
    }

    @Test
    public void shouldReturnGradesList() {
        //given
        Grade grade1 = Grade.builder()
                .grade(9.0)
                .build();

        gradeRepository.save(grade1);
        gradeRepository.save(grade0);

        //when
        List<Grade> gradesList = gradeRepository.findAll();

        //then
        assertThat(gradesList).isNotNull();
        assertThat(gradesList.size()).isEqualTo(2);
        assertThat(gradesList).extracting(Grade::getGrade).containsExactlyInAnyOrder(9.0, 8.8);
    }

    @Test
    public void shouldReturnAGradeById() {
        //given
        gradeRepository.save(grade0);

        //when
        Grade grade = gradeRepository.findById(grade0.getId()).get();

        //then
        assertThat(grade).isNotNull();
        assertThat(grade.getGrade()).isEqualTo(8.8);
    }

    @Test
    public void shouldSaveAGrade() {
        //when
        gradeRepository.save(grade0);
        Grade savedGrade = gradeRepository.findById(grade0.getId()).get();

        //then
        assertThat(savedGrade).isNotNull();
        assertThat(savedGrade.getGrade()).isEqualTo(8.8);
        assertThat(savedGrade.getId()).isGreaterThan(0);
    }

    @Test
    public void shouldUpdateAGrade() {
        //given
        gradeRepository.save(grade0);

        //when
        Grade savedGrade = gradeRepository.findById(grade0.getId()).get();
        savedGrade.setGrade(10.00);
        Grade updatedGrade = gradeRepository.save(savedGrade);

        //then
        assertThat(updatedGrade).isNotNull();
        assertThat(updatedGrade.getGrade()).isEqualTo(10.00);
    }

    public void shouldDeleteAGradeById() {
        //given
        gradeRepository.save(grade0);

        //when
        gradeRepository.deleteById(grade0.getId());
        Optional<Grade> deletedGrade = gradeRepository.findById(grade0.getId());

        //then
        assertThat(deletedGrade).isEmpty();
    }
}
