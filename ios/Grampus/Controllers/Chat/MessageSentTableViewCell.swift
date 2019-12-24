//
//  MessageSentTableViewCell.swift
//  Grampus
//
//  Created by student on 12/20/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit

class MessageSentTableViewCell: UITableViewCell {
    
    @IBOutlet weak var avatarImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var backView: UIView!
    

    override func awakeFromNib() {
        super.awakeFromNib()
        avatarImageView.layer.cornerRadius = 20
        avatarImageView.layer.borderWidth = 1.5
        avatarImageView.layer.borderColor = UIColor.darkGray.cgColor
        
        backView.layer.cornerRadius = 10
        backgroundColor = .clear
        selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
