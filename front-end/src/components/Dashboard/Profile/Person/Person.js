import React from 'react';


import styles from './Person.module.css';

const Person = ({profile, user}) => {
  return (
    <div className={styles.person}>
      <img
        className={styles.img}
        src={profile.profilePicture}
        alt={profile.id}
        width="180px"
        height="110px"
      />
      <input type='file' />
      <div className={styles.userName}>{user.fullName}</div>
      <div className={styles.jobTitle}>{user.jobTitle}</div>
      <div className={styles.likes}>{user.likes}</div>
      <div className={styles.dislikes}>{user.dislikes}</div>
    </div>
  );
};

export default Person;

window.addEventListener('load', function() {
  document.querySelector('input[type="file"]').addEventListener('change', function() {
    if (this.files && this.files[0]) {
      const img = document.querySelector('img');  // $('img')[0]
      img.src = URL.createObjectURL(this.files[0]); // set src to blob url
      img.onload = imageIsLoaded;
    }
  });
});

function imageIsLoaded() {
  // alert(this.src);  // blob url
  // update width and height ...
}
