package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.*;
import com.jonim.grades_manager.repositories.ProfessorRepository;
import com.jonim.grades_manager.repositories.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfessorServiceImpl implements ProfessorService { //TODO: Adapt this class to ProfessorDTO implementation

    private ProfessorRepository professorRepository;
    private SubjectRepository subjectRepository;

    public ProfessorServiceImpl(ProfessorRepository professorRepository, SubjectRepository subjectRepository) {
        this.professorRepository = professorRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public ResponseEntity<ProfessorDTO> getProfessorById(Integer id) {
        Optional<Professor> professorOptional = professorRepository.findById(id);
        return professorOptional
                .map(professor -> ResponseEntity.ok(new ProfessorDTO(professor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Page<ProfessorDTO>> getProfessorList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Professor> professorPage = professorRepository.findAll(pageable);

        List<ProfessorDTO> professorDTOs = professorPage.stream()
                .map(ProfessorDTO::new)
                .collect(Collectors.toList());

        Page<ProfessorDTO> professorDTOsPage = new PageImpl<>(professorDTOs, pageable, professorPage.getTotalElements());
        return professorDTOs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(professorDTOsPage);
    }

    @Override
    public ResponseEntity<ProfessorDTO> saveProfessor(ProfessorCreateUpdateDTO createDTO) {
        Professor professor = new Professor();
        professor.setName(createDTO.getName());
        professor.setSurname(createDTO.getSurname());

        Subject subject = subjectRepository.findById(createDTO.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        professor.setSubject(subject);

        Professor savedProfessor = professorRepository.save(professor);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProfessor.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ProfessorDTO(savedProfessor));
    }

    @Override
    public ResponseEntity<ProfessorDTO> modifyProfessor(Integer id, ProfessorCreateUpdateDTO updateDTO) { //TODO: This method do not update, its create a new Professor CHECK
        return professorRepository.findById(id)
                .map(professor -> {
                    Subject subject = updateDTO.getSubjectId() != null ? subjectRepository.findById(updateDTO.getSubjectId())
                            .orElseThrow(() -> new IllegalArgumentException("Subject not found"))
                            : null;

                    Professor updatedProfessor = Professor.builder()
                            .id(id)
                            .name(updateDTO.getName())
                            .surname(updateDTO.getSurname())
                            .subject(subject)
                            .build();

                    return ResponseEntity.ok(
                            new ProfessorDTO(professorRepository.save(updatedProfessor))
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteProfessorById(Integer id) {
        Optional<Professor> optionalProfessor = professorRepository.findById(id);
        if (optionalProfessor.isPresent()) {
            professorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> assignSubjectToProfessor(Integer professorId, Integer subjectId) {
        Optional<Professor> optionalProfessor = professorRepository.findById(professorId);
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

        if (optionalProfessor.isPresent() && optionalSubject.isPresent()) {
            Professor professor = optionalProfessor.get();
            Subject subject = optionalSubject.get();
            professor.setSubject(subject);
            professorRepository.save(professor);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
