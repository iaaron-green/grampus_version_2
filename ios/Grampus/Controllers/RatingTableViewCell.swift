//
//  RatingTableViewCell.swift
//  Grampus
//
//  Created by Тимур Кошевой on 5/22/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit

class RatingTableViewCell: UITableViewCell {

    @IBOutlet weak var imageViewCell: UIImageView!
    @IBOutlet weak var nameLabelCell: UILabel!
    @IBOutlet weak var professionLabelCell: UILabel!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var dislikeButton: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()

        imageViewCell.layer.cornerRadius = 40
        imageViewCell.layer.borderWidth = 1.5
        imageViewCell.layer.borderColor = UIColor.darkGray.cgColor
    }
    
    override func prepareForReuse() {
        nameLabelCell.text = ""
        professionLabelCell.text = ""
        likeButton.isEnabled = true
        likeButton.tintColor = UIColor.blue
        dislikeButton.tintColor = UIColor.blue
        dislikeButton.isEnabled = true
        
        super.prepareForReuse()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
}
