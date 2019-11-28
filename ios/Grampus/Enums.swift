//
//  Enums.swift
//  Grampus
//
//  Created by Тимур Кошевой on 6/7/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation

enum SegueIdentifier: String {
    case login_to_profile = "login_to_profile"
    case rating_to_selected_profile = "rating_to_selected_profile"
}

enum DynamicURL: String {
    //case dynamicURL = "https://grampus.herokuapp.com/api/" //web
    case dynamicURL = "http://10.11.1.155:8081/api/" //host
    //case dynamicURL = "http://10.11.1.200:8081/api/" //vadim
    //case dynamicURL = "http://10.11.1.25:8081/api/" //igor
}

enum UserDefKeys: String {
    case isLoggedIn = "isLoggedIn"
    case token = "token"
    case userId = "userId"
    case likeState = "like"
    case selectedUserId = "selectedUserId"
    case profileState = "profileState"  // if true show logged user profile, if false show selected user profile
    case selectedUserIdProfile = "selectedUserIdProfile"
    case profilePicture = "profilePicture"
}

extension String {
    func deletingPrefix(_ prefix: String) -> String {
        guard self.hasPrefix(prefix) else { return self }
        return String(self.dropFirst(prefix.count))
    }
}

extension String {
    func convertToDictionary() -> [String: Any]? {
        if let data = self.data(using: .utf8) {
            do {
                return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            } catch {
                print(error.localizedDescription)
            }
        }
        return nil
    }
}
