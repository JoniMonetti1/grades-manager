package com.jonim.grades_manager.service;

import com.jonim.grades_manager.exceptions.ResourceNotFoundException;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.SubjectRepository;
import com.jonim.grades_manager.services.SubjectServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubjectServiceTest {

    @Mock //simulacro de subject repository
    private SubjectRepository subjectRepository;

    @InjectMocks //dentro del service inyecta el mock
    private SubjectServiceImpl subjectService;

    private Subject subject0;

    @BeforeEach
    public void setUp() {
        subject0 = Subject.builder()
                .id(1)
                .name("Maths")
                .build();
    }

    @Test
    public void shouldSaveSubject() {
        // Configuramos una solicitud simulada
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        //given
        given(subjectRepository.save(subject0)).willReturn(subject0);

        //when
        ResponseEntity<Subject> savedSubject = subjectService.saveSubject(subject0);

        //then
        assertThat(savedSubject.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(savedSubject).isNotNull();
    }

    @Test
    public void shouldNotSaveThrowException() {
        //given: Simulamos que el sujeto ya existe en la base de datos
        given(subjectRepository.findById(subject0.getId()))
                .willReturn(Optional.of(subject0));

        //when: Esperamos que el método falle al intentar guardar un `subject` duplicado
        assertThrows(ResourceNotFoundException.class, () ->
                subjectService.saveSubject(subject0));

        //then: Verificamos que nunca se haya intentado guardar
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    public void shouldReturnAllSubjects() {
        //given
        Subject subject1 = Subject.builder()
                .name("Science")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Subject> page = new PageImpl<>(List.of(subject0, subject1), pageable, 2);

        given(subjectRepository.findAll(pageable)).willReturn(page);

        //when
        ResponseEntity<Page<Subject>> subjects = subjectService.getSubjectList(0, 10);

        //then
        assertThat(subjects.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(subjects.getBody()).hasSize(2);
        assertThat(subjects.getBody().getContent()).extracting(Subject::getName)
                .containsExactlyInAnyOrder("Maths", "Science");
    }

    @Test
    public void shouldReturnAEmptyPage() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        // Mockea el método findAll para devolver un Page vacío
        given(subjectRepository.findAll(pageable)).willReturn(Page.empty(pageable));

        //when
        ResponseEntity<Page<Subject>> subjectPage = subjectService.getSubjectList(0, 10);

        //then
        assertThat(subjectPage.getBody()).isEmpty();
        assertThat(subjectPage.getBody().getContent()).hasSize(0);
    }

    @Test
    public void shouldReturnASubjectById() {
        //given
        given(subjectRepository.findById(subject0.getId())).willReturn(Optional.of(subject0));

        //when
        ResponseEntity<Subject> subject = subjectService.getSubjectById(subject0.getId());

        //then
        assertThat(subject.getBody()).isNotNull();
        assertThat(subject.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(subject.getBody()).isEqualTo(subject0);
    }

    @Test
    public void shouldUpdateSubject() {
        //given
        given(subjectRepository.findById(subject0.getId())).willReturn(Optional.of(subject0));
        given(subjectRepository.save(subject0)).willReturn(subject0);
        subject0.setName("Physics");

        //when
        ResponseEntity<Subject> updatedSubject = subjectService.modifySubject(subject0.getId(), subject0);

        //then
        assertThat(updatedSubject.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedSubject.getBody().getName()).isEqualTo("Physics");
    }


    @Test
    public void shouldDeleteSubject() {
        //given
        Integer subjectId = subject0.getId();
        given(subjectRepository.findById(subjectId)).willReturn(Optional.of(subject0));
        willDoNothing().given(subjectRepository).deleteById(subjectId);

        //when
        subjectService.deleteSubjectById(subjectId);

        //then
        verify(subjectRepository, times(1)).deleteById(subjectId);
    }

    @Test
    public void shouldReturnASubjectByIdAndStudentId() {
        //given
        Integer subjectId = 1;
        Integer studentId = 1;

        given(subjectRepository.findSubjectByIdAndStudentId(subjectId, studentId))
                .willReturn(Optional.of(subject0));

        //when
        ResponseEntity<Subject> response = subjectService.findSubjectByIdAndStudentId(subjectId, studentId);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(subject0);
    }


    @Test
    public void shouldNotFoundWhenSubjectDoesNotExistForStudent() {
        //given
        Integer subjectId = 1;
        Integer studentId = 1;

        given(subjectRepository.findSubjectByIdAndStudentId(subjectId, studentId))
                .willReturn(Optional.empty());

        //when
        ResponseEntity<Subject> response = subjectService.findSubjectByIdAndStudentId(subjectId, studentId);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
