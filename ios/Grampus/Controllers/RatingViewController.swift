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
import SVProgressHUD
import SDWebImage

class RatingViewController: RootViewController, ModalViewControllerDelegate, UISearchBarDelegate, SWRevealViewControllerDelegate, UITableViewDelegate, UITableViewDataSource, UITableViewDataSourcePrefetching {
    
    // MARK: - Outlets
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var menuBarButton: UIBarButtonItem!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var tableView: UITableView!
    
    
    
    // MARK: - Properties
    let network = NetworkService()
    let storage = StorageService()
    let imageService = ImageService()
    var json = JSON()
    var filteredJson = [JSON]()
    var page = 1
    var isFetch = false
    var limit = 0
    
    // MARK: - Functions
    override func loadView() {
        super.loadView()
        fetchAllUsers(page: 0)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
                
        SVProgressHUD.show()
        
        SVProgressHUD.setMinimumDismissTimeInterval(2)
        SVProgressHUD.setDefaultStyle(.dark)
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.refreshControl = myRefreshControl
        searchBar.delegate = self
        tableView.prefetchDataSource = self

        navBarAppearance()
        
        NotificationCenter.default.addObserver(self, selector: #selector(loadList), name: NSNotification.Name(rawValue: "load"), object: nil)

        
        if revealViewController() != nil {
            menuBarButton.target = self.revealViewController()
            menuBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
            self.revealViewController()?.delegate = self
        }
    }
    
    
    func revealController(_ revealController: SWRevealViewController!, willMoveTo position: FrontViewPosition) {
        dismissKeyboard()
    }
    

    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchBar.searchTextField.resignFirstResponder()
    }
    
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
        filteredJson = [JSON]()

        if searchText == "" {
            fetchAllUsers(page: 0)
//            for i in 0..<json.count {
//                self.filteredJson.append(json[i])
//            }
        } else {
            network.fetchAllUsers(page: 0, name: searchText.lowercased()) { (json) in
                if let json = json {
                    for i in 0..<json.count {
                        self.filteredJson.append(json[i])
                        self.tableView.reloadData()
                    }
                }
            }
            
//            for item in 0..<json.count {
//                let name = json[item]["fullName"].string
//                if (name?.lowercased().contains(searchText.lowercased()))! {
//                    self.filteredJson.append(json[item])
//                }
//            }
        }
        
    }
    
    func fetchAllUsers(page: Int) {
        network.fetchAllUsers(page: page, name: "") { (json) in
            if let json = json {
                SVProgressHUD.dismiss()
                print(json)
                self.json = json
                self.filteredJson = [JSON]()
                for i in 0..<json.count {
                    self.filteredJson.append(json[i])
                }
                self.tableView.reloadData()
            } else {
                print("Error")
            }
        }
    }
    
    
    @objc func loadList(notification: NSNotification){
        DispatchQueue.main.async {
            self.fetchAllUsers(page: 0)
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                SVProgressHUD.showSuccess(withStatus: "Sucess!")
            }
        }
    }
    
    @objc override func pullToRefresh(sender: UIRefreshControl) {
        SDImageCache.shared.clearMemory()
        SDImageCache.shared.clearDisk()
        fetchAllUsers(page: 0)
        page = 1
        limit = 0
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
        blurredBackgroundView.effect = UIBlurEffect(style: .dark)
        
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
        
        if let id = self.json[buttonRow]["id"].int {
            storage.saveSelectedUserId(selectedUserId: id)
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return filteredJson.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "ratingCell", for: indexPath) as! RatingTableViewCell
        
        var userNameToDisplay = ""
        var jobTitleToDisplay = ""
        var likeDislikeButtonState: Bool?
        var profilePictureString = ""
        
        DispatchQueue.main.async {

            userNameToDisplay = self.filteredJson[indexPath.row]["fullName"].string ?? ""
            jobTitleToDisplay = self.filteredJson[indexPath.row]["jobTitle"].string ?? ""
            profilePictureString = self.filteredJson[indexPath.row]["profilePicture"].string?.replacingOccurrences(of: "\\", with: "") ?? ""
            likeDislikeButtonState = self.filteredJson[indexPath.row]["isAbleToLike"].bool ?? false
                
                cell.nameLabelCell.text = userNameToDisplay
                cell.professionLabelCell.text = jobTitleToDisplay
                
                let url = URL(string: profilePictureString)
                cell.imageViewCell.sd_setImage(with: url, placeholderImage: UIImage(named: "red cross"))

                if likeDislikeButtonState! {
                    cell.likeButton.isEnabled = true
                    cell.dislikeButton.isEnabled = true
                } else {
                    cell.likeButton.isEnabled = false
                    cell.likeButton.tintColor = UIColor.gray
                    cell.dislikeButton.isEnabled = false
                    cell.dislikeButton.tintColor = UIColor.gray
                }

                cell.likeButton.tag = indexPath.row
                cell.dislikeButton.tag = indexPath.row
                cell.likeButton.addTarget(self, action: #selector(self.buttonClicked), for: UIControl.Event.touchUpInside)
                cell.dislikeButton.addTarget(self, action: #selector(self.buttonClicked), for: UIControl.Event.touchUpInside)
        }
        return cell

    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if let id = self.filteredJson[indexPath.row]["id"].int {
            storage.saveSelectedUserIdProfile(id: id)
            storage.saveProfileState(state: false)
            self.performSegue(withIdentifier: SegueIdentifier.rating_to_selected_profile.rawValue, sender: self)
        } else {
        }
    }
    
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.row == filteredJson.count - 1 {
            isFetch = true
        } else {
            isFetch = false
        }
    }
    

    
    func tableView(_ tableView: UITableView, prefetchRowsAt indexPaths: [IndexPath]) {
        
        limit = filteredJson.count
        if isFetch {
            network.fetchAllUsers(page: page, name: "") { (json) in
                if let json = json {
                    for i in 0..<json.count {
                        if !self.filteredJson.contains(json[i]) {
                            self.filteredJson.append(json[i])
                        }
                    }
                    if self.limit < self.filteredJson.count {
                        self.page += 1
                        self.tableView.reloadData()
                    }
                } else {
                    print("Error")
                }
            }
        }
    }
}
