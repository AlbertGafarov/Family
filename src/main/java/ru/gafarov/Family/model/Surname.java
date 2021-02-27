package ru.gafarov.Family.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "surnames")
public class Surname extends BaseEntity {

    @NotNull
    @Column(name = "surname")
    private String surname;

    @Column(name = "sur_alias_1")
    private String surnameAlias1;

    @Column(name = "sur_alias_2")
    private String surnameAlias2;

    @Override
    public String toString() {
        if(surname==null){
            return null;
        }
        return surname;
    }
}
