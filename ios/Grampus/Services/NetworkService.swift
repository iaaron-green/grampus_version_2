//
//  NetworkServices.swift
//  Grampus
//
//  Created by student on 11/12/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON

class NetworkService {
    
    let manager : SessionManager = {
           let configuration = URLSessionConfiguration.default
               configuration.timeoutIntervalForRequest = 10
               configuration.timeoutIntervalForResource = 10
           let manager = Alamofire.SessionManager(configuration: configuration)

           return manager
       }()
    
    let storage = StorageService()
    
    func signIn(username: String, password: String, completion: @escaping (String?) -> ()) {
        
        let loginURL: String = "\(DynamicURL.dynamicURL.rawValue)users/login"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8"
        ]
        
        let body: [String : Any] = [
            "email": username,
            "password": password,
        ]
        
        manager.request(loginURL, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                
                if let result = responseJSON.result.value {
                    let JSON = result as! NSDictionary
                    let tokenWithBearer = (JSON["token"]! as! String)
                    let wordToRemove = "Bearer "
                    let tokenWithOutBearer = tokenWithBearer.deletingPrefix(wordToRemove)
                    self.storage.saveUserToken(token: tokenWithOutBearer)
                    //print(tokenWithOutBearer)
                    self.storage.decodeJwt(token: tokenWithOutBearer)
                    self.storage.saveLoggedState(state: true)
                    completion(nil)
                }
                
            case .failure(let error) :
                completion(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
            }
        }
    }
    
    func getErrorMessageFromAPI(responseJSON: DataResponse<Any>) -> String? {
        var errorMessage: String?
        if let data = responseJSON.data {
            if let json = try? JSON(data: data) {
                let message: String = json["message"].stringValue
                  if !message.isEmpty {
                    errorMessage = message
                  }
            }
         }
        return errorMessage
    }
    
    
    func signUp(username: String, password: String, fullName: String, completion: @escaping (Bool, String?) -> ()) {
        
        let registerURL: String = "\(DynamicURL.dynamicURL.rawValue)users/register"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8"
        ]
        
        let body: [String : Any] = [
            "email": username,
            "password": password,
            "fullName": fullName
        ]
        
        manager.request(registerURL, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                completion(true, nil)
                
            case .failure(let error) :
                completion(false, self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
            }
        }
    }
    
    
    func fetchUserInformation(userId: String, completion: @escaping (NSDictionary?, String?) -> ()) {
        
        let userURL: String = "\(DynamicURL.dynamicURL.rawValue)profiles/\(userId)"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        manager.request(userURL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                if let result = responseJSON.result.value {
                    let json = result as! NSDictionary
                    completion(json, nil)
                }
                
            case .failure :
                print(userURL)
                completion(nil, self.getErrorMessageFromAPI(responseJSON: responseJSON))
            }
        }
    }
    
    
    func editProfileText( key: String, text: String, completion: @escaping (Bool) -> ()) {
        
        let profilesURL: String = "\(DynamicURL.dynamicURL.rawValue)profiles/"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let body: [String : Any] = [
            key: text
        ]
        
        manager.request(profilesURL, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
            switch responseJSON.result {
            case .success :
                completion(true)
            case .failure(let error) :
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                completion(false)
            }
        }
    }
    
    
    func fetchAllUsers(page: Int, name: String, ratingType: String, completion: @escaping (JSON?) -> ()) {
        
        let allProfilesURL = "\(DynamicURL.dynamicURL.rawValue)profiles/all"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let parameters: Parameters = [
            "page" : String(page),
            "searchParam": name,
            "ratingType": ratingType
        ]
        
        manager.request(allProfilesURL, method: .get, parameters: parameters, encoding: URLEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            

            switch responseJSON.result {
            case .success :
                
                if let result = responseJSON.result.value {
                    completion(JSON(result))
                }
                
            case .failure(let error) :
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                completion(nil)
            }
        }
    }
    
    
    func addLikeOrDislike( ratingType: String, likeState: Bool, message: String ) {
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let body: [String : Any] = [
            "ratingType": ratingType,
            "message" : message
        ]
        
        let apiUrl = "\(DynamicURL.dynamicURL.rawValue)profiles/\(storage.getSelectedUserId()!)/addRating"


        manager.request(apiUrl, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in

            switch responseJSON.result {
            case .success :
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "load"), object: nil)
            case .failure(let error) :
                 print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
            }
        }
    }
    
    func fetchAchievements(userId: String, completion: @escaping (NSDictionary?) -> ()) {
        
        var achievements: NSDictionary!
        
        let userIdURL: String = "\(DynamicURL.dynamicURL.rawValue)profiles/\(userId)"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        manager.request(userIdURL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
            switch responseJSON.result {
            case .success :
                
                if let result = responseJSON.result.value {
                    
                    let json = result as! NSDictionary
                    
                    achievements = json["achievements"] as? NSDictionary
                    completion(achievements)
                }
                
            case .failure(let error) :
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                achievements = ["empty": "true"]
                completion(nil)
            }
        }
    }
    
    func uploadImage(selectedImage: UIImage?, completion: @escaping (Bool) -> ()){
        
        let userID = storage.getUserId()

        let imageURL = "\(DynamicURL.dynamicURL.rawValue)profiles/\(userID!)/photo"
        
        let headers : HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        manager.upload(multipartFormData: { (multipart: MultipartFormData) in
            let imageData = selectedImage!.jpegData(compressionQuality: 0.8)
            multipart.append(imageData!, withName: "file", fileName: "file.png", mimeType: "image/png")
        },usingThreshold: UInt64.init(),
           to: imageURL,
           method: .post,
           headers: headers,
           encodingCompletion: { (result) in
            switch result {
            case .success(let upload, _, _):
                upload.uploadProgress(closure: { (progress) in
                })
                completion(true)
                break
            case .failure(let error):
                print(error.localizedDescription)
                completion(false)
                    break
                }
            })
    }
    
    func followUser(completion: @escaping (Bool) -> ()) {
        
        let url = "\(DynamicURL.dynamicURL.rawValue)profiles/\(storage.getSelectedUserId()!)/change-subscription"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        manager.request(url, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in

            switch responseJSON.result {
            case .success :
                completion(true)
            case .failure(let error) :
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                completion(false)
            }
        }
    }
    
    func uploadNews(selectedImage: UIImage?, topic: String, body: String, completion: @escaping (Bool) -> ()){
            
            let userID = storage.getUserId()!
            
            let imageURL = "\(DynamicURL.dynamicURL.rawValue)news"
            
            let headers : HTTPHeaders = [
                "Content-Type": "application/json; charset=utf-8",
                "Authorization": "Bearer \(storage.getTokenString()!)"
            ]
            
            let parameters: Parameters = [
            "title" : topic,
            "content" : body,
            "userID" : userID
            ]
            
            manager.upload(multipartFormData: { (multipart: MultipartFormData) in
                if selectedImage != nil {
                    if let imageData = selectedImage!.jpegData(compressionQuality: 0.8) {
                        multipart.append(imageData, withName: "file", fileName: "file.png", mimeType: "image/png")
                    }
                }
                for (key,value) in parameters {
                     multipart.append((value as! String).data(using: .utf8)!, withName: key)
                }
                
            },usingThreshold: UInt64.init(),
               to: imageURL,
               method: .post,
               headers: headers,
               encodingCompletion: { (result) in
                switch result {
                case .success(let upload, _, _):
                    upload.uploadProgress(closure: { (progress) in
                    })
                    completion(true)
                    break
                case .failure(let error):
                    print(error.localizedDescription)
                    completion(false)
                        break
                    }
                })
        }
    
    func fetchNews(page : Int, completion: @escaping (JSON?) -> ()) {
        
        let newsURL: String = "\(DynamicURL.dynamicURL.rawValue)news"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let parameters: Parameters = [
            "page" : String(page)
        ]
        
        manager.request(newsURL, method: .get, parameters: parameters, encoding: URLEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                
                if let result = responseJSON.result.value {
                    completion(JSON(result))
                }
                
            case .failure(let error) :
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                completion(nil)
            }
        }
    }
    
    func deleteNews(id: Int, completion: @escaping (Bool) -> ()){
        
        let newsURL: String = "\(DynamicURL.dynamicURL.rawValue)news/delete/\(id)"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
//        let parameters: Parameters = [
//        "id" : id
//        ]
        
        manager.request(newsURL, method: .delete, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                completion(true)
            case .failure(let error) :
                print(newsURL)
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                completion(false)
            }
        }
    }
    
    func sendComment(comment: String, id: Int, completion: @escaping (Bool) -> ()){
        
        let newsURL: String = "\(DynamicURL.dynamicURL.rawValue)news/comment/"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let parameters: Parameters = [
        "id" : id,
        "text" : comment
        ]
        
        manager.request(newsURL, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                completion(true)
            case .failure(let error) :
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                completion(false)
            }
        }
    }
    
    func fetchComments(newsID : Int, page: Int, completion: @escaping (JSON?) -> ()) {
        
        let newsURL: String = "\(DynamicURL.dynamicURL.rawValue)news/comment/\(newsID)"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let parameters: Parameters = [
            "page" : String(page)
        ]
        
        manager.request(newsURL, method: .get, parameters: parameters, encoding: URLEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                if let result = responseJSON.result.value {
                    completion(JSON(result))
                }
                
            case .failure(let error) :
                print(self.getErrorMessageFromAPI(responseJSON: responseJSON) ?? error.localizedDescription)
                completion(nil)
            }
        }
    }
    
}
