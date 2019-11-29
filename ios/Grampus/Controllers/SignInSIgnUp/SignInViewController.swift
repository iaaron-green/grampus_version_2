//
//  ViewController.swift
//  Grampus
//
//  Created by Тимур Кошевой on 5/20/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import SVProgressHUD

class SignInViewController: RootViewController {
    
    // MARK: - Outlets
    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var backView: UIView!
    
    @IBOutlet weak var signInButton: UIButton!
    @IBOutlet weak var signUpButton: UIButton!
    
    let network = NetworkService()
    let storage = StorageService()
    
    // MARK: - Functions
    override func viewDidLoad() {
        super.viewDidLoad()
        
        storage.def.removeObject(forKey: UserDefKeys.userProfile.rawValue)
        
        SVProgressHUD.setMinimumDismissTimeInterval(2)
        SVProgressHUD.setDefaultStyle(.dark)
        
        userNameTextField.delegate = self
        passwordTextField.delegate = self
        
        NotificationCenter.default.addObserver(self, selector: #selector(updateTextfields), name: NSNotification.Name(rawValue: "userInformation"), object: nil)
        
        SetUpOutlets()
        dismissKeyboardOnTap()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setUpNotifications()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        removeNotifications()
    }
    
    @objc func updateTextfields(notification: NSNotification) {
        if let email = notification.userInfo?["email"] as? String, let password = notification.userInfo?["password"] as? String {
            userNameTextField.text = email
            passwordTextField.text = password
        }
    }
    
    
    // MARK: - Actions
    @IBAction func SignInButton(_ sender: UIButton) {
        
        dismissKeyboard()
        if emailValidation(email: userNameTextField), passwordValidation(password: passwordTextField) {
            SVProgressHUD.show()

            network.signIn(username: userNameTextField.text!, password: passwordTextField.text!) { (error) in
                if let error = error {
                    if error.contains("401") {
                        SVProgressHUD.showError(withStatus: "User not found")
                    } else {
                        SVProgressHUD.dismiss()
                        SVProgressHUD.showError(withStatus: "Error. \(error)")
                    }
                } else {
                    SVProgressHUD.dismiss()
                    self.performSegue(withIdentifier: SegueIdentifier.login_to_profile.rawValue, sender: self)
                }
            }
        }
    }
    
    func SetUpOutlets() {
        
        userNameTextField.layer.shadowColor = UIColor.darkGray.cgColor
        userNameTextField.layer.shadowOffset = CGSize(width: 3, height: 3)
        userNameTextField.layer.shadowRadius = 5
        userNameTextField.layer.shadowOpacity = 0.5
        
        passwordTextField.layer.shadowColor = UIColor.darkGray.cgColor
        passwordTextField.layer.shadowOffset = CGSize(width: 3, height: 3)
        passwordTextField.layer.shadowRadius = 5
        passwordTextField.layer.shadowOpacity = 0.5
        
        signInButton.layer.shadowColor = UIColor.darkGray.cgColor
        signInButton.layer.shadowOffset = CGSize(width: 3, height: 3)
        signInButton.layer.shadowRadius = 5
        signInButton.layer.shadowOpacity = 0.5
        
//        signUpButton.layer.shadowColor = UIColor.darkGray.cgColor
//        signUpButton.layer.shadowOffset = CGSize(width: 3, height: 3)
//        signUpButton.layer.shadowRadius = 5
//        signUpButton.layer.shadowOpacity = 0.5
        
        signInButton.layer.cornerRadius = 5
        backView.layer.cornerRadius = 7
//        signUpButton.layer.cornerRadius = 5
        
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
            if userNameTextField.isFirstResponder {
                self.view.frame.origin.y = -keyboardSize.height + 100
            } else if passwordTextField.isFirstResponder {
                self.view.frame.origin.y = -keyboardSize.height + 100
            }
        }
    }
}
