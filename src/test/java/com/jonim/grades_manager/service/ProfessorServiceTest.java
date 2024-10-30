package com.jonim.grades_manager.service;

import com.jonim.grades_manager.models.Professor;
import com.jonim.grades_manager.models.ProfessorCreateUpdateDTO;
import com.jonim.grades_manager.models.ProfessorDTO;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.ProfessorRepository;
import com.jonim.grades_manager.repositories.SubjectRepository;
import com.jonim.grades_manager.services.ProfessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private ProfessorServiceImpl professorService;

    private ProfessorCreateUpdateDTO professor0;
    private Professor expectedProfessor;
    private Subject expectedSubject;

    @BeforeEach
    public void setUp() {
        professor0 = new ProfessorCreateUpdateDTO();
        professor0.setName("Joni");
        professor0.setSurname("Monetti");
        professor0.setSubjectId(1);

        expectedSubject = new Subject();
        expectedSubject.setId(1);
        expectedSubject.setName("Mathematics");

        expectedProfessor = new Professor();
        expectedProfessor.setName(professor0.getName());
        expectedProfessor.setSurname(professor0.getSurname());
        expectedProfessor.setSubject(expectedSubject);
        expectedProfessor.setId(1);
    }

    @Test
    public void shouldSaveProfessor() {
        //Configuramos una solicitud simulada
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        //given
        given(subjectRepository.findById(1)).willReturn(Optional.of(expectedSubject));
        given(professorRepository.save(any(Professor.class))).willReturn(expectedProfessor);

        //when
        ResponseEntity<ProfessorDTO> saveProfessor = professorService.saveProfessor(professor0);

        //then
        assertThat(saveProfessor.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(saveProfessor.getBody()).isNotNull();
        assertThat(saveProfessor.getBody().getName()).isEqualTo("Joni");
        assertThat(saveProfessor.getBody().getSurname()).isEqualTo("Monetti");
    }

    @Test
    public void shouldReturnAllProfessors() {
        //given
        ProfessorCreateUpdateDTO professor1 = new ProfessorCreateUpdateDTO();
        professor1.setName("Abel");
        professor1.setSurname("Monetti");
        professor1.setSubjectId(2);

        Subject expectedSubject1 = new Subject();
        expectedSubject1.setId(2);
        expectedSubject1.setName("Sciences");

        Professor expectedProfessor1 = new Professor();
        expectedProfessor1.setName(professor1.getName());
        expectedProfessor1.setSurname(professor1.getSurname());
        expectedProfessor1.setSubject(expectedSubject1);
        expectedProfessor1.setId(2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Professor> page = new PageImpl<>(List.of(expectedProfessor, expectedProfessor1), pageable, 2);

        given(professorRepository.findAll(pageable)).willReturn(page);

        //when
        ResponseEntity<Page<ProfessorDTO>> professors = professorService.getProfessorList(0, 10);

        //then
        assertThat(professors.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(professors.getBody()).hasSize(2);
        assertThat(professors.getBody().getContent()).extracting(ProfessorDTO::getName)
                .containsExactlyInAnyOrder("Joni", "Abel");
    }

    @Test
    public void shouldReturnAEmptyPage() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        // Mockea el método findAll para devolver un Page vacío
        given(professorRepository.findAll(pageable)).willReturn(Page.empty(pageable));

        //when
        ResponseEntity<Page<ProfessorDTO>> professorPage = professorService.getProfessorList(0, 10);

        //then
        assertThat(professorPage.getBody()).isEmpty();
        assertThat(professorPage.getBody().getContent()).hasSize(0);
    }

    @Test
    public void shouldUpdateProfessor() {
        //given
        given(professorRepository.findById(expectedProfessor.getId())).willReturn(Optional.of(expectedProfessor));
        given(subjectRepository.findById(1)).willReturn(Optional.of(expectedSubject));

        expectedProfessor.setName("Jonathan");
        given(professorRepository.save(any(Professor.class))).willReturn(expectedProfessor);


        //when
        ResponseEntity<ProfessorDTO> updatedProfessor = professorService.modifyProfessor(expectedProfessor.getId(), professor0);

        //then
        assertThat(updatedProfessor.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedProfessor.getBody().getName()).isEqualTo("Jonathan");
    }

    @Test
    public void shouldDeleteSubject() {
        //given
        Integer professorId = expectedProfessor.getId();
        given(professorRepository.findById(professorId)).willReturn(Optional.of(expectedProfessor));
        willDoNothing().given(professorRepository).deleteById(professorId);

        //when
        professorService.deleteProfessorById(professorId);

        //then
        verify(professorRepository, times(1)).deleteById(professorId);
    }

    @Test
    public void shouldAssignSubjectToProfessor() {
        // given
        Integer professorId = 1;
        Integer subjectId = 1;

        Professor professor = new Professor();
        professor.setId(professorId);
        professor.setName("John");
        professor.setSurname("Doe");

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setName("Mathematics");

        given(professorRepository.findById(professorId)).willReturn(Optional.of(professor));
        given(subjectRepository.findById(subjectId)).willReturn(Optional.of(subject));

        // when
        ResponseEntity<String> response = professorService.assignSubjectToProfessor(professorId, subjectId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(professorRepository).findById(professorId);
        verify(subjectRepository).findById(subjectId);
        verify(professorRepository).save(professor);

        ArgumentCaptor<Professor> professorCaptor = ArgumentCaptor.forClass(Professor.class);
        verify(professorRepository).save(professorCaptor.capture());
        Professor savedProfessor = professorCaptor.getValue();
        assertEquals(subject, savedProfessor.getSubject());
    }

    @Test
    public void shouldReturnNotFoundWhenProfessorDoesNotExist() {
        // given
        Integer professorId = 1;
        Integer subjectId = 1;

        Subject subject = new Subject();
        subject.setId(subjectId);

        given(professorRepository.findById(professorId)).willReturn(Optional.empty());
        given(subjectRepository.findById(subjectId)).willReturn(Optional.of(subject));

        // when
        ResponseEntity<String> response = professorService.assignSubjectToProfessor(professorId, subjectId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(professorRepository).findById(professorId);
        verify(professorRepository, never()).save(any());
    }

    @Test
    public void shouldReturnNotFoundWhenSubjectDoesNotExist() {
        // given
        Integer professorId = 1;
        Integer subjectId = 1;

        Professor professor = new Professor();
        professor.setId(professorId);

        given(professorRepository.findById(professorId)).willReturn(Optional.of(professor));
        given(subjectRepository.findById(subjectId)).willReturn(Optional.empty());

        // when
        ResponseEntity<String> response = professorService.assignSubjectToProfessor(professorId, subjectId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(professorRepository).findById(professorId);
        verify(subjectRepository).findById(subjectId);
        verify(professorRepository, never()).save(any());
    }
}
