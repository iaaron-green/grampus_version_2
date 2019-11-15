//
//  MenuTableViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 5/21/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire

class MenuTableViewController: UITableViewController {
    
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var fullNameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    
    var profilePicture: String?
    var fullName: String?
    var email: String?
    let network = NetworkService()
    let storage = StorageService()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchUserInformation(userId: storage.getUserId()!)
        
        self.tableView.tableFooterView = UIView(frame: .zero)
        
        self.tableView.separatorStyle = .none
        
        // preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = true
        
        tableView.reloadData()
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    func fetchUserInformation(userId: String) {
        
        network.fetchUserInformation(userId: userId) { (json) in
            if let json = json {
                let user = json["user"] as! NSDictionary
                self.fullName = user["fullName"] as? String
                self.email = user["username"] as? String
                self.profilePicture = json["profilePicture"] as? String
                
                if let unwrappedFullName = self.fullNameLabel {
                    self.fullNameLabel = unwrappedFullName
                } else {
                    self.fullName = "Full Name"
                }
                
                if let unwrappedEmail = self.email {
                    self.email = unwrappedEmail
                } else {
                    self.email = "email"
                }
                
                if let unwrappedProfilePicture = self.profilePicture {
                    self.profilePicture = unwrappedProfilePicture
                } else {
                    self.profilePicture = "some base64"
                }
                
                self.fullNameLabel.text = self.fullName!
                self.emailLabel.text = self.email!
                self.tableView.reloadData()
            }
            
        }
    }
    
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return 7
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if indexPath.row == 1 {
            storage.saveProfileState(state: true)
        }
        if indexPath.row == 6 {
            storage.saveLoggedState(state: false)
            storage.saveUserToken(token: "")
        }
    }
    
}
