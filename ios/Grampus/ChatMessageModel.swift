//
//  ChatMessageModel.swift
//  Grampus
//
//  Created by student on 12/24/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation

struct Message: Equatable {
    
    var profilePicture: String?
    var profileFullName: String?
    var createDate: Int?
    var message: String?
    var profileId: String?
    
    static func == (lhs: Message, rhs: Message) -> Bool {
        return lhs.profilePicture == rhs.profilePicture && lhs.profileFullName == rhs.profileFullName && lhs.createDate == rhs.createDate && lhs.message == rhs.message && lhs.profileId == rhs.profileId
    }
}
