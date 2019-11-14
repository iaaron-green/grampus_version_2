package com.app.entities;

import com.app.enums.JobTitle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;


@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String header;
    private String info;
    private String skills;
    private String base64Image;
    private List<Achieve> achieveList = new ArrayList<>();
    @OneToOne
    private User user;

    public Profile(){}

    public Profile(long id){
        this.id = id;
    }

    public Profile(long id, String header, String info, String skills, String base64Image, List<Achieve> achieveList){
        this.id = id;
        this.header = header;
        this.info = info;
        this.skills = skills;
        this.base64Image = base64Image;
        this.achieveList = achieveList;
    }
    public Profile(User user){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public List<Achieve> getAchieveList() {
        return achieveList;
    }

    public void setAchieveList(List<Achieve> achieveList) {
        this.achieveList = achieveList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return id == profile.id &&
                Objects.equals(header, profile.header) &&
                Objects.equals(info, profile.info) &&
                Objects.equals(skills, profile.skills) &&
                Objects.equals(base64Image, profile.base64Image) &&
                Objects.equals(achieveList, profile.achieveList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, header, info, skills, base64Image, achieveList);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", info='" + info + '\'' +
                ", skills='" + skills + '\'' +
                ", base64Image='" + base64Image + '\'' +
                ", achieveList=" + achieveList +
                '}';
    }

}
