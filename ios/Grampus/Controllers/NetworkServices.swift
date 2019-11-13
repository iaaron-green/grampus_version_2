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
import JWTDecode

class NetworkService {

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
                    
                    self.saveUserToken(token: tokenWithOutBearer)
                    
                    self.decodeJwt(token: tokenWithOutBearer)
                    self.saveLoggedState()

                    completion(true)
                }
                
            case .failure(let error) :
                completion(false)
                print(error)
            }
        }
    }
    
    func saveUserToken( token: String ) {
        let def = UserDefaults.standard
        def.set("\(token)", forKey: UserDefKeys.token.rawValue)
        def.synchronize()
    }
    
    func saveLoggedState() {
        let def = UserDefaults.standard
        def.set(true, forKey: UserDefKeys.isLoggedIn.rawValue)
        def.synchronize()
    }
    
    func saveUserId( userId: String ) {
        let def = UserDefaults.standard
        def.set("\(userId)", forKey: UserDefKeys.userId.rawValue)
        def.synchronize()
    }
    
    func decodeJwt( token: String) {
        
        let jwt = try! decode(jwt: token)
        let userId = jwt.claim(name: "id").rawValue
        saveUserId(userId: userId! as! String)
        
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
            
            let def = UserDefaults.standard
            let token = def.string(forKey: UserDefKeys.token.rawValue)
            //print(token!)
            
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
    
}
