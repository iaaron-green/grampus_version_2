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
    @IBOutlet weak var telegramLabel: UILabel!
    @IBOutlet weak var infoAddButton: UIButton!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var skypeLabel: UILabel!
    @IBOutlet weak var telephoneLabel: UILabel!
    
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
    
    var skypePrefix = "Skype: "
    var emailPrefix = "Email: "
    var telephonePrefix = "Telephone: "
    var telegramPrefix = "Telegram: "
    
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
            infoAddButton.isEnabled = false
            skillsAddButton.isEnabled = false
            userID = storage.getSelectedUserIdProfile()!
            fetchUser(userId: storage.getSelectedUserIdProfile()!)
        }
        tableView.reloadData()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        chartView.delegate = self

                
        SVProgressHUD.setMinimumDismissTimeInterval(1)
        SVProgressHUD.setDefaultStyle(.dark)
        
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
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
                //let user = json["user"] as! NSDictionary
                self.fullName = json["fullName"] as? String ?? "Full name"
                self.profession = json["jobTitle"] as? String ?? "Job Title"
                self.email = json["email"] as? String ?? "Email"
                self.likes = json["likes"] as? Int ?? 0
                self.dislikes = json["dislikes"] as? Int ?? 0
                self.information = json["information"] as? String
                if self.information == "" || self.information == nil {
                    self.information = "Telegram:"
                }
                self.skills = json["skills"] as? String
                if self.skills == "" || self.skills == nil {
                    self.skills = "no skills"
                }
                self.profilePicture = json["profilePicture"] as? String ?? ""
                
                let achieves = json["likesNumber"] as? [String: Int]
                self.achievements = achieves
                //self.achievements = achieves?.convertToDictionary() as? [String : Int]
                self.bestLooker = self.achievements?["best_looker"]
                self.superWorker = self.achievements?["super_worker"]
                self.smartMind = self.achievements?["smart_mind"]
                self.motivator = self.achievements?["motivator"]
                self.deadLiner = self.achievements?["deadliner"]
                self.top1 = self.achievements?["top1"]
                self.mentor = self.achievements?["mentor"]

            
                
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
    
    func setUpProfile( fullName: String, profession: String, likes: Int, dislikes: Int, information: String, skills: String, photo: String) {
        
        profileFullNameLabel.text = fullName
        profileProfessionLabel.text = profession
        profileLikeLabel.text = "Likes: \(String(describing: likes))"
        profileDislikeLabel.text = "Dislikes: \(String(describing: dislikes))"
        profileSkillsLabel.text = skills
        
        emailLabel.attributedText = attributedString(from: emailPrefix + email!, boldRange: NSRange(0...emailPrefix.count - 1))
        skypeLabel.attributedText = attributedString(from: skypePrefix, boldRange: NSRange(0...skypePrefix.count - 1))
        telephoneLabel.attributedText = attributedString(from: telephonePrefix, boldRange: NSRange(0...telephonePrefix.count - 1))
        telegramLabel.attributedText = attributedString(from: telegramPrefix, boldRange: NSRange(0...telegramPrefix.count - 1))

        DispatchQueue.global(qos: .userInteractive).async {
            self.imageService.getImage(withURL: self.profilePicture!) { (image) in

                DispatchQueue.main.async {
                    if let image = image {
                        self.profileImageView.image = image
                        self.tableView.reloadData()

                    } else {
                        self.profileImageView.image = UIImage(named: "red cross")
                    }
                }
            }
        }
    }
    
    func attributedString(from string: String, boldRange: NSRange?) -> NSAttributedString {
        let fontSize = UIFont.systemFontSize
        let boldAttribute = [
            NSAttributedString.Key.font: UIFont.boldSystemFont(ofSize: fontSize),
            NSAttributedString.Key.foregroundColor: UIColor.black
        ]
        let nonBoldAttribute = [
            NSAttributedString.Key.font: UIFont.systemFont(ofSize: fontSize),
        ]
        let attrStr = NSMutableAttributedString(string: string, attributes: nonBoldAttribute)
        if let range = boldRange {
            attrStr.setAttributes(boldAttribute, range: range)
        }
        return attrStr
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
                textField.text = self.telegramLabel.text
            }
        }
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: "information", text: textField!.text!.trimmingCharacters(in: .whitespaces)) { (success) in
                if success {
                    if textField?.text?.trimmingCharacters(in: .whitespaces) != "" {
                        self.telegramLabel.text = textField?.text
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.telegramLabel.text = "Telegram:"
                    }
                    self.tableView.reloadData()
                } else {
                    SVProgressHUD.showError(withStatus: "Error updating information!")
                }
            }
            self.tableView.reloadData()
            
        }))
        
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
                        self.skypeLabel.attributedText = self.attributedString(from: self.skypePrefix + textField!.text!, boldRange: NSRange(0...self.skypePrefix.count - 1))
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.skypeLabel.attributedText = self.attributedString(from: self.skypePrefix, boldRange: NSRange(0...self.skypePrefix.count - 1))
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
                        self.telephoneLabel.attributedText = self.attributedString(from: self.telephonePrefix + textField!.text!, boldRange: NSRange(0...self.telephonePrefix.count - 1))
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.telephoneLabel.attributedText = self.attributedString(from: self.telephonePrefix, boldRange: NSRange(0...self.telephonePrefix.count - 1))
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
        
        let alert = UIAlertController(title: "Enter your Telegram:", message: nil, preferredStyle: .alert)

        alert.addTextField()
        alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
            let textField = alert?.textFields![0] // Force unwrapping because we know it exists.
            self.network.editProfileText(key: "telegram", text: textField!.text!.trimmingCharacters(in: .whitespaces)) { (success) in
                if success {
                    if textField?.text?.trimmingCharacters(in: .whitespaces) != "" {
                        self.telegramLabel.attributedText = self.attributedString(from: self.telegramPrefix + textField!.text!, boldRange: NSRange(0...self.telegramPrefix.count - 1))
                        SVProgressHUD.showSuccess(withStatus: "Success!")
                    } else {
                        self.telegramLabel.attributedText = self.attributedString(from: self.telegramPrefix, boldRange: NSRange(0...self.telegramPrefix.count - 1))
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
