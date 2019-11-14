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

    func signIn(username: String, password: String, completion: @escaping (Bool) -> ()) {
                
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
                    self.storage.decodeJwt(token: tokenWithOutBearer)
                    self.storage.saveLoggedState(state: true)

                    completion(true)
                }
                
            case .failure(let error) :
                completion(false)
                print(error)
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
                    print(error)
                    completion(false)
                }
            }
        }
    
    func fetchUserInformation(userId: String, completion: @escaping (NSDictionary?) -> ()) {
            
            let token = storage.getTokenString()
            
            let userURL: String = "\(DynamicURL.dynamicURL.rawValue)profiles/\(userId)"
            
            let headers: HTTPHeaders = [
                "Content-Type": "application/json; charset=utf-8",
                "Authorization": "Bearer \(token!)"
            ]
            
            Alamofire.request(userURL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
                
                switch responseJSON.result {
                case .success :
                    
                    if let result = responseJSON.result.value {
                        
                        let json = result as! NSDictionary

                        completion(json)
                    }
                    
                case .failure(let error) :
                    print(error)
                    completion(nil)
                }
            }
        }
    
    func editProfileText( key: String, text: String, completion: @escaping (Bool) -> ()) {

        let def = UserDefaults.standard
        let token = def.string(forKey: UserDefKeys.token.rawValue)
        let profilesURL: String = "\(DynamicURL.dynamicURL.rawValue)profiles/"

        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(token!)"
        ]

        let body: [String : Any] = [
            key: text
        ]

        Alamofire.request(profilesURL, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in

            switch responseJSON.result {
            case .success :
                completion(true)
            case .failure(let error) :
                print(error)
                completion(false)
            }
        }

    }
    
    func fetchAllUsers(completion: @escaping (JSON?) -> ()) {
        
        let def = UserDefaults.standard
        let allProfilesURL = "\(DynamicURL.dynamicURL.rawValue)profiles/all"
        let token = def.string(forKey: UserDefKeys.token.rawValue)
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(token!)"
        ]
        
        Alamofire.request(allProfilesURL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
            switch responseJSON.result {
            case .success :
                
                if let result = responseJSON.result.value {
                    
                    
                    completion(JSON(result))
                    
                }
                
            case .failure(let error) :
                print(error)
                completion(nil)
                
            }
        }
        
    }
    
    func addLikeOrDislike( ratingType: String, likeState: Bool ) {
        
        let def = UserDefaults.standard
        let token = def.string(forKey: UserDefKeys.token.rawValue)
        
        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(token!)"
        ]
        
        let body: [String : Any] = [
            "ratingType": "\(String(describing: ratingType))"
        ]
        
        var apiUrl = ""
        
        if likeState {
            
            apiUrl = "\(DynamicURL.dynamicURL.rawValue)profiles/\(String(describing: storage.getSelectedUserId()!))/like"
        } else {
            apiUrl = "\(DynamicURL.dynamicURL.rawValue)profiles/\(String(describing: storage.getSelectedUserId()!))/dislike"
        }
        
        
        Alamofire.request(apiUrl, method: .post, parameters: body, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in
            
            switch responseJSON.result {
            case .success :
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "load"), object: nil)
                
            case .failure(let error) :
                print(error)
            }
        }
    }
    
    func fetchAchievements(userId: String, completion: @escaping (NSDictionary?) -> ()) {

        let def = UserDefaults.standard
        let token = def.string(forKey: UserDefKeys.token.rawValue)
        var achievements: NSDictionary!

        let API_URL: String = "\(DynamicURL.dynamicURL.rawValue)profiles/\(userId)"

        let headers: HTTPHeaders = [
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer \(token!)"
        ]

        Alamofire.request(API_URL, method: .get, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { responseJSON in

            switch responseJSON.result {
            case .success :

                if let result = responseJSON.result.value {

                    let json = result as! NSDictionary

                    achievements = json["achievements"] as? NSDictionary
                    completion(achievements)
                }

            case .failure(let error) :
                print(error)

                achievements = ["empty": "true"]
                completion(nil)
            }
        }

    }
    
}
