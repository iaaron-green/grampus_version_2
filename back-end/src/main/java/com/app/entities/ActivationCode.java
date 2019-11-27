package com.app.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean activate;
    private Long userId;


    public ActivationCode() {
    }

    public ActivationCode(Long user_id) {
        this.activate = false;
        this.userId = user_id;
    }
}
