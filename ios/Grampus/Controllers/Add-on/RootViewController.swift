//
//  RootViewController.swift
//  Grampus
//
//  Created by Тарас on 18.11.2019.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import SVProgressHUD
import ValidationComponents

class RootViewController: UIViewController, UITextFieldDelegate {
    
    //MARK: - Properties
    let predicate = EmailValidationPredicate()
    let myRefreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(pullToRefresh(sender:)), for: .valueChanged)
        return refreshControl
    }()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

    }
    
    //MARK: - Pull to refresh
    @objc func pullToRefresh(sender: UIRefreshControl) {
        sender.endRefreshing()
    }
    
    //MARK: - Keyboard behavior
    // Hide keyboard on tap.
    func dismissKeyboardOnTap() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        view.addGestureRecognizer(tap)
    }
    
    // Hide Keyboard.
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
    
    // Hide the keyboard when the return key pressed.
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    //MARK: - Textfields validation methods
    func userNameValidation(userName: UITextField) -> Bool {
        
        if let userName = userName.text {
            
            if userName.isEmpty {
                SVProgressHUD.showError(withStatus: "Please enter your name")
                return false
            } else if userName.count < 2 {
                SVProgressHUD.showError(withStatus: "This field should contain more than two characters")
                return false
            } else if userName.count > 50 {
                SVProgressHUD.showError(withStatus: "Name too long, write your correct name")
                return false
            }
        }
        return true
    }
    
    func emailValidation(email: UITextField) -> Bool {
        
        let email = email.text
        let emailFormatBool = predicate.evaluate(with: email)
        
        // Email isEmpty check.
        if email!.isEmpty {
            SVProgressHUD.showError(withStatus: "Please enter email address")
            return false
        } else {
            // Email validation.
            if !emailFormatBool {
                SVProgressHUD.showError(withStatus: "Incorrect input, email format not correct!")
                return false
            }
        }
        return true
    }
    
    func passwordValidation(password: UITextField) -> Bool {
        
        if let password = password.text {
            if password.isEmpty {
                SVProgressHUD.showError(withStatus: "Please enter your password")
                return false
            } else if password.count < 6 {
                SVProgressHUD.showError(withStatus: "Password too short, password shoud be more than 6 characters!")
                return false
            } else if password.count >= 24 {
                SVProgressHUD.showError(withStatus: "Password too long, password shoud be less then 24 symbols")
                return false
            }
        }
        return true
    }
    
    func newsTitleValidation(title: UITextField) -> Bool {
        if let title = title.text {
            if title.trimmingCharacters(in: .whitespaces).isEmpty {
                SVProgressHUD.showError(withStatus: "Please enter title!")
                return false
            }
        }
        return true
    }
}
