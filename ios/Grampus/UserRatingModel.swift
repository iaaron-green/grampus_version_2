//
//  UserRatingModel.swift
//  Grampus
//
//  Created by student on 12/11/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation

class UserRating {
    var profilePicture: UIImage?
    var fullName: String
    var jobTitle: String?
    var totalLikes: Int
    var totalDisLikes: Int
    var isFollowing: Bool
    var id: String
    
    init(profilePicture: UIImage?, fullName: String, jobTitle: String?, totalLikes: Int, totalDisLikes: Int, isFollowing : Bool, id: String) {
        self.profilePicture = profilePicture
        self.fullName = fullName
        self.jobTitle = jobTitle
        self.totalLikes = totalLikes
        self.totalDisLikes = totalDisLikes
        self.isFollowing = isFollowing
        self.id = id
    }
    
}
