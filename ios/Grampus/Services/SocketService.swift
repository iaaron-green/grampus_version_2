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

class SocketService: StompClientLibDelegate {
    
    let storage = StorageService()
    var socketClient = StompClientLib()
    let url = NSURL(string: "ws://10.11.1.83:8081/websocketApp/websocket")!
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
            var chatMessages = [Message]()
            if let room = json["roomURL"] as? String, let messages = json["chatMessages"] as? Array<NSDictionary> {
                for i in messages {
                    var message = Message()
                    message.createDate = i["createDate"] as AnyObject?
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
    
    func stompClientDidDisconnect(client: StompClientLib!) {
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        socketClient.subscribeWithHeader(destination: "/topic/chatListener", withHeader: header)
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
        print("Error Send : \(String(describing: message))")
    }
    
    func serverDidSendPing() {
    }
}
