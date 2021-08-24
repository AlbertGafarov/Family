package ru.gafarov.Family.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "surnames")
public class Surname extends BaseEntity {

    @NotNull
    @Column(name = "surname")
    private String surname;

    @Column(name = "sur_alias_1")
    private String surnameAlias1;

    @Column(name = "sur_alias_2")
    private String surnameAlias2;

    @Column(name = "declension")
    private String declension;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private User author;

    public String toString(String gender) {
        if(surname==null){
            return null;
        }
        if(declension.equals("Y") && gender.equals("W")){
            if(!surname.matches("[а-яА-Я]+ий")) {
                return surname + "a";
            } else {
                return surname.substring(surname.lastIndexOf("ий") - 1) + "ая";
            }
        } else {
            return surname;
        }
    }
}