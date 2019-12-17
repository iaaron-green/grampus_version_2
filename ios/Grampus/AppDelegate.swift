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
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    var window: UIWindow?
    var storage = StorageService()
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        SDImageCache.shared.clearDisk()
        
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { (granted, error) in
            self.getMessage()

        }
        
        return true
    }
    
    func getMessage() {
            //Подключаемся к нашему RabbitMQ серверу
            let conn = RMQConnection(uri: "amqp://taras:taras@10.11.1.25:5672",
                                     delegate: RMQConnectionDelegateLogger())
            conn.start()
            //Создаем канал для работы с сообщениями
            let ch = conn.createChannel()
        let q = ch.queue("grampus.gueue", options: .exclusive)
            //Подписываем нашу очередь на exchange
            ch.queueBind(q.name, exchange: "local", routingKey: "mq.routingkey")
            
            let manualAck = RMQBasicConsumeOptions()
            // Ждем сообщения
            q.subscribe(manualAck, handler: {(_ message: RMQMessage) -> Void in
                //Формируем наш текст
                let messageText = String(data: message.body, encoding: .utf8)
                print("Received \(messageText!)")

                ch.ack(message.deliveryTag)
                
                //Эти 4 строчки задают вид нашего оповещения, которое будет отображаться
                let content = UNMutableNotificationContent()
                content.title = "Congratulation!"
                content.body =  messageText!
                content.sound = UNNotificationSound.default
                
                // Когда получим сообщение от брокера, то через 2 секунды нам придет уведомление
                let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 2, repeats: false)
                
                let request = UNNotificationRequest(identifier: "TestIdentifier", content: content, trigger: trigger)
                UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
            })
        }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void)
    {
        completionHandler([.alert, .badge, .sound])
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
        
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    
}

