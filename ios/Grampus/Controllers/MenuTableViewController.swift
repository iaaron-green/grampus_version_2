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
    @IBOutlet weak var jobLabel: UILabel!
    @IBOutlet weak var firstCell: UITableViewCell!
    
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
        
        firstCell.layer.insertSublayer(gradient(frame: view.bounds), at:0)
            
        fetchUserInformation(userId: storage.getUserId()!)
        
        NotificationCenter.default.addObserver(self, selector: #selector(loadUser), name: NSNotification.Name(rawValue: "updateUserInfo"), object: nil)
        
        
        self.tableView.tableFooterView = UIView(frame: .zero)
        
        self.tableView.separatorStyle = .none
        
        // preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = true
        
        tableView.reloadData()
    }

    
    @objc func loadUser(notification: NSNotification) {
        if let user = self.storage.getUserProfile() {
        fullNameLabel.text = user.name
        jobLabel.text = user.profession
        if let imageData = user.image {
            imageView.image = UIImage(data: imageData)
        } else {
            imageView.image = UIImage(named: "red cross")
        }
        }
    }
    
    func fetchUserInformation(userId: String) {
        
        if let user = self.storage.getUserProfile() {
            fullNameLabel.text = user.name
            jobLabel.text = user.profession
            if let imageData = user.image {
                imageView.image = UIImage(data: imageData)
            } else {
                imageView.image = UIImage(named: "red cross")
            }
        } else {
            network.fetchUserInformation(userId: userId) { (json, error) in
                if let json = json {
                    self.fullName = json["fullName"] as? String ?? ""
                    self.email = json["email"] as? String ?? ""
                    self.profilePicture = json["profilePicture"] as? String ?? ""
                    self.UserID = json["id"] as? Int
                    self.jobTitle = json["jobTitle"] as? String ?? ""
                    
                    self.fullNameLabel.text = self.fullName!
                    self.jobLabel.text = self.jobTitle!
                    
                    DispatchQueue.main.async {
                        let url = URL(string: self.profilePicture!)
                        
                        self.imageView.sd_setImage(with: url, placeholderImage: UIImage(named: "red cross")) { (image, error, cache, url) in
                            var imageData: Data?
                            if let image = image {
                                imageData = image.jpegData(compressionQuality: 1)
                            }
                            let user = User(image: imageData, name: self.fullName!, profession: self.jobTitle!, email: self.email!)
                            self.storage.saveUserProfile(user: user)
                        }
                        
                    }
                } else {
                    print(error)
                }
            }
        }
        
        
    }
    
    
    //Gradient method for first cell
    func gradient(frame:CGRect) -> CAGradientLayer {
          let layer = CAGradientLayer()
          layer.frame = frame
          layer.startPoint = CGPoint(x: 0, y: 0.5)
          layer.endPoint = CGPoint(x: 1, y: 0.5)
          layer.colors = [
              UIColor.black.cgColor,UIColor.darkGray.cgColor]
          return layer
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
