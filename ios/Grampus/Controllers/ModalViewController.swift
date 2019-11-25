//
//  ModalViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 6/5/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit

protocol ModalViewControllerDelegate: class {
    func removeBlurredBackgroundView()
}

class ModalViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    @IBOutlet weak var firstButton: UIButton!
    @IBOutlet weak var secondButton: UIButton!
    @IBOutlet weak var thirdButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var okButton: UIButton!
    @IBOutlet weak var textField: UITextField!
    @IBOutlet weak var countLabel: UILabel!
    @IBOutlet weak var picker: UIPickerView!
    @IBOutlet weak var backView: UIView!
    
    var ratingType: String = "Best looker"
    var likeState: Bool? // if true like, if false dislike
    var selectedUserId: Int?
    let network = NetworkService()
    let storage = StorageService()
    let achieves = ["Best looker", "Deadliner", "Smart mind", "Super worker", "Motivator", "TOP1", "Mentor"]
    
    weak var delegate: ModalViewControllerDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        likeState = storage.getLikeState()
        configureButtons()
        textField.delegate = self
        picker.delegate = self
        picker.dataSource = self

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
        
//        if likeState! {
//            firstButton.setTitle("Best Looker", for: .normal)
//            firstButton.layer.cornerRadius = 5
//            secondButton.setTitle("Super Worker", for: .normal)
//            secondButton.layer.cornerRadius = 5
//            thirdButton.setTitle("Extrovert", for: .normal)
//            thirdButton.layer.cornerRadius = 5
//        } else {
//            firstButton.setTitle("Untidy", for: .normal)
//            firstButton.layer.cornerRadius = 5
//            secondButton.setTitle("Deadliner", for: .normal)
//            secondButton.layer.cornerRadius = 5
//            thirdButton.setTitle("Introvert", for: .normal)
//            thirdButton.layer.cornerRadius = 5
//        }
        
        backView.layer.cornerRadius = 7
        cancelButton.layer.cornerRadius = 5
        okButton.layer.cornerRadius = 5
    }
    
    @IBAction func firstAction(_ sender: Any) {
        
        if likeState! {
            ratingType = "best_looker"
        } else {
            ratingType = "untidy"
        }
        firstButton.backgroundColor = UIColor.blue
        secondButton.backgroundColor = UIColor.darkGray
        thirdButton.backgroundColor = UIColor.darkGray
    }
    
    @IBAction func secondAction(_ sender: Any) {
        
        if likeState! {
            ratingType = "super_worker"
        } else {
            ratingType = "deadliner"
        }
        firstButton.backgroundColor = UIColor.darkGray
        secondButton.backgroundColor = UIColor.blue
        thirdButton.backgroundColor = UIColor.darkGray
    }
    
    @IBAction func thirdAction(_ sender: Any) {
        
        if likeState! {
            ratingType = "extrovert"
        } else {
            ratingType = "introvert"
        }
        firstButton.backgroundColor = UIColor.darkGray
        secondButton.backgroundColor = UIColor.darkGray
        thirdButton.backgroundColor = UIColor.blue
    }
    
    @IBAction func okButtonAction(_ sender: Any) {
        
            network.addLikeOrDislike(ratingType: ratingType, likeState: likeState!)
            dismiss(animated: true, completion: nil)
            delegate?.removeBlurredBackgroundView()

        
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
    //Picker methods
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return achieves.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return achieves[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        switch row {
        case 0:
            ratingType = "best_looker"
        case 1:
            ratingType = "deadliner"
        case 2:
            ratingType = "smart_mind"
        case 3:
            ratingType = "super_worker"
        case 4:
            ratingType = "motivator"
        case 5:
            ratingType = "top1"
        case 6:
            ratingType = "mentor"
        default:
            ratingType = "best_looker"
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
