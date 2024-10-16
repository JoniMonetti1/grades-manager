package com.jonim.grades_manager.repository;

import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.SubjectRepository;
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
public class SubjectRepositoryTest {

    private SubjectRepository subjectRepository;
    private Subject subject0;

    @Autowired
    public SubjectRepositoryTest(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @BeforeEach
    public void setUp() {
        subject0 = Subject.builder()
                .name("Maths")
                .build();
    }

    @Test
    public void shouldReturnSubjectList() {
        //given
        Subject subject1 = Subject.builder()
                .name("Science")
                .build();

        subjectRepository.save(subject0);
        subjectRepository.save(subject1);

        //when
        List<Subject> subjectList = subjectRepository.findAll();

        //then
        assertThat(subjectList).isNotNull();
        assertThat(subjectList).hasSize(2);
        assertThat(subjectList).extracting(Subject::getName).containsExactlyInAnyOrder("Maths", "Science");
    }

    @Test
    public void shouldReturnASubjectById() {
        //given
        subjectRepository.save(subject0);

        //when
        Subject subject = subjectRepository.findById(subject0.getId()).get();

        //then
        assertThat(subject).isNotNull();
        assertThat(subject.getName()).isEqualTo(subject0.getName());
    }

    @Test
    public void shouldSaveASubject() {
        //when
        subjectRepository.save(subject0);

        Subject savedSubject = subjectRepository.findById(subject0.getId()).get();

        //then
        assertThat(savedSubject).isNotNull();
        assertThat(savedSubject.getName()).isEqualTo(subject0.getName());
        assertThat(savedSubject.getId()).isGreaterThan(0);
    }

    @Test
    public void shouldUpdateASubject() {
        //given
        subjectRepository.save(subject0);

        //when
        Subject savedSubject = subjectRepository.findById(subject0.getId()).get();
        savedSubject.setName("chemistry");
        Subject updatedSubject = subjectRepository.save(savedSubject);

        //then
        assertThat(updatedSubject).isNotNull();
        assertThat(updatedSubject.getName()).isEqualTo("chemistry");
        assertThat(updatedSubject.getId()).isGreaterThan(0);
    }

    @Test
    public void shouldDeleteASubjectById() {
        //given
        subjectRepository.save(subject0);

        //when
        subjectRepository.deleteById(subject0.getId());
        Optional<Subject> optionalSubject = subjectRepository.findById(subject0.getId());

        //then
        assertThat(optionalSubject).isEmpty();
    }
}
