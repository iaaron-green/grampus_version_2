//
//  ImageService.swift
//  Grampus
//
//  Created by student on 11/19/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit
import Alamofire

final class ImageService {
    
    let cache = NSCache<NSString, UIImage>()
    
    func downloadImage(withURL url: URL, completion: @escaping (_ image: UIImage?) -> ()) {
        
        Alamofire.request(url).responseData { (responce) in
            switch responce.result {
            case .success :
                if let data = responce.result.value {
                    let downloadedImage = UIImage(data: data)
                    self.cache.setObject(downloadedImage!, forKey: url.absoluteString as NSString)
                    DispatchQueue.main.async {
                        completion(downloadedImage)
                    }
                }
            case .failure(let error):
                print(error)
            }
        }
    }
    
    func getImage(withURL urlString: String, completion: @escaping (_ image: UIImage?) -> ()) {
        guard let url = URL(string: urlString) else {completion(nil)
            return
        }
            if let image = cache.object(forKey: urlString as NSString) {
            completion(image)
        } else {
            downloadImage(withURL: url, completion: completion)
        }

    }
    
    func ConvertBase64StringToImage (imageBase64String: String) -> UIImage {
        if let imageData = Data.init(base64Encoded: imageBase64String, options: .init(rawValue: 0)) {
            if let image = UIImage(data: imageData){
                return image
            }
        }
        return UIImage(named: "deadliner")!
    }
    
    func clearCache() {
        self.cache.removeAllObjects()
    }
}
