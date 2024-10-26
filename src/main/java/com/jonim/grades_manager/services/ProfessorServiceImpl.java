package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Professor;
import com.jonim.grades_manager.models.ProfessorDTO;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.ProfessorRepository;
import com.jonim.grades_manager.repositories.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<Professor>> getProfessorList() {
        List<Professor> professorList = professorRepository.findAll();
        return professorList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(professorList);
    }

    @Override
    public ResponseEntity<Professor> saveProfessor(Professor professor) {
        Professor savedProfessor = professorRepository.save(professor);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProfessor.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedProfessor);
    }

    @Override
    public ResponseEntity<Professor> modifyProfessor(Integer id, Professor professor) {
        Optional<Professor> optionalProfessorToModify = professorRepository.findById(id);

        if (optionalProfessorToModify.isPresent()) {
            professor.setId(id);
            Professor modifiedProfessor = professorRepository.save(professor);
            return ResponseEntity.ok(modifiedProfessor);
        }
        return ResponseEntity.notFound().build();
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
