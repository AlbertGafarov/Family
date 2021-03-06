package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "humans")
public class Human extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "patronim")
    private String patronim;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "surname_id")
    private Surname surname;

    @Column(name = "birthdate")
    private Calendar birthdate;

    @Column(name = "deathdate")
    private Calendar deathdate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "birthplace_id")
    private Birthplace birthplace;

    @Column(name = "gender")
    private char gender;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "humans_parents",
            joinColumns = {@JoinColumn(name = "human_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "parent_id", referencedColumnName = "id")})
    private List<Human> parents;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "humans_parents",
            joinColumns = {@JoinColumn(name = "parent_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "human_id", referencedColumnName = "id")})
    private List<Human> children;

}
