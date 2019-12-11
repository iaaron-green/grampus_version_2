API 1.0

Coonnection to DB

spring.datasource.url= jdbc:mysql://10.11.1.155:3306/grampus_db?\
  useSSL=false&serverTimezone=UTC&\
  allowPublicKeyRetrieval=true
spring.datasource.username=testuser
spring.datasource.password=1234qwer


In ALL methods you shoud input user token to header in POSTMAN and parameters to JSON except
Register new User

##Content
* Register new User
* Login User
* GET Profile By Id
* add Like To Profile
* add Dislike To Profile
* update Profile By Id
* upload Photo
* get All Profiles
* get All Achieve
* get All Info
* get User Rating
* get User ByJob

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
"email":"",
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

 
##get All Profiles
    GET  http://localhost:8081/api/profiles/all

**Description:**
Method allows to get all likeable profiles
   
**Input**   
If the parameter is not specified(optional), the default values will be applied.
default value:   
page = 0, size = 5

| name | type | description                         |
|------|------|-------------------------------------|
| page | int  | page where we start viewing         |
| size | int  | number of displayed values per page |

**Output example**

```json

    [
        {
            "id": 12,
            "fullName": "natata",
            "jobTitle": "CEO",
            "profilePicture": null,
            "isAbleToLike": true
        },
        {
            "id": 13,
            "fullName": "NameSmith",
            "jobTitle": "CFO",
            "profilePicture": "ftp://10.11.1.155/img/13.jpeg",
            "isAbleToLike": true
        }
    ]

```

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
    
**valid jobTitle:**   
DEVELOPER, TESTER, ANALYST, CEO   

**Description:**
Allows you to get the users by jobTitle

**Input**   
If the parameter is not specified(it is optional), the default values will be applied.
default value:   
page = 0, size = 2

| name | type | description                         |
|------|------|-------------------------------------|
| page | int  | page where we start viewing         |
| size | int  | number of displayed values per page |

**Output example**

```json
[
    {
        "profileId": 3,
        "picture": "ftp://10.11.1.155/img/3.png",
        "fullName": "222222@gmail.com",
        "jobTitle": "PM",
        "isAbleToLike": null
    },
    {
        "profileId": 9,
        "picture": "ftp://10.11.1.155/img/9.jpeg",
        "fullName": "aaa3@gmail.com",
        "jobTitle": "PM",
        "isAbleToLike": null
    }
]
```
 
 
