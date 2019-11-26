//
//  SignUpViewController.swift
//  Grampus
//
//  Created by MacBook Pro on 5/21/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire
import SVProgressHUD

class SignUpViewController: RootViewController {
    
    // MARK: - Outlets
    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var signUpButton: UIButton!
    @IBOutlet weak var backView: UIView!
    
    let network = NetworkService()
    
    // MARK: - Functions
    override func viewDidLoad() {
        super.viewDidLoad()
        
        SVProgressHUD.setMinimumDismissTimeInterval(2)
        SVProgressHUD.setDefaultStyle(.dark)
        
        userNameTextField.delegate = self
        emailTextField.delegate = self
        passwordTextField.delegate = self
        
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
    
    func SetUpOutlets() {
        
        userNameTextField.layer.shadowColor = UIColor.darkGray.cgColor
        userNameTextField.layer.shadowOffset = CGSize(width: 3, height: 3)
        userNameTextField.layer.shadowRadius = 5
        userNameTextField.layer.shadowOpacity = 0.5
        
        passwordTextField.layer.shadowColor = UIColor.darkGray.cgColor
        passwordTextField.layer.shadowOffset = CGSize(width: 3, height: 3)
        passwordTextField.layer.shadowRadius = 5
        passwordTextField.layer.shadowOpacity = 0.5
        
        emailTextField.layer.shadowColor = UIColor.darkGray.cgColor
        emailTextField.layer.shadowOffset = CGSize(width: 3, height: 3)
        emailTextField.layer.shadowRadius = 5
        emailTextField.layer.shadowOpacity = 0.5
        
        signUpButton.layer.shadowColor = UIColor.darkGray.cgColor
        signUpButton.layer.shadowOffset = CGSize(width: 3, height: 3)
        signUpButton.layer.shadowRadius = 5
        signUpButton.layer.shadowOpacity = 0.5
        
        signUpButton.layer.cornerRadius = 5
        backView.layer.cornerRadius = 7
        
    }
    
    // MARK: - Actions
    
    @IBAction func backButton(_ sender: UIButton) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func SignUpButton(_ sender: UIButton) {
        
        self.dismissKeyboard()

        if userNameValidation(userName: userNameTextField), emailValidation(email: emailTextField), passwordValidation(password: passwordTextField) {
            //Networking
            network.signUp(email: emailTextField.text!.trimmingCharacters(in: .whitespaces), password: passwordTextField.text!, fullName: userNameTextField.text!.trimmingCharacters(in: .whitespaces)) { (success, error) in
                if success {
                    let userInformation = [
                        "email" : self.emailTextField.text,
                        "password": self.passwordTextField.text
                    ]
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "userInformation"), object: nil, userInfo: userInformation)
    
                    SVProgressHUD.showSuccess(withStatus: "Registration success. Thank you. We have sent you an email to \(self.emailTextField.text!)")
                    DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
                        self.dismiss(animated: true, completion: nil)
                        //Directing to profile page
                        //                    self.network.signIn(username: self.emailTextField.text!, password: self.passwordTextField.text!) { (true) in
                        //                        self.performSegue(withIdentifier: "goToProfile", sender: self)
                        //                    }
                        
                    }
                } else if (error?.contains("400"))! {
                    SVProgressHUD.showError(withStatus: "This email address already exists, please enter another email address")
                } else {
                    SVProgressHUD.showError(withStatus: "Error, registration error")
                }
            }
        }
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
            } else if emailTextField.isFirstResponder {
                self.view.frame.origin.y = -keyboardSize.height + 100
            } else if passwordTextField.isFirstResponder {
                self.view.frame.origin.y = -keyboardSize.height + 100
            }
        }
    }
}
