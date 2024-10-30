package com.jonim.grades_manager.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorDTO {
    private Integer id;
    private String name;
    private String surname;
    private SubjectDTO subject;

    public ProfessorDTO(Professor professor) {
        this.id = professor.getId();
        this.name = professor.getName();
        this.surname = professor.getSurname();
        if (professor.getSubject() != null) {
            this.subject = new SubjectDTO(professor.getSubject());
        } else {
            this.subject = null;
        }
    }
}
