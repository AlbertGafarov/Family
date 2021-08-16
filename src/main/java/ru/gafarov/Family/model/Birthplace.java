package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@Table(name = "birthplaces")
public class Birthplace extends BaseEntity {

    @Column(name = "birthplace")
    private String birthplace;

    @Column(name = "guid")
    private String guid;

    @Override
    public String toString() {
        return birthplace;
    }
}
