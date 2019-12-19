//
//  CommentTableViewCell.swift
//  Grampus
//
//  Created by student on 12/10/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit

class CommentTableViewCell: UITableViewCell {
    
    @IBOutlet weak var commentImageView: UIImageView!
    @IBOutlet weak var commentView: UIView!
    @IBOutlet weak var commentNameLabel: UILabel!
    @IBOutlet weak var commentDateLabel: UILabel!
    @IBOutlet weak var commentLabel: UILabel!
    

    override func awakeFromNib() {
        super.awakeFromNib()
        
        commentImageView.layer.cornerRadius = 20
        commentImageView.layer.borderWidth = 1.5
        commentImageView.layer.borderColor = UIColor.darkGray.cgColor
        
        commentView.layer.cornerRadius = 10
        backgroundColor = .clear
        selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
