//
//  ChatViewController.swift
//  Grampus
//
//  Created by student on 12/13/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import StompClientLib
class ChatViewController: UIViewController, StompClientLibDelegate {

    

    var socketClient = StompClientLib()
    let url = NSURL(string: "ws://10.11.1.83:8080/websocketApp/websocket")!

    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var messageTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        socketClient.openSocketWithURLRequest(request: NSURLRequest(url: url as URL) , delegate: self)
//        socketClient.sendMessage(message: "StompClientLib HI!!!", toDestination: "/app/chat.newUser", withHeaders: nil, withReceipt: nil)


    }
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
    print("Destination : \(destination)")
    print("JSON Body : \(String(describing: jsonBody))")
    print("String Body : \(stringBody ?? "nil")")
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
    print("Socket is Disconnected")
    }
    
    func stompClientJSONBody(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
      print("DESTIONATION : \(destination)")
      print("String JSON BODY : \(String(describing: jsonBody))")
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
    print("Socket is connected")
    // Stomp subscribe will be here!
//    socketClient.subscribe(destination: "/topic/javainuse")
        socketClient.subscribeWithHeader(destination: "/topic/javainuse", withHeader: ["username": "Taras"])
    // Note : topic needs to be a String object
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
      print("Receipt : \(receiptId)")
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
      print("Error Send : \(String(describing: message))")
    }
    
    func serverDidSendPing() {
      print("Server ping")
    }

    

    @IBAction func BackButtonPressed(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    @IBAction func sendButtonPressed(_ sender: UIButton) {
        
        socketClient.sendMessage(message: "StompClientLib Test", toDestination: "/topic/javainuse", withHeaders: ["username": "Taras"], withReceipt: nil)
        
    }
    
}
