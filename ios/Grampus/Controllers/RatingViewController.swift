//
//  RatingViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 5/21/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON

class RatingViewController: UIViewController, ModalViewControllerDelegate {
    
    // MARK: - Outlets
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var menuBarButton: UIBarButtonItem!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var tableView: UITableView!
    
    
    
    // MARK: - Properties
    let network = NetworkService()
    let storage = StorageService()
    var json = JSON()
    let myRefreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(pullToRefresh(sender:)), for: .valueChanged)
        return refreshControl
    }()
    
    // MARK: - Functions
    override func loadView() {
        super.loadView()
        fetchAllUsers()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.refreshControl = myRefreshControl
        navBarAppearance()
        
        NotificationCenter.default.addObserver(self, selector: #selector(loadList), name: NSNotification.Name(rawValue: "load"), object: nil)
        
        
        if revealViewController() != nil {
            menuBarButton.target = self.revealViewController()
            menuBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
        }
    }
    
    func fetchAllUsers() {
        network.fetchAllUsers { (json) in
            if let json = json {
                self.json = json
                self.tableView.reloadData()
            } else {
                print("Error")
            }
        }
    }
    
    
    @objc func loadList(notification: NSNotification){
        DispatchQueue.main.async {
            self.fetchAllUsers()
            self.tableView.reloadData()
        }
    }
    
    @objc func pullToRefresh(sender: UIRefreshControl) {
        fetchAllUsers()
        tableView.reloadData()
        sender.endRefreshing()
    }
    
    
    func navBarAppearance() {
        navigationBar.barTintColor = UIColor.darkText
        navigationBar.tintColor = UIColor.white
    }
    
    
    // Actions
    @IBAction func likeButtonAction(_ sender: Any) {
        storage.chooseLikeOrDislike(bool: true)
        
        self.performSegue(withIdentifier: "ShowModalView", sender: self)
        self.definesPresentationContext = true
        self.providesPresentationContextTransitionStyle = true
        
        self.overlayBlurredBackgroundView()
    }
    
    @IBAction func dislikeButtonAction(_ sender: Any) {
        storage.chooseLikeOrDislike(bool: false)
        self.performSegue(withIdentifier: "ShowModalView", sender: self)
        
        self.definesPresentationContext = true
        self.providesPresentationContextTransitionStyle = true
        
        self.overlayBlurredBackgroundView()
    }
    
    func overlayBlurredBackgroundView() {
        
        let blurredBackgroundView = UIVisualEffectView()
        
        blurredBackgroundView.frame = view.frame
        blurredBackgroundView.effect = UIBlurEffect(style: .regular)
        
        view.addSubview(blurredBackgroundView)
        
    }
    
    func removeBlurredBackgroundView() {
        
        for subview in view.subviews {
            if subview.isKind(of: UIVisualEffectView.self) {
                subview.removeFromSuperview()
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let identifier = segue.identifier {
            if identifier == "ShowModalView" {
                if let viewController = segue.destination as? ModalViewController {
                    viewController.delegate = self
                    viewController.modalPresentationStyle = .overFullScreen
                }
            }
        }
    }
    
    
    @objc func buttonClicked(sender:UIButton) {
        let buttonRow = sender.tag
        
        if let id = self.json[buttonRow]["profileId"].int {
            storage.saveSelectedUserId(selectedUserId: id)
            
        } else {
                        print("HERE WE GO AGAIN 1")
        }
    }
    
}

extension RatingViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return json.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "ratingCell", for: indexPath) as! RatingTableViewCell
        
        var userNameToDisplay = ""
        var jobTitleToDisplay = ""
        var likeDislikeButtonState: Bool?
        
        DispatchQueue.main.async {
            
            if let id = self.json[indexPath.row]["profileId"].int {
                //                print(id)
            } else {
                //                print("HERE WE GO AGAIN 1")
            }
            
            if let userName = self.json[indexPath.row]["fullName"].string {
                //                print(userName)
                userNameToDisplay = userName
            } else {
                //                print("HERE WE GO AGAIN 2")
            }
            
            if let jobTitle = self.json[indexPath.row]["jobTitle"].string {
                jobTitleToDisplay = jobTitle
            } else {
                //                print("HERE WE GO AGAIN 3")
            }
            
            if let profilePicture = self.json[indexPath.row]["picture"].string {
                //                print(profilePicture)
            } else {
                //                print("HERE WE GO AGAIN 4")
            }
            
            if let isAbleToLike = self.json[indexPath.row]["isAbleToLike"].bool {
                //                print("IS ABLE TO LIKE -------------------------------")
                //                print(isAbleToLike)
                likeDislikeButtonState = isAbleToLike
            } else {
                //                print("HERE WE GO AGAIN 5")
            }
            
            cell.nameLabelCell.text = userNameToDisplay
            cell.professionLabelCell.text = jobTitleToDisplay
            
            if likeDislikeButtonState! {
                cell.likeButton.isEnabled = true
                cell.dislikeButton.isEnabled = true
            } else {
                cell.likeButton.isEnabled = false
                cell.likeButton.tintColor = UIColor.gray
                cell.dislikeButton.isEnabled = false
                cell.dislikeButton.tintColor = UIColor.gray
            }
        }
        
        cell.likeButton.tag = indexPath.row
        cell.dislikeButton.tag = indexPath.row
        cell.likeButton.addTarget(self, action: #selector(self.buttonClicked), for: UIControl.Event.touchUpInside)
        cell.dislikeButton.addTarget(self, action: #selector(self.buttonClicked), for: UIControl.Event.touchUpInside)
        
        return cell
        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if let id = self.json[indexPath.row]["profileId"].int {
            storage.saveSelectedUserIdProfile(id: id)
            storage.saveProfileState(state: false)
            self.performSegue(withIdentifier: SegueIdentifier.rating_to_selected_profile.rawValue, sender: self)
        } else {
            //            print("HERE WE GO AGAIN 1")
        }
    }
    
}
