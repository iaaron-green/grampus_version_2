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

class ChatViewController: RootViewController, UITableViewDelegate, UITableViewDataSource, StompClientLibDelegate, SWRevealViewControllerDelegate, UITableViewDataSourcePrefetching {
    
    var socket = SocketService()
    let storage = StorageService()
    var socketClient = StompClientLib()
//    let url = NSURL(string: "ws://10.11.1.194:8081/websocketApp/websocket")!
    let url = NSURL(string: "ws://10.11.1.25:6001/websocketApp/websocket")!

    var header = [String: String]()
    var titleChat = ""
    var userId = ""
    var roomURL = ""
    var roomId = ""
    var messagesArray = [Message]()
    var page = 1
    var isFetch = false
    var limit = 0
    var needNewConnect = true
    var backButtonEnabled = true

    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var messageTextField: UITextField!
    @IBOutlet weak var stackViewBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var menuBarButton: UIBarButtonItem!
    @IBOutlet weak var backButton: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: "MessageSentTableViewCell", bundle: nil), forCellReuseIdentifier: "MessageSentCell")
        tableView.register(UINib(nibName: "MessageReceivedTableViewCell", bundle: nil), forCellReuseIdentifier: "MessageReceivedCell")
        tableView.delegate = self
        tableView.dataSource = self
        tableView.prefetchDataSource = self
        tableView.backgroundColor = .clear
        view.backgroundColor = #colorLiteral(red: 0.1137254902, green: 0.1294117647, blue: 0.1411764706, alpha: 1)
        tableView.transform = CGAffineTransform(rotationAngle: -(CGFloat)(Double.pi))
        tableView.scrollIndicatorInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: tableView.bounds.size.width - 8.0)
        NotificationCenter.default.addObserver(self, selector: #selector(updateSocketInfo), name: NSNotification.Name(rawValue: "chat"), object: nil)
        navigationBar.topItem?.title = titleChat
        dismissKeyboardOnTap()
        header = ["Authorization":"Bearer \(storage.getTokenString()!)"]
        if let myDelegate = UIApplication.shared.delegate as? AppDelegate {
            socket = myDelegate.socket
        }
        
        if revealViewController() != nil {
            menuBarButton.target = self.revealViewController()
            menuBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
            self.revealViewController()?.delegate = self
        }
        
        if backButtonEnabled {
            backButton.isEnabled = true
        } else {
            backButton.isEnabled = false
            backButton.title = ""
        }
    }
    
    func revealController(_ revealController: SWRevealViewController!, willMoveTo position: FrontViewPosition) {
        dismissKeyboard()
    }
    
    @objc func updateSocketInfo(notification: NSNotification){
        if let room = notification.userInfo?["room"] as? String {
            SVProgressHUD.dismiss()
            roomURL = room
            roomId = roomURL
            let remove = "/topic/chat"
            if let range = roomId.range(of: remove) {
               roomId.removeSubrange(range)
            }
         }
        if let messages = notification.userInfo?["messages"] as? Array<Message> {
            for i in messages {
                if !messagesArray.contains(i) {
                    messagesArray.append(i)
                }
            }
        }
        isEmtyCheck()
        tableView.reloadData()
        if needNewConnect {
            connectToSocket()
            needNewConnect = false
        }
    }
    
    func connectToSocket() {
        let request = NSMutableURLRequest(url: url as URL)
        request.setValue("Bearer \(storage.getTokenString()!)", forHTTPHeaderField: "Authorization")
        socketClient.openSocketWithURLRequest(request: request, delegate: self, connectionHeaders: header)
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        fetchFirstMessages()
        storage.saveCurrentUserChatId(userId: userId)
    }
    
    func fetchFirstMessages() {
        let dict: [String: String] = [
            "targetUserId": userId,
            "chatType": "PRIVATE",
            "roomId" : "0",
            "page" : "0",
            "size" : "20"
        ]
        let encoder = JSONEncoder()
        if let jsonData = try? encoder.encode(dict) {
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                socket.socketClient.sendMessage(message: jsonString, toDestination: "/app/chat.chatInit", withHeaders: header, withReceipt: nil)
            }
        }
    }
    
    func fetchNewMessages(page: Int) {
        let dict: [String: String] = [
            "roomId" : roomId,
            "page" : String(describing: page),
            "size" : "20"
        ]
        let encoder = JSONEncoder()
        if let jsonData = try? encoder.encode(dict) {
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                socketClient.sendMessage(message: jsonString, toDestination: "/app/chat.getMessages", withHeaders: header, withReceipt: nil)
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setUpNotifications()
        SVProgressHUD.show()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        removeNotifications()
        storage.saveCurrentUserChatId(userId: "")
    }
    
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        tableView.restore()
        parseMessages(json: jsonBody)
        
    }
    
    func parseMessages(json: AnyObject?) {
        if let jsonDict = json as? NSDictionary {
            var message = Message()
            message.createDate = jsonDict["createDate"] as? Int ?? 0
            message.profilePicture = jsonDict["profilePicture"] as? String ?? ""
            message.message = jsonDict["message"] as? String ?? ""
            message.profileFullName = jsonDict["profileFullName"] as? String ?? ""
            messagesArray.insert(message, at: 0)
            let indexPath = IndexPath(row: 0, section: 0)
            tableView.insertRows(at: [indexPath], with: .bottom)
        } else if let jsonArray = json as? Array<NSDictionary> {
            limit = messagesArray.count
            for i in jsonArray {
                var message = Message()
                message.createDate = i["createDate"] as? Int
                message.message = i["message"] as? String
                message.profileFullName = i["profileFullName"] as? String
                message.profileId = i["profileId"] as? String
                message.profilePicture = i["profilePicture"] as? String
                if !messagesArray.contains(message) {
                    messagesArray.append(message)
                }
                tableView.reloadData()
            }
        }
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.row == messagesArray.count - 1 {
            isFetch = true
        } else {
            isFetch = false
        }
    }
    
    func tableView(_ tableView: UITableView, prefetchRowsAt indexPaths: [IndexPath]) {
        if isFetch {
            fetchNewMessages(page: self.page)
            if limit < messagesArray.count {
                page += 1
            } else if limit == messagesArray.count {
                tableView.prefetchDataSource = nil
            }
        }
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
        if !message.isEmpty {
            let dic = ["roomId": roomId,"textMessage": message,"targetUserId": userId]
            if !messagesArray.isEmpty {
                let indexPath = IndexPath(row: 0, section: 0)
                tableView.scrollToRow(at: indexPath, at: .top, animated: true)
            }
            let encoder = JSONEncoder()
            if let jsonData = try? encoder.encode(dic) {
                if let jsonString = String(data: jsonData, encoding: .utf8) {
                    socketClient.sendMessage(message: jsonString, toDestination: "/app/chat.sendMessage", withHeaders: header, withReceipt: nil)
                }
            }
            messageTextField.text = ""
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messagesArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let image = messagesArray[indexPath.row].profilePicture ?? ""
        let name = messagesArray[indexPath.row].profileFullName ?? ""
        let date = messagesArray[indexPath.row].createDate ?? 0
        let messsage = messagesArray[indexPath.row].message ?? ""
        
        let dateToShow = Date(timeIntervalSince1970: (Double(date) / 1000.0))
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "HH:mm:ss d/MM/yyyy"
        
        if name == storage.getUserProfile()?.name {
            let cell = tableView.dequeueReusableCell(withIdentifier: "MessageSentCell", for: indexPath) as! MessageSentTableViewCell
            
            let imageURL = URL(string: image)
            cell.avatarImageView.sd_setImage(with: imageURL, placeholderImage: UIImage(named: "red cross"))
            cell.nameLabel.text = name
            cell.dateLabel.text = dateFormatter.string(from: dateToShow)
            cell.messageLabel.text = messsage
            cell.transform = CGAffineTransform(rotationAngle: CGFloat(Double.pi))
            
            let profileImageTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(selfProfileTapped(tapGestureRecognizer:)))
            cell.avatarImageView.addGestureRecognizer(profileImageTapGestureRecognizer)

            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "MessageReceivedCell", for: indexPath) as! MessageReceivedTableViewCell
            
            let imageURL = URL(string: image)
            cell.avatarImageView.sd_setImage(with: imageURL, placeholderImage: UIImage(named: "red cross"))
            cell.nameLabel.text = name
            cell.dateLabel.text = dateFormatter.string(from: dateToShow)
            cell.messageLabel.text = messsage
            cell.transform = CGAffineTransform(rotationAngle: CGFloat(Double.pi))
            let profileImageTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(profileTapped(tapGestureRecognizer:)))
            cell.avatarImageView.addGestureRecognizer(profileImageTapGestureRecognizer)
            
            return cell
        }    
    }
    
    @objc func profileTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            self.storage.saveSelectedUserId(selectedUserId: self.userId)
            self.storage.saveProfileState(state: false)
            self.performSegue(withIdentifier: "goToProfile", sender: self)
        }
    }
    
    @objc func selfProfileTapped(tapGestureRecognizer: UITapGestureRecognizer) {
            storage.saveProfileState(state: true)
            performSegue(withIdentifier: "goToProfile", sender: self)
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
        UIView.animate(withDuration: 0.5, animations: {
            self.view.layoutIfNeeded()
        })
       }
       
       @objc func keyboardWillChange(notification: NSNotification) {
           if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
               if messageTextField.isFirstResponder {
                   stackViewBottomConstraint.constant = -keyboardSize.height - 10
                UIView.animate(withDuration: 0.5, animations: {
                    self.view.layoutIfNeeded()
                })
               }
           }
       }

    
    func isEmtyCheck() {
        if self.messagesArray.isEmpty {
            self.tableView.setEmptyView(title: "No messages yet", message: "They will appear here", titleColor: .lightGray, messageColor: .lightGray, needTransform: true)
        } else {
            self.tableView.restore()
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
          return .lightContent
    }
}
