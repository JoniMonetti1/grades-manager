package com.jonim.grades_manager.service;

import com.jonim.grades_manager.models.Grade;
import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.GradeRepository;
import com.jonim.grades_manager.repositories.StudentRepository;
import com.jonim.grades_manager.repositories.SubjectRepository;
import com.jonim.grades_manager.services.GradeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GradeServiceTest {
    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private GradeServiceImpl gradeService;

    private Grade grade0;
    private Subject subject0;
    private Student student0;

    @BeforeEach
    public void setUp() {
        subject0 = Subject.builder()
                .name("Science")
                .id(1)
                .build();

        student0 = Student.builder()
                .studentId(1)
                .name("joni")
                .surname("monetti")
                .subjects(Set.of(subject0))
                .build();

        grade0 = Grade.builder()
                .id(1)
                .grade(9.20)
                .student(student0)
                .subject(subject0)
                .build();
    }

    @Test
    public void shouldSaveGrade() {
        //Configuramos una solicitud simulada
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        //given
        given(gradeRepository.save(grade0)).willReturn(grade0);

        //when
        ResponseEntity<Grade> saveGrade = gradeService.saveGrade(grade0);

        //then
        assertThat(saveGrade.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(saveGrade.getBody()).isNotNull();
        assertThat(saveGrade.getBody().getSubject().getName()).isEqualTo("Science");
        assertThat(saveGrade.getBody().getGrade()).isEqualTo(9.20);
    }

    @Test
    public void shouldReturnAllGrades() {
        //given
        Subject subject1 = Subject.builder()
                .name("Physics")
                .id(2)
                .build();

        Student student1 = Student.builder()
                .studentId(2)
                .name("abel")
                .surname("monetti")
                .subjects(Set.of(subject0))
                .build();

        Grade grade1 = Grade.builder()
                .id(2)
                .grade(9.00)
                .student(student1)
                .subject(subject1)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Grade> page = new PageImpl<>(List.of(grade0, grade1), pageable, 2);

        given(gradeRepository.findAll(pageable)).willReturn(page);

        //when
        ResponseEntity<Page<Grade>> grades = gradeService.getGradesList(0, 10);

        //then
        assertThat(grades.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(grades.getBody()).hasSize(2);
        assertThat(grades.getBody().getContent()).extracting(Grade::getGrade)
                .containsExactlyInAnyOrder(9.20, 9.00);
    }

    @Test
    public void shouldReturnAEmptyPage() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        // Mockea el método findAll para devolver un Page vacío
        given(gradeRepository.findAll(pageable)).willReturn(Page.empty(pageable));

        //when
        ResponseEntity<Page<Grade>> gradePage = gradeService.getGradesList(0, 10);

        //then
        assertThat(gradePage.getBody()).isEmpty();
        assertThat(gradePage.getBody().getContent()).hasSize(0);
    }

    @Test
    public void shouldUpdateGrade() {
        //given
        given(gradeRepository.findById(grade0.getId())).willReturn(Optional.of(grade0));
        given(gradeRepository.save(grade0)).willReturn(grade0);
        grade0.setGrade(10.00);

        //when
        ResponseEntity<Grade> updatedGrade = gradeService.modifyGrade(grade0.getId(), grade0);

        //then
        assertThat(updatedGrade.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedGrade.getBody().getGrade()).isEqualTo(10.00);
    }

    @Test
    public void shouldDeleteGrade() {
        //given
        Integer gradeId = grade0.getId();
        given(gradeRepository.findById(gradeId)).willReturn(Optional.of(grade0));
        willDoNothing().given(gradeRepository).deleteById(gradeId);

        //when
        gradeService.deleteGradeById(gradeId);

        //then
        verify(gradeRepository, times(1)).deleteById(gradeId);
    }
}
