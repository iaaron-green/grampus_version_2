//
//  AchievementsCollectionViewCell.swift
//  Grampus
//
//  Created by Тимур Кошевой on 6/18/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit

class AchievementsCollectionViewCell: UICollectionViewCell {
   
    @IBOutlet weak var achievementsImageView: UIImageView!
    @IBOutlet weak var achievementsLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        achievementsLabel.text = "1234"
    }
    
}
