//
//  NewsTableTableViewController.swift
//  Grampus
//
//  Created by student on 12/4/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON
import SVProgressHUD
import SDWebImage

class NewsTableTableViewController: UITableViewController, UINavigationControllerDelegate {
    
    
    @IBOutlet weak var navegationBar: UINavigationBar!
    @IBOutlet weak var leftBarButton: UIBarButtonItem!
    @IBOutlet weak var addButton: UIBarButtonItem!
    
    let network = NetworkService()
    let storage = StorageService()
    let imageService = ImageService()
    var newsArray = [JSON]()
    var newsID = 0
    var profileID = 0

    let myRefreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.tintColor = .white
        refreshControl.addTarget(self, action: #selector(pullToRefresh(sender:)), for: .valueChanged)
        return refreshControl
    }()
                
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchNews()
        tableView.register(UINib(nibName: "NewsTableViewCell", bundle: nil), forCellReuseIdentifier: "newsTableCell")
        tableView.estimatedRowHeight = 500
        tableView.rowHeight = UITableView.automaticDimension
        
        tableView.separatorStyle = .none
        tableView.backgroundColor = .clear
        view.backgroundColor = #colorLiteral(red: 0.1125021651, green: 0.1299118698, blue: 0.1408866942, alpha: 1)
        tableView.refreshControl = myRefreshControl

        
        if revealViewController() != nil {
            leftBarButton.target = self.revealViewController()
            leftBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
        }
    }

    
    override var preferredStatusBarStyle: UIStatusBarStyle {
          return .lightContent
    }
    
    @objc func pullToRefresh(sender: UIRefreshControl) {
        fetchNews()
        sender.endRefreshing()
        
    }
    
    func fetchNews() {
        network.fetchNews { (news) in
            if let news = news {
                self.newsArray = [JSON]()
                print(news)
                SVProgressHUD.dismiss()
                for i in 0..<news.count {
                    self.newsArray.append(news[i])
                }
                self.tableView.reloadData()
            } else {
                print("Error")
            }
        }
    }
    
    

    // MARK: - Table view data source


    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return newsArray.count
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        newsID = newsArray[indexPath.row]["id"].int ?? 0
        profileID = newsArray[indexPath.row]["profileID"].int ?? 0
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "newsTableCell", for: indexPath) as! NewsTableViewCell
        
        var nameCell = ""
        var userPictureCell = ""
        var dateCell = ""
        var titleCell = ""
        var bodyCell = ""
        var imageCell = ""
        var countOfComment = 0
            
        nameCell = self.newsArray[indexPath.row]["nameProfile"].string ?? ""
        userPictureCell = self.newsArray[indexPath.row]["imgProfile"].string ?? ""
        dateCell = self.newsArray[indexPath.row]["date"].string ?? ""
        titleCell = self.newsArray[indexPath.row]["title"].string ?? ""
        bodyCell = self.newsArray[indexPath.row]["content"].string ?? ""
        imageCell = self.newsArray[indexPath.row]["picture"].string?.replacingOccurrences(of: "\\", with: "") ?? ""
        countOfComment = self.newsArray[indexPath.row]["countOfComment"].int ?? 0
        let urlProfile = URL(string: userPictureCell.replacingOccurrences(of: "\\", with: ""))
        let urlNews = URL(string: imageCell.replacingOccurrences(of: "\\", with: ""))
            
        cell.nameLabel.text = nameCell
        cell.dateLabel.text = dateCell
        cell.titleLabel.text = titleCell
        cell.bodyLabel.text = bodyCell
        cell.commentsLabel.text = "Comments: \(countOfComment)"
            
        DispatchQueue.main.async {
            cell.avatarImageView.sd_setImage(with: urlProfile, placeholderImage: UIImage(named: "red cross"))
            cell.newsImageView.sd_setImage(with: urlNews, placeholderImage: nil, options: [], completed: { [weak cell] (image, error, cache, url) in
                if let image = image {
                    cell?.setCustomImage(image: image)
                }
                else {
                    cell?.setCustomImage(image: UIImage(named: "2")!)
                }
                self.tableView.beginUpdates()
                self.tableView.endUpdates()

            })
        }
        
        let commentTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(commentTapped(tapGestureRecognizer:)))
        let profileTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(profileTapped(tapGestureRecognizer:)))
        let profileImageTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(profileTapped(tapGestureRecognizer:)))
        profileImageTapGestureRecognizer.cancelsTouchesInView = false
        profileTapGestureRecognizer.cancelsTouchesInView = false
        commentTapGestureRecognizer.cancelsTouchesInView = false
        cell.nameLabel.addGestureRecognizer(profileTapGestureRecognizer)
        cell.avatarImageView.addGestureRecognizer(profileImageTapGestureRecognizer)
        cell.commentsLabel.addGestureRecognizer(commentTapGestureRecognizer)
        cell.layoutIfNeeded()
        return cell
    }
    
    @objc func commentTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            self.performSegue(withIdentifier: "goToComments", sender: self)
        }
    }
    
    @objc func profileTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            self.performSegue(withIdentifier: "goToProfile", sender: self)
        }
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToComments" {
            let vc = segue.destination as? CommentsViewController
            vc?.newsID = newsID
        } else if segue.identifier == "goToProfile" {
            storage.saveSelectedUserId(selectedUserId: String(describing: profileID))
            storage.saveProfileState(state: false)
        }
    }
    
}
