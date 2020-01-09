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
    //case dynamicURL = "http://mexanik.ddns.net:6001/api/" //web
     //case dynamicURL = "http://10.11.1.155:8081/api/" //host
//    case dynamicURL = "http://10.11.1.194:8081/api/" //vadim
    case dynamicURL = "http://10.11.1.25:6001/api/" //igor
}

enum UserDefKeys: String {
    case isLoggedIn = "isLoggedIn"
    case token = "token"
    case userId = "userId"
    case likeState = "like"
    case selectedUserId = "selectedUserId"
    case profileState = "profileState"  // if true show logged user profile, if false show selected user profile
    case selectedUserIdProfile = "selectedUserIdProfile"
    case userProfile = "userProfile"
    case isAbleToLike = "isAbleToLike"
    case chatWithCurrentUser = "chatWithCurrentUser"
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

extension UITableView {
    func setEmptyView(title: String, message: String, titleColor: UIColor, messageColor: UIColor, needTransform: Bool) {
        let emptyView = UIView(frame: CGRect(x: self.center.x, y: self.center.y, width: self.bounds.size.width, height: self.bounds.size.height))
        let titleLabel = UILabel()
        let messageLabel = UILabel()
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        messageLabel.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.textColor = titleColor
        titleLabel.font = UIFont(name: "HelveticaNeue-Bold", size: 18)
        messageLabel.textColor = messageColor
        messageLabel.font = UIFont(name: "HelveticaNeue-Regular", size: 17)
        emptyView.addSubview(titleLabel)
        emptyView.addSubview(messageLabel)
        titleLabel.centerYAnchor.constraint(equalTo: emptyView.centerYAnchor).isActive = true
        titleLabel.centerXAnchor.constraint(equalTo: emptyView.centerXAnchor).isActive = true
        messageLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 20).isActive = true
        messageLabel.leftAnchor.constraint(equalTo: emptyView.leftAnchor, constant: 20).isActive = true
        messageLabel.rightAnchor.constraint(equalTo: emptyView.rightAnchor, constant: -20).isActive = true
        titleLabel.text = title
        messageLabel.text = message
        messageLabel.numberOfLines = 0
        messageLabel.textAlignment = .center
        if needTransform {
            emptyView.transform = CGAffineTransform(rotationAngle: -(CGFloat)(Double.pi))
        }
        self.backgroundView = emptyView
    }
    func restore() {
        self.backgroundView = nil
    }
}
