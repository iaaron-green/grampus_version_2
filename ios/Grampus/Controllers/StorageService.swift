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
    
    
    func chooseLikeOrDislike( bool: Bool ) {
        def.set(bool, forKey: UserDefKeys.likeState.rawValue)
        def.synchronize()
    }
    
    func saveSelectedUserId( selectedUserId: Int ) {
        def.set(selectedUserId, forKey: UserDefKeys.selectedUserId.rawValue)
        def.synchronize()
    }
    
    func saveSelectedUserIdProfile(id: Int) {
        def.set("\(id)", forKey: UserDefKeys.selectedUserIdProfile.rawValue)
    }
    
    func saveProfileState(state: Bool) {
        def.set(state, forKey: UserDefKeys.profileState.rawValue)

    }
    
    
    
    
    
    
    func decodeJwt( token: String) {
        
        let jwt = try! decode(jwt: token)
        let userId = jwt.claim(name: "id").rawValue
        saveUserId(userId: userId! as! String)
        
    }
    
    func saveUserToken( token: String ) {
        let def = UserDefaults.standard
        def.set("\(token)", forKey: UserDefKeys.token.rawValue)
        def.synchronize()
    }
    
    func saveLoggedState(state: Bool?) {
        let def = UserDefaults.standard
        def.set(state, forKey: UserDefKeys.isLoggedIn.rawValue)
        def.synchronize()
    }
    
    func saveUserId( userId: String ) {
        let def = UserDefaults.standard
        def.set("\(userId)", forKey: UserDefKeys.userId.rawValue)
        def.synchronize()
    }
}
