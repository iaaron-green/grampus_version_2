//
//  RefreshControl.swift
//  Grampus
//
//  Created by Тарас on 16.11.2019.
//  Copyright © 2019 Тимур Кошевой. All rights reserved.
//

import Foundation

var tableRefreshControl:UIRefreshControl = UIRefreshControl()
let refreshControl = UIRefreshControl()

//MARK:- VIEWCONTROLLER EXTENSION METHODS
public extension UIViewController
{
    func makeMyPullToRefreshToTableView(tableName: UITableView){
        
        refreshControl.addTarget(self, action: #selector(pullToRefresh(sender:tableName:)), for: .valueChanged)
        tableName.addSubview(tableRefreshControl)

    }
    @objc func pullToRefresh(sender: UIRefreshControl, tableName: UITableView) {
        tableName.reloadData()
        sender.endRefreshing()
    }
}

extension UITableView {

}
