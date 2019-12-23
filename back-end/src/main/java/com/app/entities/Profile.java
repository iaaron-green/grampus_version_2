package com.app.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@Entity
@Table(name = "profiles")
@ToString
public class Profile {
   @Id
   private Long id;

   private String profilePicture;

   private Long likes;

   private Long dislikes;

   private String skype;

   private String phone;

   private String telegram;

   private String skills;

   private String country;

   @OneToOne
   private User user;

   @OneToMany(cascade = CascadeType.ALL,
           fetch = FetchType.LAZY, mappedBy = "profileRating")
   private List<Rating> ratings = new ArrayList<>();


   public Profile() {
   }

   public Profile(User user) {
      this.id = user.getId();
      this.likes = 0L;
      this.dislikes = 0L;
      this.user = user;
   }

}
