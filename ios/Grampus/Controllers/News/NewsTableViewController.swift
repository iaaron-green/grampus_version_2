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
    var newsArray = [JSON]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchNews()
        
        tableView.register(UINib(nibName: "NewsTableViewCell", bundle: nil), forCellReuseIdentifier: "newsTableCell")
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 100
        
        tableView.separatorStyle = .none
        tableView.backgroundColor = .clear
        view.backgroundColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)

        
        if revealViewController() != nil {
            leftBarButton.target = self.revealViewController()
            leftBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
        }

    }
    
    func fetchNews() {
        network.fetchNews { (news) in
            if let news = news {
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
            imageCell = self.newsArray[indexPath.row]["picture"].string ?? ""
            

            let urlProfile = URL(string: userPictureCell)
            let urlNews = URL(string: imageCell)
            cell.avatarImageView.sd_setImage(with: urlProfile, placeholderImage: UIImage(named: "red cross"))
            cell.nameLabel.text = nameCell
            cell.dateLabel.text = dateCell
            cell.titleLabel.text = titleCell
            cell.bodyLabel.text = bodyCell
            
            var cellFrame = cell.frame.size
            cellFrame.height =  cellFrame.height - 15
            cellFrame.width =  cellFrame.width - 15

//            cell.newsImageView.sd_setImage(with: urlNews, placeholderImage: nil, options: [], completed: { (image, error, cache, url) in
//                if let image = image {
//                    cell.newsImageHeight.constant = self.getAspectRatioAccordingToiPhones(cellImageFrame: cellFrame,downloadedImage: image)
//                } else {
//                    cell.newsImageHeight.constant = 0
//                }
//            })
            
            cell.newsImageView.sd_setImage(with: urlNews)
        }
        
        return cell
    }
    func getAspectRatioAccordingToiPhones(cellImageFrame: CGSize, downloadedImage: UIImage) -> CGFloat {
        let widthOffset = downloadedImage.size.width - cellImageFrame.width
        let widthOffsetPercentage = (widthOffset*100)/downloadedImage.size.width
        let heightOffset = (widthOffsetPercentage * downloadedImage.size.height)/100
        let effectiveHeight = downloadedImage.size.height - heightOffset
        return effectiveHeight
    }
    
//    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
//        212
//    }

    
}
