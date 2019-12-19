//
//  NotificationService.swift
//  Grampus
//
//  Created by student on 12/18/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation
import RMQClient
import NotificationBannerSwift

class NotificationService {
    
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
}
