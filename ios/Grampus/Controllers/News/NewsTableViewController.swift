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
    let imageService = ImageService()
    var newsArray = [JSON]()
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
//                print(news)
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
    
    var cachedImages = [UIImage]()

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "newsTableCell", for: indexPath) as! NewsTableViewCell
        
        var nameCell = ""
        var userPictureCell = ""
        var dateCell = ""
        var titleCell = ""
        var bodyCell = ""
        var imageCell = ""
        
        DispatchQueue.main.async {
            
            
            nameCell = self.newsArray[indexPath.row]["nameProfile"].string ?? ""
            userPictureCell = self.newsArray[indexPath.row]["imgProfile"].string ?? ""
            dateCell = self.newsArray[indexPath.row]["date"].string ?? ""
            titleCell = self.newsArray[indexPath.row]["title"].string ?? ""
            bodyCell = self.newsArray[indexPath.row]["content"].string ?? ""
            imageCell = self.newsArray[indexPath.row]["picture"].string?.replacingOccurrences(of: "\\", with: "") ?? ""
            

            let urlProfile = URL(string: userPictureCell)
            let urlNews = URL(string: imageCell)
            cell.avatarImageView.sd_setImage(with: urlProfile, placeholderImage: UIImage(named: "red cross"))
            cell.nameLabel.text = nameCell
            cell.dateLabel.text = dateCell
            cell.titleLabel.text = titleCell
            cell.bodyLabel.text = bodyCell
            cell.newsImageView.sd_setImage(with: urlNews, placeholderImage: nil, options: [], completed: { [weak cell] (image, error, cache, url) in
                if let image = image {
                    cell?.setCustomImage(image: image)
                }
                else {
                    cell?.setCustomImage(image: UIImage(named: "2")!)
                }
                
                self.tableView.beginUpdates()
                print("UPDATES")
                self.tableView.endUpdates()
//                self.tableView.reloadRows(
//                    at: [indexPath],
//                    with: .fade)
                
                
            })
        }
        
        return cell
    }
}
