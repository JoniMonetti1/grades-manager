package com.jonim.grades_manager.service;

import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.SubjectRepository;
import com.jonim.grades_manager.services.SubjectService;
import com.jonim.grades_manager.services.SubjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

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
}
