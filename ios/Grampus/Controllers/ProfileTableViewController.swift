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
import SDWebImage
import SkeletonView


class ProfileTableViewController: UITableViewController, UICollectionViewDataSource, UICollectionViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, ChartViewDelegate {
    
    @IBOutlet weak var menuBarButton: UIBarButtonItem!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var saveButton: UIButton!
    
    //Overview cell
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var profileFullNameLabel: UILabel!
    @IBOutlet weak var profileProfessionLabel: UILabel!
    @IBOutlet weak var profileLikeLabel: UILabel!
    @IBOutlet weak var profileDislikeLabel: UILabel!
    
    //Achievement cell
    @IBOutlet weak var collectionView: UICollectionView!
    
    //Information cell

    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var skypeLabel: UILabel!
    @IBOutlet weak var telephoneLabel: UILabel!
    @IBOutlet weak var telegramLabel: UILabel!
    @IBOutlet weak var noChartImage: UIImageView!
    @IBOutlet weak var noChartLabel: UILabel!
    
    @IBOutlet weak var skypeAddButton: UIButton!
    @IBOutlet weak var telephoneAddButton: UIButton!
    @IBOutlet weak var telegramAddButton: UIButton!
    
    
    
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
    var skype: String?
    var telephone: String?
    var telegram: String?
    var skills: String?
    var achievements: [String: Int]?
    var profilePicture: String?
    var bestLooker: Int?
    var superWorker: Int?
    var smartMind: Int?
    var deadLiner: Int?
    var motivator: Int?
    var top1: Int?
    var mentor: Int?
    var staff = true
    
    var userID: String?
    
    var entries: [PieChartDataEntry] = []
    
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
            profileImageView.isUserInteractionEnabled = true
        } else {
            profileImageView.isUserInteractionEnabled = false
            skillsAddButton.isHidden = true
            skypeAddButton.isHidden = true
            telephoneAddButton.isHidden = true
            telegramAddButton.isHidden = true
            userID = storage.getSelectedUserIdProfile()!
            fetchUser(userId: storage.getSelectedUserIdProfile()!)
        }
        tableView.reloadData()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        SVProgressHUD.show()
        addSkeleton()
        chartView.delegate = self
                
        SVProgressHUD.setMinimumDismissTimeInterval(1)
        SVProgressHUD.setDefaultStyle(.dark)
        
        addLabelGestures()
        
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
    
    func addSkeleton() {
        profileImageView.showAnimatedGradientSkeleton()
        profileLikeLabel.showAnimatedGradientSkeleton()
        profileDislikeLabel.showAnimatedGradientSkeleton()
        profileFullNameLabel.showAnimatedGradientSkeleton()
        profileProfessionLabel.showAnimatedGradientSkeleton()
        emailLabel.showAnimatedGradientSkeleton()
        skypeLabel.showAnimatedGradientSkeleton()
        telephoneLabel.showAnimatedGradientSkeleton()
        telegramLabel.showAnimatedGradientSkeleton()
        profileSkillsLabel.showAnimatedGradientSkeleton()
        noChartImage.showAnimatedGradientSkeleton()
        noChartLabel.showAnimatedGradientSkeleton()
        skypeAddButton.showAnimatedGradientSkeleton()
        telephoneAddButton.showAnimatedGradientSkeleton()
        telegramAddButton.showAnimatedGradientSkeleton()
    }
    
    func removeSkeleton() {
        profileImageView.hideSkeleton()
        profileLikeLabel.hideSkeleton()
        profileDislikeLabel.hideSkeleton()
        profileFullNameLabel.hideSkeleton()
        profileProfessionLabel.hideSkeleton()
        emailLabel.hideSkeleton()
        skypeLabel.hideSkeleton()
        telephoneLabel.hideSkeleton()
        telegramLabel.hideSkeleton()
        noChartLabel.hideSkeleton()
        noChartImage.hideSkeleton()
        profileSkillsLabel.hideSkeleton()
        skypeAddButton.hideSkeleton()
        telephoneAddButton.hideSkeleton()
        telegramAddButton.hideSkeleton()
        navBarAppearance()
    }
    
    
    func addLabelGestures() {
        
        let imageTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        let emailTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(emailTapped(tapGestureRecognizer:)))
        let skypeTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(skypeTapped(tapGestureRecognizer:)))
        let telephoneTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(telephoneTapped(tapGestureRecognizer:)))
        let telegramTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(telegramTapped(tapGestureRecognizer:)))
        
        profileImageView.addGestureRecognizer(imageTapGestureRecognizer)
        emailLabel.addGestureRecognizer(emailTapGestureRecognizer)
        skypeLabel.addGestureRecognizer(skypeTapGestureRecognizer)
        telephoneLabel.addGestureRecognizer(telephoneTapGestureRecognizer)
        telegramLabel.addGestureRecognizer(telegramTapGestureRecognizer)
    }
    
    
    @objc func loadList(notification: NSNotification){
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
    
    @objc func pullToRefresh(sender: UIRefreshControl) {
        fetchUser(userId: userID!)
        sender.endRefreshing()
    }
    
    @objc func emailTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        if let url = URL(string: "mailto:\(email!)") {
          if #available(iOS 10.0, *) {
              UIApplication.shared.open(url as URL, options: [:], completionHandler: nil)
          }
          else {
              UIApplication.shared.openURL(url as URL)
          }
        }
    }
    
    @objc func skypeTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        if let url = URL(string: "skype:\(skype!)?chat") {
          if #available(iOS 10.0, *) {
            UIApplication.shared.open(url)
          } else {
            UIApplication.shared.openURL(url)
          }
        }
    }
    
    @objc func telephoneTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        if let url = URL(string: "tel://\(telephone!)") {
          if #available(iOS 10.0, *) {
            UIApplication.shared.open(url)
          } else {
            UIApplication.shared.openURL(url)
          }
        }
    }
    
    @objc func telegramTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        if let url = URL(string: "tg://resolve?domain=\(telegram!)") {
            print(url)
          if #available(iOS 10.0, *) {
            UIApplication.shared.open(url)
          } else {
            UIApplication.shared.openURL(url)
          }
        }
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
                
                //Clear image cache
                SDImageCache.shared.clearMemory()
                SDImageCache.shared.clearDisk()

                //Setup notification to change profile photo in MenuTableViewController
                let imageDict = ["image": selectedImage]
                NotificationCenter.default.post(name: Notification.Name(rawValue: "imageChanged"), object: nil, userInfo: imageDict)
                
                let imageData = selectedImage?.jpegData(compressionQuality: 1)
                let user = User(image: imageData!, name: self.fullName!, profession: self.profession!, email: self.email!)
                self.storage.saveUserProfile(user: user)
                
                
                SVProgressHUD.showSuccess(withStatus: "Profile photo changed!")
            } else {
                SVProgressHUD.showError(withStatus: "Error uploading photo")
            }
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    func mapAchievements() {
        achievementsArray.removeAll()
        if staff {
            let count = 0
            let image = UIImage(named: "staff")
            let achive = Achievements(type: "staff", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if bestLooker! >= 5 {
            let count = (bestLooker! / 5)
            let image = UIImage(named: "best_looker")
            let achive = Achievements(type: "best looker", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if superWorker! >= 5 {
            let count = (superWorker! / 5)
            let image = UIImage(named: "super_worker")
            let achive = Achievements(type: "super_worker", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if smartMind! >= 5 {
            let count = (smartMind! / 5)
            let image = UIImage(named: "smart_mind")
            let achive = Achievements(type: "smart_mind", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if deadLiner! >= 5 {
            let count = (deadLiner! / 5)
            let image = UIImage(named: "deadliner")
            let achive = Achievements(type: "deadliner", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if motivator! >= 5 {
            let count = (motivator! / 5)
            let image = UIImage(named: "motivator")
            let achive = Achievements(type: "motivator", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if top1! >= 5 {
            let count = (top1! / 5)
            let image = UIImage(named: "top1")
            let achive = Achievements(type: "top1", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        if mentor! >= 5 {
            let count = (top1! / 5)
            let image = UIImage(named: "mentor")
            let achive = Achievements(type: "mentor", count: count, image: image)
            achievementsArray.append(achive)
        }
        
        collectionView.reloadData()
        
    }
    
    func fetchUser(userId: String) {
        
        network.fetchUserInformation(userId: userId) { (json) in
            
            if let json = json {
                //print(json)
                self.removeSkeleton()
                SVProgressHUD.dismiss()
                self.fullName = json["fullName"] as? String ?? "Full name"
                self.profession = json["jobTitle"] as? String ?? "Job Title"
                self.email = json["email"] as? String ?? ""
                self.skype = json["skype"] as? String ?? ""
                self.telephone = json["telephone"] as? String ?? ""
                self.telegram = json["telegram"] as? String ?? ""
                self.likes = json["likes"] as? Int ?? 0
                self.dislikes = json["dislikes"] as? Int ?? 0
                self.skills = json["skills"] as? String
                if self.skills == "" || self.skills == nil {
                    self.skills = "no skills"
                }
                self.profilePicture = json["profilePicture"] as? String ?? ""
                
                let achieves = json["likesNumber"] as? [String: Int]
                self.achievements = achieves
                self.bestLooker = self.achievements?["best_looker"]
                self.superWorker = self.achievements?["super_worker"]
                self.smartMind = self.achievements?["smart_mind"]
                self.motivator = self.achievements?["motivator"]
                self.deadLiner = self.achievements?["deadliner"]
                self.top1 = self.achievements?["top1"]
                self.mentor = self.achievements?["mentor"]
                
                self.setUpProfile(fullName: self.fullName!, profession: self.profession!, likes: self.likes!, dislikes: self.dislikes!, email: self.email!, skype: self.skype!, telephone: self.telephone!, telegram: self.telegram!, skills: self.skills!, photo: self.profilePicture!)
                self.setUpCharts()
                self.mapAchievements()
                if self.entries.isEmpty {
                    self.saveButton.isHidden = true
                    self.noChartImage.isHidden = false
                    self.noChartLabel.isHidden = false
                } else {
                    self.saveButton.isHidden = false
                    self.noChartImage.isHidden = true
                    self.noChartLabel.isHidden = true
                }
                    self.tableView.reloadData()
                
            } else {
                print("Error")
            }
        }
    }
    
    func setUpCharts() {

        chartView.chartDescription?.enabled = false
        chartView.drawHoleEnabled = true
        chartView.rotationAngle = 0
        chartView.rotationEnabled = true
        chartView.isUserInteractionEnabled = true
        chartView.drawEntryLabelsEnabled = false
        
        let legend = chartView.legend
        legend.horizontalAlignment = .left
        legend.verticalAlignment = .bottom
        legend.orientation = .vertical
        legend.xEntrySpace = 0
        legend.yEntrySpace = 0
        legend.font = UIFont(name: "Helvetica Neue", size: 14)!
        
        chartView.animate(yAxisDuration: 2, easingOption: .easeInOutSine)
        chartView.highlightPerTapEnabled = true
                
        
        var bestLookerColor = UIColor.clear
        var superWorkerColor = UIColor.clear
        var smartMind = UIColor.clear
        var motivator = UIColor.clear
        var deadLinerColor = UIColor.clear
        var top1 = UIColor.clear
        var mentor = UIColor.clear

        
        var colorArray: [UIColor] = []
        entries = [PieChartDataEntry]()

        if self.top1! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.top1!), label: "TOP1"))
            top1 = #colorLiteral(red: 0, green: 0.2470588235, blue: 0.3607843137, alpha: 1)
            colorArray.append(top1)
        }
        
        if self.mentor! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.mentor!), label: "Mentor"))
            mentor = #colorLiteral(red: 0.2156862745, green: 0.2980392157, blue: 0.5019607843, alpha: 1)
            colorArray.append(mentor)
        }
        
        if self.motivator! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.motivator!), label: "Motivator"))
            motivator = #colorLiteral(red: 0.4784313725, green: 0.3176470588, blue: 0.5843137255, alpha: 1)
            colorArray.append(motivator)
        }
        
        if self.deadLiner! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.deadLiner!), label: "Deadliner"))
            deadLinerColor = #colorLiteral(red: 0.737254902, green: 0.3137254902, blue: 0.5647058824, alpha: 1)
            colorArray.append(deadLinerColor)
        }

        if self.superWorker! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.superWorker!), label: "Super worker"))
            superWorkerColor = #colorLiteral(red: 0.937254902, green: 0.337254902, blue: 0.4588235294, alpha: 1)
            colorArray.append(superWorkerColor)
        }

        if self.smartMind! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.smartMind!), label: "Smart mind"))
            smartMind = #colorLiteral(red: 1, green: 0.462745098, blue: 0.2901960784, alpha: 1)
            colorArray.append(smartMind)
        }

        if self.bestLooker! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.bestLooker!), label: "Best looker"))
            bestLookerColor = #colorLiteral(red: 1, green: 0.6509803922, blue: 0, alpha: 1)
            colorArray.append(bestLookerColor)
        }

        let dataSet = PieChartDataSet(entries: entries, label: nil)
        dataSet.colors = colorArray
        dataSet.drawValuesEnabled = true
        
        //Value formatter to show Ints
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 0
        dataSet.valueFormatter = DefaultValueFormatter(formatter: formatter)
        
        chartView.data = PieChartData(dataSet: dataSet)
        
    }
    
    func chartValueSelected(_ chartView: ChartViewBase, entry: ChartDataEntry, highlight: Highlight) {

        let label = entries[Int(highlight.x)].label!
        let value = Int(entries[Int(highlight.x)].value)
        
        SVProgressHUD.show(UIImage(named: "heart")!, status: "\(label) - \(value)")
    }
    
    @IBAction func saveAction(_ sender: UIButton) {
        let image = chartView.getChartImage(transparent: false)
        UIImageWriteToSavedPhotosAlbum(image!, nil, nil, nil)
        SVProgressHUD.showSuccess(withStatus: "Saved to Camera roll!")
    }
    
    func setUpProfile( fullName: String, profession: String, likes: Int, dislikes: Int, email: String, skype: String, telephone: String, telegram: String, skills: String, photo: String) {
        
        profileProfessionLabel.text = profession
        profileLikeLabel.text = "Likes: \(String(describing: likes))"
        profileDislikeLabel.text = "Dislikes: \(String(describing: dislikes))"
        skypeLabel.text = skype
        telephoneLabel.text = telephone
        telegramLabel.text = telegram
        profileSkillsLabel.text = skills
        
        profileFullNameLabel.text = fullName
        emailLabel.text = email
        
        
        DispatchQueue.main.async {

            let url = URL(string: self.profilePicture!)
            self.profileImageView.sd_setImage(with: url, placeholderImage: UIImage(named: "red cross"))
        }
        
        
        
        let savedUser = self.storage.getUserProfile()
        if savedUser != nil && storage.getProfileState() {
            profileFullNameLabel.text = savedUser?.name
            emailLabel.text = savedUser?.email
            if let imageData = savedUser?.image {
                    profileImageView.image = UIImage(data: imageData)
                } else {
                    profileImageView.image = UIImage(named: "red cross")
                }
            } else {
                
                profileFullNameLabel.text = fullName
                emailLabel.text = email
                
                
                DispatchQueue.main.async {

                    let url = URL(string: self.profilePicture!)
                    self.profileImageView.sd_setImage(with: url, placeholderImage: UIImage(named: "red cross"))
                }
                
            }
        }

    
    func navBarAppearance() {
        profileImageView.layer.cornerRadius = 50
        profileImageView.layer.borderWidth = 1.5
        profileImageView.layer.borderColor = UIColor.darkGray.cgColor
    }
    
    // MARK: - Actions
    
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
            self.network.editProfileText(key: "skills", text: textField!.text!.trimmingCharacters(in: .whitespaces)) { (success) in
                if success {
                    if textField?.text?.trimmingCharacters(in: .whitespaces) != "" {
                        self.profileSkillsLabel.text = textField?.text
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.profileSkillsLabel.text = "no skills"
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: "Error updating skills!")
                }
            }
            
        }))
        
        self.present(alert, animated: true, completion: nil)
        self.tableView.reloadData()
    }
    
    @IBAction func skypeAddAction(_ sender: UIButton) {
        let alert = UIAlertController(title: "Enter your Skype:", message: nil, preferredStyle: .alert)

        alert.addTextField()
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: "skype", text: textField!.text!.trimmingCharacters(in: .whitespaces)) { (success) in
                if success {
                    if textField?.text?.trimmingCharacters(in: .whitespaces) != "" {
                        self.skypeLabel.text = textField?.text
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.skypeLabel.text = ""
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: "Error updating Skype!")
                }
            }
            
        }))
        
        self.present(alert, animated: true, completion: nil)
        self.tableView.reloadData()
    }
    
    @IBAction func telephoneAddAction(_ sender: UIButton) {
        
        let alert = UIAlertController(title: "Enter your Telephone number:", message: nil, preferredStyle: .alert)

        alert.addTextField { (textfield) in
            textfield.keyboardType = .phonePad
        }
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: "telephone", text: textField!.text!.trimmingCharacters(in: .whitespaces)) { (success) in
                if success {
                    if textField?.text?.trimmingCharacters(in: .whitespaces) != "" {
                        self.telephoneLabel.text = textField?.text
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.telephoneLabel.text = ""
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: "Error updating Telephone number!")
                }
            }
            
        }))
        
        self.present(alert, animated: true, completion: nil)
        self.tableView.reloadData()
    }
    
    @IBAction func telegramAddAction(_ sender: UIButton) {
        
        let alert = UIAlertController(title: "Enter your Telegram:", message: "Your accoun must begin with '@'", preferredStyle: .alert)

        alert.addTextField()
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: "telegram", text: textField!.text!.trimmingCharacters(in: .whitespaces)) { (success) in
                if success {
                    if textField?.text?.trimmingCharacters(in: .whitespaces) != "" {
                        self.telegramLabel.text = textField?.text
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.telegramLabel.text = ""
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: "Error updating Telegram number!")
                }
            }
            
        }))
        
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
        
        if achievementsArray[indexPath.row].count != 0 {
            cell.achievementsLabel.text = String(describing: achievementsArray[indexPath.row].count!)
        } else {
            cell.achievementsLabel.text = "Staff"
        }
        cell.achievementsImageView.image = achievementsArray[indexPath.row].image
        return cell
    }
    
    func collectionView(collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAtIndexPath indexPath: NSIndexPath) -> CGSize {
        
        return CGSize(width: screenWidth/6, height: screenWidth/4);
        
    }
    
}
