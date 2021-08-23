package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@Table(name = "birthplaces")
public class Birthplace extends BaseEntity {

    @Column(name = "birthplace")
    private String birthplace;

    @Column(name = "guid")
    private UUID guid;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private User author;
}
