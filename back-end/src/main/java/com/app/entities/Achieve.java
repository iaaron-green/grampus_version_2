package com.app.entities;

import com.app.enums.Mark;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "achieves")
public class Achieve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToMany(mappedBy = "users", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @NotBlank(message = "User is required")
    private User user;
    @NotBlank(message = "Mark is required")
    private Mark mark;

    public Achieve(){}

    public Achieve(long id, User user, String mark){
        this.id = id;
        this.user = user;
        this.mark = Mark.getMark(mark);
    }
    public Achieve (User user, Mark mark){
        this.user = user;
        this.mark = mark;
    }
    public Achieve (User user, String mark){
        this.user = user;
        this.mark = Mark.getMark(mark);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achieve achieve = (Achieve) o;
        return id == achieve.id &&
                Objects.equals(user, achieve.user) &&
                mark == achieve.mark;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, user, mark);
    }

    @Override
    public String toString() {
        return "Achieve{" +
                "id=" + id +
                ", user=" + user +
                ", mark=" + mark +
                '}';
    }
}
