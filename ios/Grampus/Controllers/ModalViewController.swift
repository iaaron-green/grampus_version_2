//
//  ModalViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 6/5/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire

protocol ModalViewControllerDelegate: class {
    func removeBlurredBackgroundView()
}

class ModalViewController: UIViewController {
    
    @IBOutlet weak var firstButton: UIButton!
    @IBOutlet weak var secondButton: UIButton!
    @IBOutlet weak var thirdButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var okButton: UIButton!
    @IBOutlet weak var textField: UITextField!
    @IBOutlet weak var countLabel: UILabel!
    
    var ratingType: String?
    var likeState: Bool? // if true like, if false dislike
    var selectedUserId: Int?
    let network = NetworkService()
    let storage = StorageService()
    
    weak var delegate: ModalViewControllerDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        likeState = storage.getLikeState()
        configureButtons()
        textField.delegate = self
        
        textField.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
        countLabel.text = "0/24"
    }
    
    override func viewDidLayoutSubviews() {
        view.backgroundColor = UIColor.clear
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setUpNotifications()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        removeNotifications()
    }
    
    func configureButtons() {
        
        if likeState! {
            firstButton.setTitle("Best Looker", for: .normal)
            firstButton.layer.cornerRadius = 5
            secondButton.setTitle("Super Worker", for: .normal)
            secondButton.layer.cornerRadius = 5
            thirdButton.setTitle("Extrovert", for: .normal)
            thirdButton.layer.cornerRadius = 5
        } else {
            firstButton.setTitle("Untidy", for: .normal)
            firstButton.layer.cornerRadius = 5
            secondButton.setTitle("Deadliner", for: .normal)
            secondButton.layer.cornerRadius = 5
            thirdButton.setTitle("Introvert", for: .normal)
            thirdButton.layer.cornerRadius = 5
        }
        
        cancelButton.layer.cornerRadius = 5
        okButton.layer.cornerRadius = 5
    }
    
    @IBAction func firstAction(_ sender: Any) {
        
        if likeState! {
            ratingType = "like_best_looker"
        } else {
            ratingType = "dislike_untidy"
        }
        firstButton.backgroundColor = UIColor.blue
        secondButton.backgroundColor = UIColor.darkGray
        thirdButton.backgroundColor = UIColor.darkGray
    }
    
    @IBAction func secondAction(_ sender: Any) {
        
        if likeState! {
            ratingType = "like_super_worker"
        } else {
            ratingType = "dislike_deadliner"
        }
        firstButton.backgroundColor = UIColor.darkGray
        secondButton.backgroundColor = UIColor.blue
        thirdButton.backgroundColor = UIColor.darkGray
    }
    
    @IBAction func thirdAction(_ sender: Any) {
        
        if likeState! {
            ratingType = "like_extrovert"
        } else {
            ratingType = "dislike_introvert"
        }
        firstButton.backgroundColor = UIColor.darkGray
        secondButton.backgroundColor = UIColor.darkGray
        thirdButton.backgroundColor = UIColor.blue
    }
    
    @IBAction func okButtonAction(_ sender: Any) {
        
        if let unwrappedRatingType = ratingType {
            ratingType = unwrappedRatingType
            
            network.addLikeOrDislike(ratingType: unwrappedRatingType, likeState: likeState!)
            dismiss(animated: true, completion: nil)
            delegate?.removeBlurredBackgroundView()
            
        } else {
            return
        }
        
    }
    
    @IBAction func cancelButtonAction(_ sender: Any) {
        dismiss(animated: true, completion: nil)
        delegate?.removeBlurredBackgroundView()
    }
    
    // Notifications for moving view when keyboard appears.
    func setUpNotifications() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    // Removing notifications.
    func removeNotifications() {
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func keyboardWillHide() {
        self.view.frame.origin.y = 0
    }
    
    @objc func keyboardWillChange(notification: NSNotification) {
        
        if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if textField.isFirstResponder {
                self.view.frame.origin.y = -keyboardSize.height + 100
            }
        }
    }
}

extension ModalViewController: UITextFieldDelegate {
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        if range.length + range.location > textField.text!.count {
            return false
        }
        let newLenghth = textField.text!.count + string.count - range.length
        return newLenghth < 25
    }
    
    @objc func textFieldDidChange(textField : UITextField){
        countLabel.text = "\(textField.text!.count)/24"
    }
}
