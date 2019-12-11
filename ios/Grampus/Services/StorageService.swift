//
//  StorageService.swift
//  Grampus
//
//  Created by student on 11/14/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation
import JWTDecode

class StorageService {
    
    let def = UserDefaults.standard
    
    //MARK: - Read methods
    
    func getTokenString() -> String? {
        return def.string(forKey: UserDefKeys.token.rawValue)
    }
    
    func getSelectedUserId() -> Int? {
        return def.integer(forKey: UserDefKeys.selectedUserId.rawValue)
    }
    
    func getUserId() -> String? {
        return def.string(forKey: UserDefKeys.userId.rawValue)
    }
    
    func getSelectedUserIdProfile() -> String? {
        return def.string(forKey: UserDefKeys.selectedUserIdProfile.rawValue)
    }
    
    func getProfileState() -> Bool {
        return def.bool(forKey: UserDefKeys.profileState.rawValue)
    }
    
    func getLikeState()  -> Bool? {
        return def.bool(forKey: UserDefKeys.likeState.rawValue)
    }
    
    func isLoggedIn() -> Bool {
        return def.bool(forKey: UserDefKeys.isLoggedIn.rawValue)
    }
    
    func getUserProfile() -> User? {
        
        if let savedProfile = def.object(forKey: UserDefKeys.userProfile.rawValue) as? Data {
            let decoder = JSONDecoder()
            if let loadedProfile = try? decoder.decode(User.self, from: savedProfile) {
                return loadedProfile
            }
        }
        return nil
    }
    
    
    //MARK: - Save methods
    
    func chooseLikeOrDislike( bool: Bool ) {
        def.set(bool, forKey: UserDefKeys.likeState.rawValue)
    }
    
    func saveSelectedUserId( selectedUserId: Int ) {
        def.set(selectedUserId, forKey: UserDefKeys.selectedUserId.rawValue)
    }
    
    func saveSelectedUserIdProfile(id: Int) {
        def.set("\(id)", forKey: UserDefKeys.selectedUserIdProfile.rawValue)
    }
    
    func saveProfileState(state: Bool) {
        def.set(state, forKey: UserDefKeys.profileState.rawValue)
        
    }
    
    func saveUserToken( token: String ) {
        def.set("\(token)", forKey: UserDefKeys.token.rawValue)
    }
    
    func saveLoggedState(state: Bool?) {
        def.set(state, forKey: UserDefKeys.isLoggedIn.rawValue)
    }
    
    func saveUserId( userId: String ) {
        def.set("\(userId)", forKey: UserDefKeys.userId.rawValue)
    }
    
    func saveUserProfile(user: User) {
        
        let encoder = JSONEncoder()
        if let encoded = try? encoder.encode(user) {
            def.set(encoded, forKey: UserDefKeys.userProfile.rawValue)
        }
    }
    
    
    //MARK: - Decode jwt
    
    func decodeJwt( token: String) {
        
        let jwt = try! decode(jwt: token)
        let userId = jwt.claim(name: "id").rawValue
        saveUserId(userId: userId! as! String)
    }
}
