//
//  ModalViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 6/5/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import SVProgressHUD
import Lottie

protocol ModalViewControllerDelegate: class {
    func removeBlurredBackgroundView()
}

class ModalViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var okButton: UIButton!
    @IBOutlet weak var messageTextfield: UITextField!
    @IBOutlet weak var countLabel: UILabel!
    @IBOutlet weak var picker: UIPickerView!
    @IBOutlet weak var backView: UIView!
    @IBOutlet weak var dislikeLottie: AnimationView!
    
    var ratingType: String = "BEST_LOOKER"
    var likeState: Bool? // if true like, if false dislike
    var selectedUserId: Int?
    let network = NetworkService()
    let storage = StorageService()
    let achieves = ["Best looker", "Deadliner", "Smart mind", "Super worker", "Motivator", "TOP1", "Mentor"]
    
    weak var delegate: ModalViewControllerDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        SVProgressHUD.setMinimumDismissTimeInterval(2)
        SVProgressHUD.setDefaultStyle(.dark)
        
        likeState = storage.getLikeState()
        configureView()
        messageTextfield.delegate = self
        picker.delegate = self
        picker.dataSource = self

        messageTextfield.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
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
    
    func configureView() {
        
        backView.layer.cornerRadius = 7
        cancelButton.layer.cornerRadius = 5
        okButton.layer.cornerRadius = 5
        
        if likeState! {
            dislikeLottie.isHidden = true
            
        } else {
            picker.isHidden = true
            ratingType = "DISLIKE"
            startAnimation()
            
        }
    }
    
    func startAnimation() {
        dislikeLottie.animation = Animation.named("dislike_animation")
        dislikeLottie.loopMode = .loop
        dislikeLottie.play()
    }
    
   
    
    @IBAction func okButtonAction(_ sender: Any) {
        
        let message = messageTextfield.text!
        network.addLikeOrDislike(ratingType: ratingType, likeState: likeState!, message: message)
        print(ratingType)
        dismiss(animated: true, completion: nil)
        dislikeLottie.stop()
        delegate?.removeBlurredBackgroundView()
        //SVProgressHUD.showSuccess(withStatus: "Sucess!")
        
        
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
            if messageTextfield.isFirstResponder {
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
            ratingType = "BEST_LOOKER"
        case 1:
            ratingType = "DEADLINER"
        case 2:
            ratingType = "SMART_MIND"
        case 3:
            ratingType = "SUPER_WORKER"
        case 4:
            ratingType = "MOTIVATOR"
        case 5:
            ratingType = "TOP1"
        case 6:
            ratingType = "MENTOR"
        default:
            ratingType = "BEST_LOOKER"
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
