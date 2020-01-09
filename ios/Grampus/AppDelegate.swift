//
//  AppDelegate.swift
//  Grampus
//
//  Created by Тимур Кошевой on 5/20/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import SDWebImage
import UserNotifications
import RMQClient

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    let storage = StorageService()
    let socket = SocketService()
    
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        SDImageCache.shared.clearDisk()
        
        storage.saveCurrentUserChatId(userId: "")

        if storage.getTokenString() != nil {
            socket.connectToSocket()
        }
        
        return true
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        
    }
    
    func goToChat(id: String, name: String, buttonState: Bool) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let sw = storyboard.instantiateViewController(withIdentifier: "SWRevealViewController") as! SWRevealViewController
        self.window?.rootViewController = sw
        
        let destinationController = storyboard.instantiateViewController(withIdentifier: "ProfileTableViewController") as! ProfileTableViewController
        let navigationController = UINavigationController(rootViewController: destinationController)
        destinationController.userID = id
        destinationController.fullName = name
        destinationController.backButtonForChat = false

        sw.pushFrontViewController(navigationController, animated: true)
        destinationController.performSegue(withIdentifier: "goToChat", sender: nil)
    }
}

