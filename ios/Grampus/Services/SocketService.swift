//
//  SocketService.swift
//  Grampus
//
//  Created by student on 12/20/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation
import UIKit
import SwiftyJSON
import StompClientLib
import NotificationBannerSwift
import SDWebImage

class SocketService: StompClientLibDelegate {

    
    let storage = StorageService()
    var socketClient = StompClientLib()
//    let url = NSURL(string: "ws://10.11.1.194:8081/websocketApp/websocket")!
    let url = NSURL(string: "ws://10.11.1.25:6001/websocketApp/websocket")!
    var header = [String: String]()

    
    func connectToSocket() {
        header = ["Authorization":"Bearer \(storage.getTokenString()!)"]
        let request = NSMutableURLRequest(url: url as URL)
        request.setValue("Bearer \(storage.getTokenString()!)", forHTTPHeaderField: "Authorization")
        socketClient.openSocketWithURLRequest(request: request, delegate: self, connectionHeaders: header)
    }
    
    func disconnect() {
        socketClient.disconnect()
    }
    
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        if let json = jsonBody as? NSDictionary {
            if json["profileFullName"] != nil {
                let imageString = json["profilePicture"] as? String ?? ""
                let name = json["profileFullName"] as? String ?? ""
                let message = json["message"] as? String ?? ""
                let userId = json["profileId"] as? Int ?? 0
                if storage.chatWithCurrentUser() != String(describing: userId) {
                    showBanner(imageString: imageString, name: name, message: message, userId: userId)
                }
            }
            var chatMessages = [Message]()
            if let room = json["roomURL"] as? String, let messages = json["chatMessages"] as? Array<NSDictionary> {
                for i in messages {
                    var message = Message()
                    message.createDate = i["createDate"] as? Int
                    message.message = i["message"] as? String
                    message.profileFullName = i["profileFullName"] as? String
                    message.profileId = i["profileId"] as? String
                    message.profilePicture = i["profilePicture"] as? String
                    chatMessages.append(message)
                }
                let sendDict:[String: Any] = ["room": room, "messages": chatMessages]
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chat"), object: nil, userInfo: sendDict)
            }
        }
    }
    
    func showBanner(imageString: String, name: String, message: String, userId: Int) {
        let imageView = UIImageView(image: UIImage(named: "background"))
        imageView.frame = CGRect(x: 0, y: 5, width: 40, height: 40)
        let leftView = UIView()
        leftView.addSubview(imageView)
        let imageURL = URL(string: imageString)
        imageView.layer.cornerRadius = (imageView.frame.size.height)/2
        imageView.contentMode = .scaleAspectFill
        leftView.layoutIfNeeded()
        imageView.clipsToBounds = true
        imageView.sd_setImage(with: imageURL, placeholderImage: UIImage(named: "red cross"))
        let banner = FloatingNotificationBanner(title: name, subtitle: message, leftView: leftView, style: .success)
        banner.backgroundColor = #colorLiteral(red: 0, green: 0.6274509804, blue: 0.9529411765, alpha: 1)
        banner.show(cornerRadius: 8, shadowBlurRadius: 16, shadowEdgeInsets: UIEdgeInsets(top: 8, left: 8, bottom: 0, right: 8))
        banner.onTap = {
            if let myDelegate = UIApplication.shared.delegate as? AppDelegate {
                myDelegate.goToChat(id: String(describing: userId), name: name, buttonState: false)
            }
        }
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        if let userId = storage.getUserId() {
            socketClient.subscribeWithHeader(destination: "/topic/chatListener\(userId)", withHeader: header)
        }
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
        print("Error Send : \(String(describing: message))")
    }
    
    func serverDidSendPing() {
    }
}
