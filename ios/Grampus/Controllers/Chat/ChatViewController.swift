//
//  ChatViewController.swift
//  Grampus
//
//  Created by student on 12/13/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import StompClientLib
import SwiftyJSON
import SDWebImage
import SVProgressHUD

class ChatViewController: RootViewController, UITableViewDelegate, UITableViewDataSource, StompClientLibDelegate {
    
    var socket = SocketService()
    let storage = StorageService()
    var socketClient = StompClientLib()
    let url = NSURL(string: "ws://10.11.1.83:8081/websocketApp/websocket")!
    var header = [String: String]()
    var titleChat = ""
    var userId = ""
    var roomURL = ""
    var roomId = ""
    var messagesArray = [Message]()
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var messageTextField: UITextField!
    @IBOutlet weak var stackViewBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var navigationBar: UINavigationBar!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: "MessageSentTableViewCell", bundle: nil), forCellReuseIdentifier: "MessageSentCell")
        tableView.register(UINib(nibName: "MessageReceivedTableViewCell", bundle: nil), forCellReuseIdentifier: "MessageReceivedCell")
        tableView.delegate = self
        tableView.dataSource = self

        
        NotificationCenter.default.addObserver(self, selector: #selector(updateSocketInfo), name: NSNotification.Name(rawValue: "chat"), object: nil)
        navigationBar.topItem?.title = titleChat
        dismissKeyboardOnTap()
        header = ["Authorization":"Bearer \(storage.getTokenString()!)"]
        if let myDelegate = UIApplication.shared.delegate as? AppDelegate {
            socket = myDelegate.socket
        }
    }
    
    @objc func updateSocketInfo(notification: NSNotification){
        if let room = notification.userInfo?["room"] as? String {
            roomURL = room
            roomId = roomURL
            let remove = "/topic/chat"
            if let range = roomId.range(of: remove) {
               roomId.removeSubrange(range)
            }
         }
        if let messages = notification.userInfo?["messages"] as? Array<Message> {
            messagesArray = messages
        }
        tableView.reloadData()
        tableView.scrollToBottom()
        connectToSocket()
    }
    
    func connectToSocket() {
        let request = NSMutableURLRequest(url: url as URL)
        request.setValue("Bearer \(storage.getTokenString()!)", forHTTPHeaderField: "Authorization")
        socketClient.openSocketWithURLRequest(request: request, delegate: self, connectionHeaders: header)
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        let dict: [String: String] = [
            "targetUserId": userId,
            "chatType": "PRIVATE",
            "roomId" : "0",
            "page" : "2",
            "size" : "20"
        ]
        let encoder = JSONEncoder()
        if let jsonData = try? encoder.encode(dict) {
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                socket.socketClient.sendMessage(message: jsonString, toDestination: "/app/chat.chatInit", withHeaders: header, withReceipt: nil)
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setUpNotifications()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        removeNotifications()
    }
    
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        fetchMessages(json: jsonBody)
    }
    
    func fetchMessages(json: AnyObject?) {
        if let jsonDict = json as? NSDictionary {
            var message = Message()
//            message.createDate = "Some date"
            message.profilePicture = jsonDict["profilePicture"] as? String ?? ""
            message.message = jsonDict["message"] as? String ?? ""
            message.profileFullName = jsonDict["profileFullName"] as? String ?? ""
            messagesArray.append(message)
            isEmtyCheck()
            tableView.reloadData()
//            tableView.layoutIfNeeded()
//            tableView.setContentOffset(CGPoint(x: 0, y: tableView.contentSize.height - tableView.frame.height), animated: true)
            tableView.scrollToBottom()
        }
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        print(indexPath)
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
        socketClient.unsubscribe(destination: roomURL)
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        socketClient.subscribeWithHeader(destination: roomURL, withHeader: header)
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
      print("Receipt : \(receiptId)")
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
      print("Error Send : \(String(describing: message))")
    }
    
    func serverDidSendPing() {
    }

    
    @IBAction func BackButtonPressed(_ sender: UIBarButtonItem) {
        socketClient.disconnect()
        self.dismiss(animated: true, completion: nil)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            SVProgressHUD.dismiss()
        }
    }
    
    
    @IBAction func sendButtonPressed(_ sender: UIButton) {
        let message = messageTextField.text!
        let dic = ["roomId": roomId,"textMessage": message]
        
        let encoder = JSONEncoder()
        if let jsonData = try? encoder.encode(dic) {
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                socketClient.sendMessage(message: jsonString, toDestination: "/app/chat.sendMessage", withHeaders: header, withReceipt: nil)
            }
        }
        messageTextField.text = ""
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messagesArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let image = messagesArray[indexPath.row].profilePicture ?? ""
        let name = messagesArray[indexPath.row].profileFullName ?? ""
//        let date = messagesArray[indexPath.row].createDate ?? ""
        let messsage = messagesArray[indexPath.row].message ?? ""
        
        if name == storage.getUserProfile()?.name {
            let cell = tableView.dequeueReusableCell(withIdentifier: "MessageSentCell", for: indexPath) as! MessageSentTableViewCell
            
            let imageURL = URL(string: image)
            cell.avatarImageView.sd_setImage(with: imageURL, placeholderImage: UIImage(named: "red cross"))
            
            cell.nameLabel.text = name
//            cell.dateLabel.text = date
            cell.messageLabel.text = messsage

            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "MessageReceivedCell", for: indexPath) as! MessageReceivedTableViewCell
            
            let imageURL = URL(string: image)
            cell.avatarImageView.sd_setImage(with: imageURL, placeholderImage: UIImage(named: "red cross"))
            
            cell.nameLabel.text = name
//            cell.dateLabel.text = date
            cell.messageLabel.text = messsage

            return cell
        }
    
//        return cell
    }
    
    
       
       // Notifications for moving view when keyboard appears.
       func setUpNotifications() {
           NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
           
           NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
       }
       
       // Removing notifications.
       func removeNotifications() {
           NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
           NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
       }
       
       @objc func keyboardWillHide() {
           stackViewBottomConstraint.constant = -5
       }
       
       @objc func keyboardWillChange(notification: NSNotification) {
           if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
               if messageTextField.isFirstResponder {
                   stackViewBottomConstraint.constant = -keyboardSize.height
               }
           }
       }
    
    func isEmtyCheck() {
        if self.messagesArray.isEmpty {
            self.tableView.setEmptyView(title: "No messages yet", message: "They will appear here", titleColor: .lightGray, messageColor: .lightGray)
        } else {
            self.tableView.restore()
        }
    }
}

extension UITableView {
    func scrollToBottom(animated: Bool = true) {
        let section = self.numberOfSections
        if section > 0 {
            let row = self.numberOfRows(inSection: section - 1)
            if row > 0 {
                self.scrollToRow(at: IndexPath(row: row-1, section: section-1), at: .bottom, animated: animated)
            }
        }
    }
}
