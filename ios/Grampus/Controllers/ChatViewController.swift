//
//  ChatViewController.swift
//  Grampus
//
//  Created by student on 12/13/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import StompClientLib
class ChatViewController: UIViewController, StompClientLibDelegate {

    
    let storage = StorageService()
    var socketClient = StompClientLib()
    let url = NSURL(string: "ws://10.11.1.83:8080/websocketApp/websocket")!
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var messageTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        socketClient.openSocketWithURLRequest(request: NSURLRequest(url: url as URL) , delegate: self)
    }
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
    print("Destination : \(destination)")
//    print("JSON Body : \(String(describing: jsonBody))")
    print("String Body : \(stringBody ?? "nil")")
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
    print("Socket is Disconnected")
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
    print("Socket is connected")
        
    let header: HTTPHeaders = [
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        
    // Stomp subscribe will be here!
        let dict: [String: String] = [
            "type": "newUser",
            "content": "",
            "sender": "Taras"
        ]
        
        let encoder = JSONEncoder()
        if let jsonData = try? encoder.encode(dict) {
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                socketClient.sendMessage(message: jsonString, toDestination: "/app/chat.newUser", withHeaders: header, withReceipt: nil)
            }
        }

        socketClient.subscribe(destination: "/topic/javainuse")
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
      print("Receipt : \(receiptId)")
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
      print("Error Send : \(String(describing: message))")
    }
    
    func serverDidSendPing() {
//      print("Server ping")
    }

    

    @IBAction func BackButtonPressed(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    @IBAction func sendButtonPressed(_ sender: UIButton) {
        let message = messageTextField.text!
        let header: HTTPHeaders = [
            "Authorization": "Bearer \(storage.getTokenString()!)"
        ]
        let dic = ["type":"CHAT","content":message,"sender":"Taras"]
        let encoder = JSONEncoder()
        if let jsonData = try? encoder.encode(dic) {
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                socketClient.sendMessage(message: jsonString, toDestination: "/app/chat.newUser", withHeaders: header, withReceipt: nil)
            }
        }
        
    }
}
