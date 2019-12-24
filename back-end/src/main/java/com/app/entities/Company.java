package com.app.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "companies")
public class Company {

    public Company() {
    }


    @Id
    private Long id;

    private String companyName;
    private Set<User> users;

    @ManyToMany
    @JoinTable(
            name = "user_company",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id"))
    private Set<User> companies;

}
