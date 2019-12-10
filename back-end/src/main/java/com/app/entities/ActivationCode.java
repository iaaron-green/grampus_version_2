package com.app.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean activate;
    private Long userId;
    private String code;
    private Date date;


    public ActivationCode() {
    }

    public ActivationCode(Long user_id, String code) {
        this.activate = false;
        this.userId = user_id;
        this.code = code;
    }
}
