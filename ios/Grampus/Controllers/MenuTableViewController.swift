//
//  MenuTableViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 5/21/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import SDWebImage

class MenuTableViewController: UITableViewController {
    
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var fullNameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    
    var profilePicture: String?
    var fullName: String?
    var email: String?
    var UserID: Int?
    var jobTitle: String?
    let network = NetworkService()
    let storage = StorageService()
    let imageService = ImageService()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
            
        fetchUserInformation(userId: storage.getUserId()!)
        
        NotificationCenter.default.addObserver(self, selector: #selector(updateImage), name: NSNotification.Name(rawValue: "imageChanged"), object: nil)
        
        
        self.tableView.tableFooterView = UIView(frame: .zero)
        
        self.tableView.separatorStyle = .none
        
        // preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = true
        
        tableView.reloadData()
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    @objc func updateImage(notification: NSNotification) {
        if let image = notification.userInfo?["image"] as? UIImage {
            imageView.image = image
        }
    }
    
    func fetchUserInformation(userId: String) {
        
        if let user = self.storage.getUserProfile() {
            fullNameLabel.text = user.name
            emailLabel.text = user.email
            if let imageData = user.image {
                imageView.image = UIImage(data: imageData)
            } else {
                imageView.image = UIImage(named: "red cross")
            }
        } else {
                    network.fetchUserInformation(userId: userId) { (json) in
                        if let json = json {
                            self.fullName = json["fullName"] as? String
                            self.email = json["email"] as? String
                            self.profilePicture = json["profilePicture"] as? String
                            self.UserID = json["id"] as? Int
                            self.jobTitle = json["jobTitle"] as? String
                            
                            
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
                                self.profilePicture = ""
                            }
                            self.fullNameLabel.text = self.fullName!
                            self.emailLabel.text = self.email!
                            
            //                DispatchQueue.main.async {
            //                    let imageData = self.storage.getProfileImage()
            //                    if imageData != nil {
            //                        self.imageView.image = UIImage(data: self.storage.getProfileImage()!)
            //                    } else {
            //                            let url = URL(string: self.profilePicture!)
            //                            self.imageView.sd_setImage(with: url, placeholderImage: UIImage(named: "red cross"))
            //                    }
            //                }
                            
                            DispatchQueue.main.async {
                                    let url = URL(string: self.profilePicture!)
                                    //self.imageView.sd_setImage(with: url, placeholderImage: UIImage(named: "red cross"))

                                self.imageView.sd_setImage(with: url, placeholderImage: UIImage(named: "red cross")) { (image, error, cache, url) in
                                    var imageData: Data?
                                    if let image = image {
                                        imageData = image.jpegData(compressionQuality: 1)
                                    }
                                    let user = User(image: imageData, name: self.fullName!, profession: self.jobTitle!, email: self.email!)
                                    self.storage.saveUserProfile(user: user)
                                }
                                //let imageData = UIImage(named: "deadliner")?.jpegData(compressionQuality: 1)

                                
                            }


                                
            //                DispatchQueue.global(qos: .userInteractive).async {
            //                    self.imageService.getImage(withURL: self.profilePicture!) { (image) in
            //                        DispatchQueue.main.async {
            //                            if let image = image {
            //                                self.imageView.image = image
            //                            } else {
            //                                self.imageView.image = UIImage(named: "red cross")
            //                            }
            //                            self.tableView.reloadData()
            //                        }
            //                    }
            //                }

                        }
                        
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
