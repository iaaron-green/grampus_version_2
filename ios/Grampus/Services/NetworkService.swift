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
    
    let storage = StorageService()
    
    func signIn(username: String, password: String, completion: @escaping (String?) -> ()) {
        
        let loginURL: String = "\(DynamicURL.dynamicURL.rawValue)users/login"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8"
        ]
        
        let body: [String : Any] = [
            "username": username,
            "password": password,
        ]
        
        Alamofire.request(loginURL, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
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
    
    
    func signUp(email: String, password: String, fullName: String, completion: @escaping (Bool) -> ()) {
        
        let registerURL: String = "\(DynamicURL.dynamicURL.rawValue)users/register"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8"
        ]
        
        let body: [String : Any] = [
            "username": email,
            "password": password,
            "fullName": fullName
        ]
        
        Alamofire.request(registerURL, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            switch responseJSON.result {
            case .success :
                completion(true)
                
            case .failure(let error) :
                self.handleError(error: error)
                completion(false)
            }
        }
    }
    
    
    func fetchUserInformation(userId: String, completion: @escaping (NSDictionary?) -> ()) {
        
        let userURL: String = "\(DynamicURL.dynamicURL.rawValue)profiles/\(userId)"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        Alamofire.request(userURL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
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
        
        Alamofire.request(profilesURL, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
            switch responseJSON.result {
            case .success :
                completion(true)
            case .failure(let error) :
                self.handleError(error: error)
                completion(false)
            }
        }
    }
    
    
    func fetchAllUsers(completion: @escaping (JSON?) -> ()) {
        
        let allProfilesURL = "\(DynamicURL.dynamicURL.rawValue)profiles/all"
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        Alamofire.request(allProfilesURL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
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
    
    
    func addLikeOrDislike( ratingType: String, likeState: Bool ) {
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let body: [String : Any] = [
            "ratingType": ratingType
        ]
        
        var apiUrl = ""
        print(ratingType, likeState)
        if likeState {
            
            apiUrl = "\(DynamicURL.dynamicURL.rawValue)profiles/\(storage.getSelectedUserId()!)/like"
        } else {
            apiUrl = "\(DynamicURL.dynamicURL.rawValue)profiles/\(storage.getSelectedUserId()!)/dislike"
        }
        print(apiUrl)
        Alamofire.request(apiUrl, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            print(responseJSON)
            switch responseJSON.result {
                
            case .success :
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
        
        Alamofire.request(userIdURL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
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
    
    func uploadImage(selectedImage: UIImage?){
        
        let imageURL = "\(DynamicURL.dynamicURL.rawValue)profiles/photo"
        
        let headers : HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
        let userID = storage.getUserId()
        let parameters: Parameters = [
        "id" : userID!
        ]
        
        Alamofire.upload(multipartFormData: { (multipart: MultipartFormData) in
            let imageData = selectedImage!.jpegData(compressionQuality: 0.8)
            multipart.append(imageData!, withName: "file", fileName: "file.png", mimeType: "image/png")
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
                break
            case .failure(let error):
                self.handleError(error: error)
                    break
                }
            })
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
