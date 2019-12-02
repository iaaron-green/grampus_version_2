Coonnection to DB

spring.datasource.url= jdbc:mysql://10.11.1.155:3306/grampus_db?\
  useSSL=false&serverTimezone=UTC&\
  allowPublicKeyRetrieval=true
spring.datasource.username=testuser
spring.datasource.password=1234qwer


In ALL methods you shoud input user token to header in POSTMAN and parameters to JSON except
Register new User

##Content

##Register new User
    POST  http://localhost:8081/api/users/register
 **Description:**
 *Input parameters*
{
"email":"",
"password":"",
"fullName":""
}

*Output example*

```json
{
    "userId": 2,
    "email": "123@gmail.com",
    "password": "******",
    "fullName": "aaa@gmail.com"
}
```

##Login User
    POST http://localhost:8081/api/users/login (Generates user token if user was already created)   
*Input parameters*
{
"username":"",
"password":""
}

##GET Profile By Id
    GET  http://localhost:8081/api/profiles/{profileId}  
**Description:**   
 GET  http://localhost:8081/api/profiles/{profileId}  

Output example

```json
{
    "id": 43,
    "profilePicture": null,
    "likes": 1,
    "dislikes": 1,
    "information": null,
    "skills": null,
    "email": "111qq44qqq@gmail.com",
    "jobTitle": "HR",
    "fullName": "qqqq",
    "likesNumber": {
        "best_looker": 1,
        "smart_mind": 0,
        "mentor": 0,
        "motivator": 0,
        "top1": 0,
        "deadliner": 1,
        "super_worker": 0
    }
}
```

##add Like To Profile
    POST  http://localhost:8081/api/profiles/{profileId}/like   
**Description:**   
*Input parameters*


##add Dislike To Profile
    POST  http://localhost:8081/api/profiles/{profileId}/dislike
**Description:**


##update Profile By Id
    POST  http://localhost:8081/api/profiles
**Description:**
*Input parameters*

##upload Photo
    POST  http://localhost:8081/api/profiles/photo
**Description:**
*Input parameters*
 
 
##get All Profiles
    GET  http://localhost:8081/api/profiles/all
**Description:**

##get All Achieve
**Description:**
 GET  http://localhost:8081/api/profiles/achive
 
 
##get All Info
    GET  http://localhost:8081/api/profiles/catalogue
**Description:**
 Input parameters

##get User Rating
    GET  http://localhost:8081/api/profiles/userRating/{markType}
**markType valid value:** BEST_LOOKER, DEADLINER, SMART_MIND, SUPER_WORKER, MOTIVATOR, TOP1, MENTOR   
   
**Description:**

##get User ByJob
    GET  http://localhost:8081/api/profiles/userJobTitle/{jobTitle}
    
**jobTitle example:** DEVELOPER, TESTER, ANALYST, CEO   
**Description:**
 Output example
```json

```
 
 
