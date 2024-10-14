package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Professor;
import com.jonim.grades_manager.repositories.ProfessorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    private ProfessorRepository professorRepository;

    public ProfessorServiceImpl(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public ResponseEntity<Professor> getProfessorById(Integer id) {
        Optional<Professor> professorOptional = professorRepository.findById(id);
        return professorOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
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
}
