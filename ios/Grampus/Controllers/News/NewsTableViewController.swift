//
//  NewsTableTableViewController.swift
//  Grampus
//
//  Created by student on 12/4/19.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import UIKit

class NewsTableTableViewController: UITableViewController, UINavigationControllerDelegate {
    
    @IBOutlet weak var navegationBar: UINavigationBar!
    @IBOutlet weak var leftBarButton: UIBarButtonItem!
    @IBOutlet weak var addButton: UIBarButtonItem!
    
    
    var screenSize: CGRect!
    var screenWidth: CGFloat!
    var screenHeight: CGFloat!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        screenSize = UIScreen.main.bounds
        screenWidth = screenSize.width
        screenHeight = screenSize.height
        
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 120
        
        if revealViewController() != nil {
            leftBarButton.target = self.revealViewController()
            leftBarButton.action = #selector(SWRevealViewController().revealToggle(_:))
            
            self.view.addGestureRecognizer(revealViewController().panGestureRecognizer())
        }

    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 0
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }

    
}
