package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private User destination;

    @Column(name = "read_date")
    private Date readDate;

    @Column(name = "text_message")
    private String textMessage;

}
