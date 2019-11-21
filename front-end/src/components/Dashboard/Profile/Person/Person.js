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
      <input  type="file" onChange={UploadImage()}/>
      <div className={styles.container}>
        <div className={styles.userName}>{user.fullName}</div>
        <div className={styles.jobTitle}>{user.jobTitle}</div>
      </div>
    </div>
  );
};




function UploadImage() {
  window.addEventListener('load', function() {
    document.querySelector('input[type="file"]').addEventListener('change', function() {
      if (this.files && this.files[0]) {
        const img = document.querySelector('img');
        img.src = URL.createObjectURL(this.files[0]);
        img.onload = imageIsLoaded;
      }
    });
  });
}

function imageIsLoaded() {
}


export default Person;
