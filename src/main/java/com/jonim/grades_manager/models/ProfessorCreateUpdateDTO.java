package com.jonim.grades_manager.models;

import lombok.Data;

@Data
public class ProfessorCreateUpdateDTO {
    private String name;
    private String surname;
    private Integer subjectId;
}
