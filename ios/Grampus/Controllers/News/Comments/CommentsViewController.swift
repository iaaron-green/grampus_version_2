//
//  CommentsTableViewController.swift
//  Grampus
//
//  Created by student on 12/10/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON
import SDWebImage
import SVProgressHUD

class CommentsViewController: RootViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var stackViewBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var commentTextField: UITextField!
    
    var commentsArray = [JSON]()
    var newsID = 0
    var profileID = 0
    let network = NetworkService()
    let storage = StorageService()

    override func viewDidLoad() {
        super.viewDidLoad()
        fetchComments()
        dismissKeyboardOnTap()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.separatorStyle = .none
        tableView.register(UINib(nibName: "CommentTableViewCell", bundle: nil), forCellReuseIdentifier: "commentCell")
        
        tableView.estimatedRowHeight = 200
        tableView.rowHeight = UITableView.automaticDimension
    }
    
    
    func fetchComments() {
        network.fetchComments(newsID: newsID) { (comments) in
            if let comments = comments {
                print(comments)
                self.commentsArray = [JSON]()
                for i in 0..<comments.count {
                    self.commentsArray.append(comments[i])
                }
                self.tableView.reloadData()
            } else {
                print("Error")
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
    
    @IBAction func backButtonPressed(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    
    
    @IBAction func sendPressed(_ sender: UIButton) {
        let comment = commentTextField.text!
        network.sendComment(comment: comment, id: newsID) { (success) in
            if success {
                self.commentTextField.text = ""
                self.fetchComments()
                SVProgressHUD.showSuccess(withStatus: "Success!")
            } else {
                SVProgressHUD.showError(withStatus: "Error!")
            }
        }
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return commentsArray.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        profileID = commentsArray[indexPath.row]["id"].int ?? 0
        print(profileID)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "commentCell", for: indexPath) as! CommentTableViewCell
        
        var image = ""
        var name = ""
        var date = ""
        var text = ""
        
        image = self.commentsArray[indexPath.row]["picture"].string ?? ""
        name = self.commentsArray[indexPath.row]["fullName"].string ?? "Barack Obama"
        date = self.commentsArray[indexPath.row]["date"].string ?? ""
        text = self.commentsArray[indexPath.row]["text"].string ?? ""
        
        DispatchQueue.main.async {
            let imageURL = URL(string: image)
            cell.commentImageView.sd_setImage(with: imageURL, placeholderImage: UIImage(named: "red cross"))
        }
        
        cell.commentNameLabel.text = name
        cell.commentDateLabel.text = date
        cell.commentLabel.text = text
        
        let userTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(userTapped(tapGestureRecognizer:)))
        userTapGestureRecognizer.cancelsTouchesInView = false
        cell.commentNameLabel.addGestureRecognizer(userTapGestureRecognizer)
        cell.commentImageView.addGestureRecognizer(userTapGestureRecognizer)
        return cell
    }
    
    @objc func userTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        storage.saveSelectedUserId(selectedUserId: String(describing: profileID))
        storage.saveProfileState(state: false)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            self.performSegue(withIdentifier: "goToProfile", sender: self)
        }
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
        stackViewBottomConstraint.constant = 0
    }
    
    @objc func keyboardWillChange(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if commentTextField.isFirstResponder {
                stackViewBottomConstraint.constant = keyboardSize.height - 20
            }
        }
    }
}
