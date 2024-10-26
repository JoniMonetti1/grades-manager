package com.jonim.grades_manager.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "professors")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;
    @OneToOne(targetEntity = Subject.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "subject_id")
    private Subject subject;
}
