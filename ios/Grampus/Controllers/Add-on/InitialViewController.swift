//
//  InitialViewController.swift
//  Grampus
//
//  Created by student on 11/18/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import SVProgressHUD

class InitialViewController: UIViewController {

    var window: UIWindow?
    let network = NetworkService()

    override func viewDidLoad() {
        super.viewDidLoad()
        SVProgressHUD.show()
        SVProgressHUD.setDefaultStyle(.dark)
        SVProgressHUD.setMinimumDismissTimeInterval(20)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        let storage = StorageService()
        let is_authenticated = storage.isLoggedIn()
        storage.saveProfileState(state: true)
        if is_authenticated {
            let userId = storage.getUserId()!
            network.fetchUserInformation(userId: userId) { (json) in
                if json != nil {
                    self.performSegue(withIdentifier: "goToReveal", sender: self)
                } else {
                    SVProgressHUD.showError(withStatus: "Connection error. Please try later")
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.performSegue(withIdentifier: "goToSignIn", sender: self)
                    }
                }
                SVProgressHUD.dismiss()
            }
        } else {
            self.window = UIWindow(frame: UIScreen.main.bounds)
            let mainStoryboard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
            let exampleViewController: SignInViewController = mainStoryboard.instantiateViewController(withIdentifier: "SignInViewController") as! SignInViewController
            SVProgressHUD.dismiss()
            self.window?.rootViewController = exampleViewController
            
            self.window?.makeKeyAndVisible()
            
        }
    }

}