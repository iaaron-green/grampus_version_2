//
//  ProfileTableViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 5/23/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import Charts
import SVProgressHUD

class ProfileTableViewController: UITableViewController, UICollectionViewDataSource, UICollectionViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    @IBOutlet weak var menuBarButton: UIBarButtonItem!
    @IBOutlet weak var navigationBar: UINavigationBar!
    
    //Overview cell
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var profileFullNameLabel: UILabel!
    @IBOutlet weak var profileProfessionLabel: UILabel!
    @IBOutlet weak var profileLikeLabel: UILabel!
    @IBOutlet weak var profileDislikeLabel: UILabel!
    
    //Achievement cell
    @IBOutlet weak var collectionView: UICollectionView!
    
    //Information cell
    @IBOutlet weak var profileInformationLabel: UILabel!
    @IBOutlet weak var infoAddButton: UIButton!
    
    //Skills cell
    @IBOutlet weak var profileSkillsLabel: UILabel!
    @IBOutlet weak var skillsAddButton: UIButton!
    
    //Chart cell
    @IBOutlet weak var chartView: PieChartView!
    
    let network = NetworkService()
    let storage = StorageService()
    let imageService = ImageService()
    let menuVC = MenuTableViewController()
    let reuseCell = "achievementCell"
    
    var screenSize: CGRect!
    var screenWidth: CGFloat!
    var screenHeight: CGFloat!
    
    var fullName: String?
    var email: String?
    var profession: String?
    var likes: Int?
    var dislikes: Int?
    var information: String?
    var skills: String?
    var achievements: [String: Int]?
    var profilePicture: String?
    var bestLooker: Int?
    var superWorker: Int?
    var untidy: Int?
    var deadLiner: Int?
    var extrovert: Int?
    var introvert: Int?
    var userID: String?
    
    var achievementsArray = [Achievements]()
    let myRefreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(pullToRefresh(sender:)), for: .valueChanged)
        return refreshControl
    }()
    
    override func loadView() {
        super.loadView()
        if storage.getProfileState() {
            userID = storage.getUserId()!
            fetchUser(userId: storage.getUserId()!)
        } else {
            infoAddButton.isEnabled = false
            skillsAddButton.isEnabled = false
            userID = storage.getSelectedUserIdProfile()!
            fetchUser(userId: storage.getSelectedUserIdProfile()!)
        }
        tableView.reloadData()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
                
        SVProgressHUD.setMinimumDismissTimeInterval(1)
        SVProgressHUD.setDefaultStyle(.dark)
        
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        profileImageView.isUserInteractionEnabled = true
        profileImageView.addGestureRecognizer(tapGestureRecognizer)
        
        screenSize = UIScreen.main.bounds
        screenWidth = screenSize.width
        screenHeight = screenSize.height
        
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.isScrollEnabled = true
        collectionView.showsHorizontalScrollIndicator = true
        
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.minimumInteritemSpacing = 1
        layout.minimumLineSpacing = 1
        
        navBarAppearance()
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 120
        
        NotificationCenter.default.addObserver(self, selector: #selector(loadList), name: NSNotification.Name(rawValue: "loadChart"), object: nil)
        
        //self.tableView.tableFooterView = UIView(frame: .zero)
        
        navigationBar.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor)
        if revealViewController() != nil {
            menuBarButton.target = self.revealViewController()
            menuBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
        }
        collectionView.delegate = self
        collectionView.dataSource = self
        tableView.refreshControl = myRefreshControl
        tableView.reloadData()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(true)
        storage.saveProfileState(state: true)
    }
    
    
    @objc func loadList(notification: NSNotification){
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
    
    @objc func pullToRefresh(sender: UIRefreshControl) {
        fetchUser(userId: userID!)
        tableView.reloadData()
        sender.endRefreshing()
    }
    
    @objc func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
    {
        let alert = UIAlertController(title: "Choose Image Source", message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Gallery", style: .default, handler: { _ in
            self.openGallery()
        }))

        alert.addAction(UIAlertAction(title: "Camera", style: .default, handler: { _ in
            self.openCamera()
        }))

        alert.addAction(UIAlertAction.init(title: "Cancel", style: .destructive, handler: nil))

        self.present(alert, animated: true, completion: nil)
        //handleProfilePicker()
        
    }
    
    func openCamera()
    {
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerController.SourceType.camera) {
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerController.SourceType.camera
            imagePicker.allowsEditing = true
            self.present(imagePicker, animated: true, completion: nil)
        }
        else
        {
            SVProgressHUD.showError(withStatus: "You don't have permission to access camera")
        }
    }
    
     func openGallery()
    {
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerController.SourceType.photoLibrary){
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.allowsEditing = true
            imagePicker.sourceType = UIImagePickerController.SourceType.photoLibrary
            self.present(imagePicker, animated: true, completion: nil)
        }
        else
        {
            SVProgressHUD.showError(withStatus: "You don't have permission to access gallery")

        }
    }
    
    func handleProfilePicker() {
        let picker = UIImagePickerController()
        picker.delegate = self
        picker.allowsEditing = true
        self.present(picker,animated: true,completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
      var selectedImage: UIImage?
        if let editedImage = info[.editedImage] as? UIImage {
      selectedImage = editedImage
        } else if let originalImage = info[.originalImage] as? UIImage {
      selectedImage = originalImage
      }
        
        network.uploadImage(selectedImage: selectedImage) { (success) in
            if success {
                self.profileImageView.image = selectedImage
                
                //Update image cache
                self.imageService.cache.setObject(selectedImage!, forKey: self.profilePicture! as NSString)
                
                //Setup notification to change profile photo in MenuTableViewController
                let imageDict = ["image": selectedImage]
                NotificationCenter.default.post(name: Notification.Name(rawValue: "imageChanged"), object: nil, userInfo: imageDict)
                
                SVProgressHUD.showSuccess(withStatus: "Profile photo changed!")
            } else {
                SVProgressHUD.showError(withStatus: "Error uploading photo")
            }
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    func mapAchievements() {
        
        if bestLooker! >= 1 {
            let count = (bestLooker! / 25)
            let image = UIImage(named: "best_looker")
            let achive = Achievements(type: "best looker", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if superWorker! >= 1 {
            let count = (superWorker! / 25)
            let image = UIImage(named: "super_worker")
            let achive = Achievements(type: "super_worker", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if extrovert! >= 1 {
            let count = (extrovert! / 25)
            let image = UIImage(named: "extrovert")
            let achive = Achievements(type: "extrovert", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if deadLiner! >= 1 {
            let count = (deadLiner! / 25)
            let image = UIImage(named: "deadliner")
            let achive = Achievements(type: "deadliner", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if untidy! >= 1 {
            let count = (untidy! / 25)
            let image = UIImage(named: "untidy")
            let achive = Achievements(type: "untidy", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if introvert! >= 1 {
            let count = (introvert! / 25)
            let image = UIImage(named: "introvert")
            let achive = Achievements(type: "introvert", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        collectionView.reloadData()
        
    }
    
    func fetchUser(userId: String) {
        
        network.fetchUserInformation(userId: userId) { (json) in
            
            if let json = json {
                let user = json["user"] as! NSDictionary
                //ACHIEVEMENTS FIX!
                self.achievements = json["achievements"] as? [String: Int]
                print(json["achievements"]!)
                print(self.achievements)
                self.fullName = user["fullName"] as? String ?? "Full name"
                self.profession = user["jobTitle"] as? String ?? "Job Title"
                self.email = user["username"] as? String ?? "Email"
                self.likes = json["likes"] as? Int ?? 0
                self.dislikes = json["dislikes"] as? Int ?? 0
                self.information = json["information"] as? String
                if self.information == "" || self.information == nil {
                    self.information = "no info"
                }
                self.skills = json["skills"] as? String
                if self.skills == "" || self.skills == nil {
                    self.skills = "no skills"
                }
                self.profilePicture = json["profilePicture"] as? String ?? ""
                
//                self.bestLooker = achievements?["BEST_LOOKER"] as? Int
//                self.superWorker = achievements?["SUPER_WORKER"] as? Int
//                self.extrovert = achievements?["EXTROVERT"] as? Int
//                self.untidy = achievements?["UNTIDY"] as? Int
//                self.deadLiner = achievements?["DEADLINER"] as? Int
//                self.introvert = achievements?["INTROVERT"] as? Int
                
                
                if let unwrappedbestLooker = self.bestLooker {
                    self.bestLooker = unwrappedbestLooker
                } else {
                    self.bestLooker = 0
                }
                
                if let unwrappedsuperWorker = self.superWorker {
                    self.superWorker = unwrappedsuperWorker
                } else {
                    self.superWorker = 0
                }
                
                if let unwrappedextrovert = self.extrovert {
                    self.extrovert = unwrappedextrovert
                } else {
                    self.extrovert = 0
                }
                
                if let unwrappeduntidy = self.untidy {
                    self.untidy = unwrappeduntidy
                } else {
                    self.untidy = 0
                }
                
                if let unwrappeddeadLiner = self.deadLiner {
                    self.deadLiner = unwrappeddeadLiner
                } else {
                    self.deadLiner = 0
                }
                
                if let unwrappedintrovert = self.introvert {
                    self.introvert = unwrappedintrovert
                } else {
                    self.introvert = 0
                }
                
                self.setUpProfile(fullName: self.fullName!, profession: self.profession!, likes: self.likes!, dislikes: self.dislikes!, information: self.information!, skills: self.skills!, photo: self.profilePicture!)
                self.setUpCharts()
                self.mapAchievements()
                self.tableView.reloadData()
                
            } else {
                print("Error")
            }
        }
    }
    
    
    
    func setUpCharts() {
        
        chartView.chartDescription?.enabled = false
        chartView.drawHoleEnabled = false
        chartView.rotationAngle = 0
        chartView.rotationEnabled = false
        chartView.isUserInteractionEnabled = false
        chartView.drawEntryLabelsEnabled = false
        
        var bestLookerColor = UIColor.clear
        var superWorkerColor = UIColor.clear
        var extrovertColor = UIColor.clear
        var untidyColor = UIColor.clear
        var deadLinerColor = UIColor.clear
        var introvertColor = UIColor.clear
        
        var entries: [PieChartDataEntry] = []
        var colorArray: [UIColor] = []
        
        if self.bestLooker! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.bestLooker!), label: "Best looker"))
            bestLookerColor = UIColor.red
            colorArray.append(bestLookerColor)
        }
        
        if self.superWorker! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.superWorker!), label: "Super worker"))
            superWorkerColor = UIColor.orange
            colorArray.append(superWorkerColor)
        }
        
        if self.extrovert! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.extrovert!), label: "Extrovert"))
            extrovertColor = UIColor.purple
            colorArray.append(extrovertColor)
        }
        
        if self.untidy! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.untidy!), label: "Untidy"))
            untidyColor = UIColor( red: CGFloat(0/255.0), green: CGFloat(110/255.0), blue: CGFloat(255/255.0), alpha: CGFloat(0.5) )
            colorArray.append(untidyColor)
        }
        
        if self.deadLiner! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.deadLiner!), label: "Deadliner"))
            deadLinerColor = UIColor.green
            colorArray.append(deadLinerColor)
        }
        
        if self.introvert! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.introvert!), label: "Introvert"))
            introvertColor = UIColor.blue
            colorArray.append(introvertColor)
        }
        
        let dataSet = PieChartDataSet(entries: entries, label: "")
        dataSet.colors = colorArray
        dataSet.drawValuesEnabled = true
        chartView.data = PieChartData(dataSet: dataSet)
        
    }
    
    func setUpProfile( fullName: String, profession: String, likes: Int, dislikes: Int, information: String, skills: String, photo: String) {
        
        profileFullNameLabel.text = fullName
        profileProfessionLabel.text = profession
        profileLikeLabel.text = "Likes: \(String(describing: likes))"
        profileDislikeLabel.text = "Dislikes: \(String(describing: dislikes))"
        profileInformationLabel.text = information
        profileSkillsLabel.text = skills
//        DispatchQueue.main.async {
//            let image = self.imageService.ConvertBase64StringToImage(imageBase64String: self.profilePicture!)
//            self.profileImageView.image = image
//        }
        DispatchQueue.global(qos: .userInteractive).async {
            self.imageService.getImage(withURL: self.profilePicture!) { (image) in

                DispatchQueue.main.async {
                    if let image = image {
                        self.profileImageView.image = image
                        self.tableView.reloadData()

                    } else {
                        self.profileImageView.image = UIImage(named: "deadliner")
                    }
                }
            }
        }
    }
    
    func navBarAppearance() {
        profileImageView.layer.cornerRadius = 50
        profileImageView.layer.borderWidth = 1.5
        profileImageView.layer.borderColor = UIColor.darkGray.cgColor
    }
    
    // MARK: - Actions
    @IBAction func informationAddAction(_ sender: Any) {
        let alert = UIAlertController(title: "Enter information about yourself:", message: nil, preferredStyle: .alert)
        
        alert.addTextField { (textField) in
            if self.information == "" || self.information == "no info" {
                textField.text = ""
            } else {
                textField.text = self.profileInformationLabel.text
            }
        }
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: "information", text: textField!.text!) { (success) in
                if success {
                    if textField?.text != "" {
                        self.profileInformationLabel.text = textField?.text
                        SVProgressHUD.showSuccess(withStatus: "Done!")
                    } else {
                        self.profileInformationLabel.text = "no info"
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: "Error updating information!")
                }
            }
            self.tableView.reloadData()
            
        }))
        
        // 4. Present the alert.
        self.present(alert, animated: true, completion: nil)
        self.tableView.reloadData()
    }
    
    @IBAction func skillsAddAction(_ sender: Any) {
        let alert = UIAlertController(title: "Enter your skills:", message: nil, preferredStyle: .alert)
        
        
        alert.addTextField { (textField) in
            if self.skills == "" || self.skills == "no skills" {
                textField.text = ""
            } else {
                textField.text = self.profileSkillsLabel.text
            }
        }
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: "skills", text: textField!.text!) { (success) in
                if success {
                    if textField?.text != "" {
                        self.profileSkillsLabel.text = textField?.text
                        SVProgressHUD.showSuccess(withStatus: "Done!")
                    } else {
                        self.profileSkillsLabel.text = "no skills"
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: "Error updating skills!")
                }
            }
            
        }))
        
        // 4. Present the alert.
        self.present(alert, animated: true, completion: nil)
        self.tableView.reloadData()
    }
    
    
    // MARK: - Table view data source
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.row == 2 || indexPath.row == 3 {
            return UITableView.automaticDimension
        } else if indexPath.row == 4{
            return 300.0
        } else if indexPath.row == 1{
            if achievementsArray.count == 0 {
                return 0
            } else {
                return 90.0
            }
        } else {
            return 120.0
        }
    }
    
    override func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.row == 2 || indexPath.row == 3 {
            return UITableView.automaticDimension
        } else {
            return 120.0
        }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 5
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        
        return achievementsArray.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "achievementCell", for: indexPath) as! AchievementsCollectionViewCell
        
        cell.achievementsLabel.text = String(describing: achievementsArray[indexPath.row].count!)
        cell.achievementsImageView.image = achievementsArray[indexPath.row].image
        return cell
    }
    
    func collectionView(collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAtIndexPath indexPath: NSIndexPath) -> CGSize {
        
        return CGSize(width: screenWidth/6, height: screenWidth/4);
        
    }
    
}
