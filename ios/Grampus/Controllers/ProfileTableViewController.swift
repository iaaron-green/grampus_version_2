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
    @IBOutlet weak var countryLabel: UILabel!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var dislikeButton: UIButton!
    @IBOutlet weak var followButton: UIButton!
    
    
    //Achievement cell
    @IBOutlet weak var collectionView: UICollectionView!
    
    //Information cell

    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var skypeLabel: UILabel!
    @IBOutlet weak var telephoneLabel: UILabel!
    @IBOutlet weak var telegramLabel: UILabel!
    @IBOutlet weak var noChartLabel: UILabel!
    @IBOutlet weak var noChartImage: UIImageView!
    
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
    
    var fullName: String?
    var email: String?
    var country: String?
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
    var isAbleToLike = 0
    var mentor: Int?
    var staff = true
    var isFollowing = 0
    
    var userID: String?
    var newsSegueUsed = false
    
    var entries: [PieChartDataEntry] = []
    
    var achievementsArray = [Achievements]()
    let myRefreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(pullToRefresh(sender:)), for: .valueChanged)
        return refreshControl
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        profileCheck()
        addSkeleton()
        chartView.delegate = self
        
        SVProgressHUD.setMinimumDismissTimeInterval(2)
        SVProgressHUD.setDefaultStyle(.dark)
        
        NotificationCenter.default.addObserver(self, selector: #selector(loadProfile(notification:)), name: NSNotification.Name(rawValue: "load"), object: nil)
        
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
                
        if revealViewController() != nil {
            menuBarButton.target = self.revealViewController()
            menuBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
        } else {
            menuBarButton.image = nil
            menuBarButton.action = #selector(dismisController)
        }
        collectionView.delegate = self
        collectionView.dataSource = self
        tableView.refreshControl = myRefreshControl
        tableView.reloadData()
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        SVProgressHUD.show()
    }
    
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(true)
        storage.saveProfileState(state: true)
    }
    
    func profileCheck() {
        if storage.getProfileState() || storage.getUserId()! == storage.getSelectedUserId()! {
            userID = storage.getUserId()!
            fetchUser(userId: userID!)
            addSelfLabelGestures()
            followButton.isHidden = true
            likeButton.isHidden = true
            dislikeButton.isHidden = true
        } else {
            skillsAddButton.isHidden = true
            skypeAddButton.isHidden = true
            telephoneAddButton.isHidden = true
            telegramAddButton.isHidden = true
            userID = storage.getSelectedUserId()!
            fetchUser(userId: userID!)
            addOtherLabelGestures()
        }
    }
    
    func addSkeleton() {
        profileImageView.showAnimatedGradientSkeleton()
        profileLikeLabel.showAnimatedGradientSkeleton()
        profileDislikeLabel.showAnimatedGradientSkeleton()
        profileFullNameLabel.showAnimatedGradientSkeleton()
        profileProfessionLabel.showAnimatedGradientSkeleton()
        countryLabel.showAnimatedGradientSkeleton()
        emailLabel.showAnimatedGradientSkeleton()
        skypeLabel.showAnimatedGradientSkeleton()
        telephoneLabel.showAnimatedGradientSkeleton()
        telegramLabel.showAnimatedGradientSkeleton()
        profileSkillsLabel.showAnimatedGradientSkeleton()
        noChartLabel.showAnimatedGradientSkeleton()
        noChartImage.showAnimatedGradientSkeleton()
    }
    
    func removeSkeleton() {
        profileImageView.hideSkeleton()
        profileLikeLabel.hideSkeleton()
        profileDislikeLabel.hideSkeleton()
        profileFullNameLabel.hideSkeleton()
        profileProfessionLabel.hideSkeleton()
        countryLabel.hideSkeleton()
        emailLabel.hideSkeleton()
        skypeLabel.hideSkeleton()
        telephoneLabel.hideSkeleton()
        telegramLabel.hideSkeleton()
        noChartLabel.hideSkeleton()
        noChartImage.hideSkeleton()
        profileSkillsLabel.hideSkeleton()
        navBarAppearance()
    }
    
    func addSelfLabelGestures() {
        
        let countryTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(countryTapped(tapGestureRecognizer:)))
        let professionTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(professionTapped(tapGestureRecognizer:)))
        let imageTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        
        profileImageView.addGestureRecognizer(imageTapGestureRecognizer)
        countryLabel.addGestureRecognizer(countryTapGestureRecognizer)
        profileProfessionLabel.addGestureRecognizer(professionTapGestureRecognizer)
        
    }
    
    func addOtherLabelGestures() {

        let emailTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(labelTapped(tapGestureRecognizer:)))
        let skypeTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(labelTapped(tapGestureRecognizer:)))
        let telephoneTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(labelTapped(tapGestureRecognizer:)))
        let telegramTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(labelTapped(tapGestureRecognizer:)))

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
        SDImageCache.shared.clearDisk()
        fetchUser(userId: userID!)
        sender.endRefreshing()
    }
    
    @objc func labelTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        var url : URL?
        switch tapGestureRecognizer.view {
        case emailLabel:
            url = URL(string: "mailto:\(email!)")
        case skypeLabel:
            url = URL(string: "skype:\(skype!)?chat")
        case telephoneLabel:
            url = URL(string: "tel://\(telephone!)")
        case telegramLabel:
            url = URL(string: "tg://resolve?domain=\(telegram!)")
        default:
            return
        }
        if let url = url {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url as URL, options: [:], completionHandler: nil)
            }
            else {
                UIApplication.shared.openURL(url as URL)
            }
        }
    }
    
    @IBAction func likeButtonTouched(_ sender: UIButton) {
        storage.chooseLikeOrDislike(bool: true)
        storage.saveSelectedUserId(selectedUserId: userID!)
        self.performSegue(withIdentifier: "ShowModalView", sender: self)

    }
    
    @IBAction func dislikeButtonTouched(_ sender: UIButton) {
        storage.chooseLikeOrDislike(bool: false)
        storage.saveSelectedUserId(selectedUserId: userID!)
        self.performSegue(withIdentifier: "ShowModalView", sender: self)

        
    }
    
    @objc func loadProfile(notification: NSNotification){
        DispatchQueue.main.async {
            self.fetchUser(userId: self.userID!)
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                SVProgressHUD.showSuccess(withStatus: "Sucess!")
            }
        }
    }
    
    
    @objc func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
    {
        let alert = UIAlertController(title: "Choose image source", message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Gallery", style: .default, handler: { _ in
            self.openGallery()
        }))

        alert.addAction(UIAlertAction(title: "Camera", style: .default, handler: { _ in
            self.openCamera()
        }))
        self.present(alert, animated: true) {
            self.tapRecognizer(alert: alert)
        }
        
    }
    
    func tapRecognizer(alert: UIAlertController) {
        alert.view.superview?.subviews.first?.isUserInteractionEnabled = true
        alert.view.superview?.subviews.first?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.actionSheetBackgroundTapped)))
    }
    
    @objc func actionSheetBackgroundTapped() {
        self.dismiss(animated: true, completion: nil)
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
                
                let imageData = selectedImage?.jpegData(compressionQuality: 1)
                let user = User(image: imageData!, name: self.fullName!, profession: self.profession!, email: self.email!)
                self.storage.saveUserProfile(user: user)
                
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "updateUserInfo"), object: nil)
                
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
        network.fetchUserInformation(userId: userId) { (json, error) in
            if let json = json {
//                print(json)
                self.removeSkeleton()
                SVProgressHUD.dismiss()
                self.fullName = json["fullName"] as? String ?? "Full name"
                self.country = json["country"] as? String ?? "Ukraine"
                self.profession = json["jobTitle"] as? String ?? "Job Title"
                self.email = json["email"] as? String ?? ""
                self.skype = json["skype"] as? String ?? ""
                self.telephone = json["phone"] as? String ?? ""
                self.telegram = json["telegram"] as? String ?? ""
                self.likes = json["likes"] as? Int ?? 0
                self.dislikes = json["dislikes"] as? Int ?? 0
                self.skills = json["skills"] as? String
                if self.skills == "" || self.skills == nil {
                    self.skills = "no skills"
                }
                self.isAbleToLike = json["isAbleToLike"] as? Int ?? 1
                self.isFollowing = json["isFollowing"] as? Int ?? 0
                self.profilePicture = json["profilePicture"] as? String ?? ""
                let achieves = json["likesNumber"] as? [String: Int]
                self.achievements = achieves
                self.bestLooker = self.achievements?["BEST_LOOKER"]
                self.superWorker = self.achievements?["SUPER_WORKER"]
                self.smartMind = self.achievements?["SMART_MIND"]
                self.motivator = self.achievements?["MOTIVATOR"]
                self.deadLiner = self.achievements?["DEADLINER"]
                self.top1 = self.achievements?["TOP1"]
                self.mentor = self.achievements?["MENTOR"]
                
                self.setUpProfile(fullName: self.fullName!,country: self.country!, profession: self.profession!, likes: self.likes!, dislikes: self.dislikes!, email: self.email!, skype: self.skype!, telephone: self.telephone!, telegram: self.telegram!, skills: self.skills!, photo: self.profilePicture!)
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
                SVProgressHUD.showError(withStatus: error)
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
        var dislikeColor = UIColor.clear
        var colorArray: [UIColor] = []
        
        entries = [PieChartDataEntry]()

        if self.top1! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.top1!), label: "TOP1"))
            top1 = #colorLiteral(red: 0, green: 0.2470588235, blue: 0.3607843137, alpha: 1)
            colorArray.append(top1)
        }
        
        if self.mentor! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.mentor!), label: "Mentor"))
            mentor = #colorLiteral(red: 0.1843137255, green: 0.2941176471, blue: 0.4862745098, alpha: 1)
            colorArray.append(mentor)
        }
        
        if self.motivator! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.motivator!), label: "Motivator"))
            motivator = #colorLiteral(red: 0.4, green: 0.3176470588, blue: 0.568627451, alpha: 1)
            colorArray.append(motivator)
        }
        
        if self.deadLiner! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.deadLiner!), label: "Deadliner"))
            deadLinerColor = #colorLiteral(red: 0.6274509804, green: 0.3176470588, blue: 0.5843137255, alpha: 1)
            colorArray.append(deadLinerColor)
        }

        if self.superWorker! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.superWorker!), label: "Super worker"))
            superWorkerColor = #colorLiteral(red: 0.831372549, green: 0.3137254902, blue: 0.5294117647, alpha: 1)
            colorArray.append(superWorkerColor)
        }

        if self.smartMind! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.smartMind!), label: "Smart mind"))
            smartMind = #colorLiteral(red: 0.9764705882, green: 0.3647058824, blue: 0.4156862745, alpha: 1)
            colorArray.append(smartMind)
        }

        if self.bestLooker! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.bestLooker!), label: "Best looker"))
            bestLookerColor = #colorLiteral(red: 1, green: 0.4862745098, blue: 0.262745098, alpha: 1)
            colorArray.append(bestLookerColor)
        }
        
        if self.dislikes! != 0 {
            entries.append(PieChartDataEntry(value: Double(self.dislikes!), label: "Dislike"))
            dislikeColor = #colorLiteral(red: 1, green: 0.6509803922, blue: 0, alpha: 1)
            colorArray.append(dislikeColor)
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
        if label == "Dislike" {
            SVProgressHUD.show(UIImage(named: "dislike")!, status: "\(label) - \(value)")
        } else {
            SVProgressHUD.show(UIImage(named: "like")!, status: "\(label) - \(value)")
        }
    }
    
    @IBAction func saveAction(_ sender: UIButton) {
        let image = chartView.getChartImage(transparent: false)
        UIImageWriteToSavedPhotosAlbum(image!, self, #selector(image(_:didFinishSavingWithError:contextInfo:)), nil)
    }
    
    @objc func image(_ image: UIImage, didFinishSavingWithError error: NSError?, contextInfo: UnsafeRawPointer) {
        if error != nil {
            SVProgressHUD.showError(withStatus: "Error saving image!")
        } else {
            SVProgressHUD.showSuccess(withStatus: "Saved to Camera roll!")
        }
    }
    
    func setUpProfile(fullName: String, country: String, profession: String, likes: Int, dislikes: Int, email: String, skype: String, telephone: String, telegram: String, skills: String, photo: String) {
        
        countryLabel.text = country
        profileProfessionLabel.text = profession
        profileLikeLabel.text = "Likes: \(String(describing: likes))"
        profileDislikeLabel.text = "Dislikes: \(String(describing: dislikes))"
        skypeLabel.text = skype
        telephoneLabel.text = telephone
        telegramLabel.text = telegram
        profileSkillsLabel.text = skills
        profileFullNameLabel.text = fullName
        emailLabel.text = email
        
        if isAbleToLike == 0 {
            likeButton.isHidden = true
            dislikeButton.isHidden = true
        }
        
        if isFollowing == 0 {
            followButton.setImage(UIImage(named: "follow"), for: .normal)
        } else {
            followButton.setImage(UIImage(named: "follower"), for: .normal)
        }
        
        let savedUser = self.storage.getUserProfile()
        if savedUser != nil && storage.getProfileState() {
            profileFullNameLabel.text = savedUser?.name
            emailLabel.text = savedUser?.email
            if savedUser?.profession == nil || savedUser?.profession == "" {
                profileProfessionLabel.text = "Job title"
            } else {
                profileProfessionLabel.text = savedUser?.profession
            }
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
        addAlert(title: "Enter your skills:", message: nil, label: profileSkillsLabel, key: "skills", error: "Error updating skills!")
    }
    
    @objc func countryTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        addAlert(title: "Enter your counry:", message: nil, label: countryLabel, key: "country", error: "Error updating country!")
    }
    
    @objc func professionTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        addAlert(title: "Enter your profession:", message: nil, label: profileProfessionLabel, key: "jobTitle", error: "Error updating profession!")
    }
    
    
    @IBAction func skypeAddAction(_ sender: UIButton) {
        addAlert(title: "Enter your Skype:", message: nil, label: skypeLabel, key: "skype", error: "Error updating Skype!")
    }
    
    @IBAction func telephoneAddAction(_ sender: UIButton) {
        addAlert(title: "Enter your Telephone number:", message: nil, label: telephoneLabel, key: "phone", error: "Error updating Telephone number!")
    }
    
    @IBAction func telegramAddAction(_ sender: UIButton) {
        addAlert(title: "Enter your Telegram:", message: "Your account must begin with '@'", label: telegramLabel, key: "telegram", error: "Error updating Telegram!")
    }
    
    func addAlert(title: String, message: String?, label: UILabel, key: String, error: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)

        if label == telephoneLabel {
            alert.addTextField { (textField) in
                textField.keyboardType = .phonePad
            }
        } else if label == profileSkillsLabel {
            alert.addTextField { (textField) in
                if self.skills == "" || self.skills == "no skills" {
                    textField.text = ""
                } else {
                    textField.text = self.profileSkillsLabel.text
                }
            }
        } else {
            alert.addTextField()
        }
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: key, text: textField!.text!.trimmingCharacters(in: .whitespaces)) { (success) in
                if success {
                    if textField?.text?.trimmingCharacters(in: .whitespaces) != "" {
                        label.text = textField?.text
                        if label == self.profileProfessionLabel {
                            var user = self.storage.getUserProfile()
                            user?.profession = textField!.text!
                            self.storage.def.removeObject(forKey: UserDefKeys.userProfile.rawValue)
                            self.storage.saveUserProfile(user: user!)
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "updateUserInfo"), object: nil)
                        }
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        if label == self.profileSkillsLabel {
                            label.text = "no skills"
                        } else if label == self.countryLabel {
                            label.text = "Ukraine"
                        } else if label == self.profileProfessionLabel {
                            label.text = "Job title"
                        } else {
                            label.text = ""
                        }
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: error)
                }
            }
            
        }))
        
        self.present(alert, animated: true, completion: nil)
        self.tableView.reloadData()
    }
    
    @IBAction func followAction(_ sender: UIButton) {
        network.followUser { (success) in
            if success {
                if self.isFollowing == 0 {
                    self.isFollowing = 1
                    SVProgressHUD.showSuccess(withStatus: "Followed!")
                    self.followButton.setImage(UIImage(named: "follower"), for: .normal)
                } else {
                    self.isFollowing = 0
                    SVProgressHUD.showSuccess(withStatus: "Unfollowed!")
                    self.followButton.setImage(UIImage(named: "follow"), for: .normal)
                }
            } else {
                SVProgressHUD.showError(withStatus: "Error!")
            }
        }
    }
    
    @objc func dismisController() {
        self.dismiss(animated: true, completion: nil)
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
        
        return CGSize(width: self.view.frame.width/6, height: self.view.frame.width/4);
        
    }
    
}
