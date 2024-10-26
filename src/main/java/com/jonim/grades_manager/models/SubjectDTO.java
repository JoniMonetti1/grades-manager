package com.jonim.grades_manager.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectDTO {
    private Integer id;
    private String name;

    public SubjectDTO(Subject subject) {
        this.id = subject.getId();
        this.name = subject.getName();
    }
}
