//
//  NewsTableViewCell.swift
//  Grampus
//
//  Created by student on 12/5/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit

class NewsTableViewCell: UITableViewCell {
    
    @IBOutlet weak var avatarImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var bodyLabel: UILabel!
    @IBOutlet weak var newsImageView: UIImageView!
    @IBOutlet weak var cardView: UIView!
    @IBOutlet weak var commentsLabel: UILabel!
    @IBOutlet weak var deleteButton: UIButton!
    let storage = StorageService()
    let network = NetworkService()
    var newsId = 0
    
    
    
    internal var aspectConstraint : NSLayoutConstraint? {
        didSet {
            if oldValue != nil {
                newsImageView.removeConstraint(oldValue!)
            }
            if aspectConstraint != nil {
                newsImageView.addConstraint(aspectConstraint!)
            }
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        aspectConstraint = nil
        avatarImageView.layer.cornerRadius = 20
        avatarImageView.layer.borderWidth = 1.5
        avatarImageView.layer.borderColor = UIColor.darkGray.cgColor
        
        cardView.layer.cornerRadius = 10
        
        
        backgroundColor = .clear
        selectionStyle = .none
    }
    
    
//    override func prepareForReuse() {
//        super.prepareForReuse()
//        newsImageView.sd_cancelCurrentImageLoad()
//        newsImageView.image = nil
//    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }
    
    @IBAction func deleteAction(_ sender: UIButton) {
        network.deleteNews(id: newsId) { (success) in
            if success {
                print("OK")
            } else {
                print("error")
            }
        }
    }
    
    
    
    func setCustomImage(image : UIImage) {
        
        let aspect = image.size.width / image.size.height
        
        let constraint = NSLayoutConstraint(item: newsImageView!, attribute: NSLayoutConstraint.Attribute.width, relatedBy: NSLayoutConstraint.Relation.equal, toItem: newsImageView, attribute: NSLayoutConstraint.Attribute.height, multiplier: aspect, constant: 0.0)
        constraint.priority = UILayoutPriority(rawValue: 999)
        
        aspectConstraint = constraint

        newsImageView.image = image
        newsImageView.layer.cornerRadius = 10

    }
    
}
