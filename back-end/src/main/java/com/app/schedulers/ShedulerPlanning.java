package com.app.schedulers;

import com.app.entities.User;

public interface ShedulerPlanning {

    Iterable<User> findByRegistrationDate();
}
