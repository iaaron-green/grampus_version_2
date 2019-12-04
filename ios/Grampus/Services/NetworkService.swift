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
                completion(error.localizedDescription)
                self.handleError(error: error)
            }
        }
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
                
                self.handleError(error: error)
                completion(false, error.localizedDescription)
            }
        }
    }
    
    
    func fetchUserInformation(userId: String, completion: @escaping (NSDictionary?) -> ()) {
        
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
                    completion(json)
                }
                
            case .failure(let error) :
                self.handleError(error: error)
                completion(nil)
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
                self.handleError(error: error)
                completion(false)
            }
        }
    }
    
    
    func fetchAllUsers(page: Int, name: String, completion: @escaping (JSON?) -> ()) {
        
        let allProfilesURL = "\(DynamicURL.dynamicURL.rawValue)profiles/all"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let parameters: Parameters = [
            "page" : String(page),
            "searchParam": name
        ]
        
        manager.request(allProfilesURL, method: .get, parameters: parameters, encoding: URLEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            

            switch responseJSON.result {
            case .success :
                
                if let result = responseJSON.result.value {
                    completion(JSON(result))
                }
                
            case .failure(let error) :
                self.handleError(error: error)
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
        
        var apiUrl = ""
        if likeState {
            
            apiUrl = "\(DynamicURL.dynamicURL.rawValue)profiles/\(storage.getSelectedUserId()!)/like"
        } else {
            apiUrl = "\(DynamicURL.dynamicURL.rawValue)profiles/\(storage.getSelectedUserId()!)/dislike"
        }

        manager.request(apiUrl, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in

            switch responseJSON.result {
            case .success :
                print(apiUrl)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "load"), object: nil)
                
            case .failure(let error) :
                self.handleError(error: error)
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
                self.handleError(error: error)
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
        
//        let userID = storage.getUserId()
//        let parameters: Parameters = [
//        "id" : userID!
//        ]
        
        manager.upload(multipartFormData: { (multipart: MultipartFormData) in
            let imageData = selectedImage!.jpegData(compressionQuality: 0.8)
            multipart.append(imageData!, withName: "file", fileName: "file.png", mimeType: "image/png")
//            for (key,value) in parameters {
//                 multipart.append((value as! String).data(using: .utf8)!, withName: key)
//            }
            
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
                self.handleError(error: error)
                completion(false)
                    break
                }
            })
    }
    
    func followUser(completion: @escaping (Bool) -> ()) {
        
        let url = "\(DynamicURL.dynamicURL.rawValue)profiles/\(storage.getSelectedUserIdProfile()!)/change-subscription"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        

        manager.request(url, method: .post, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in

            switch responseJSON.result {
            case .success :
                print(url)
                completion(true)
            case .failure(let error) :
                completion(false)
                self.handleError(error: error)
            }
        }
    }
    
    func handleError(error: Error) {
        if let error = error as? AFError {
            switch error {
            case .invalidURL(let url):
                print("Invalid URL: \(url) - \(error.localizedDescription)")
            case .parameterEncodingFailed(let reason):
                print("Parameter encoding failed: \(error.localizedDescription)")
                print("Failure Reason: \(reason)")
            case .multipartEncodingFailed(let reason):
                print("Multipart encoding failed: \(error.localizedDescription)")
                print("Failure Reason: \(reason)")
            case .responseValidationFailed(let reason):
                print("Response validation failed: \(error.localizedDescription)")
                print("Failure Reason: \(reason)")

                switch reason {
                case .dataFileNil, .dataFileReadFailed:
                    print("Downloaded file could not be read")
                case .missingContentType(let acceptableContentTypes):
                    print("Content Type Missing: \(acceptableContentTypes)")
                case .unacceptableContentType(let acceptableContentTypes, let responseContentType):
                    print("Response content type: \(responseContentType) was unacceptable: \(acceptableContentTypes)")
                case .unacceptableStatusCode(let code):
                    print("Response status code was unacceptable: \(code)")
                }
            case .responseSerializationFailed(let reason):
                print("Response serialization failed: \(error.localizedDescription)")
                print("Failure Reason: \(reason)")
            }

            print("Underlying error: \(String(describing: error.underlyingError))")
        } else if let error = error as? URLError {
            print("URLError occurred: \(error)")
        } else {
            print("Unknown error: \(error)")
        }
    }
}
