package com.dbizz.service;

import com.dbizz.model.User;
import com.dbizz.repo.UserRepo;
import com.dbizz.util.EmailUtil;
import com.dbizz.util.NameUtil;
import com.dbizz.util.PasswordUtil;
import com.dbizz.util.PhoneNoUtil;

public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public int register(User user) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        NameUtil.validateUsername(user.username());
        EmailUtil.validate(user.email());
        PhoneNoUtil.validatePhoneNo(user.phoneNo());
        PasswordUtil.validate(user.password());
        String hashedPassword = PasswordUtil.hashPassword(user.password());
        User userToSave = new User(0, user.username(), user.email(), user.phoneNo(), hashedPassword, null);
        return userRepo.create(userToSave);
    }

    public User login(User user) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.password() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        if (user.email() == null && user.phoneNo() == null) {
            throw new IllegalArgumentException("Either email or phone number must be provided");
        }

        User searchedUser = null;
        if (user.email() != null) {
            EmailUtil.validate(user.email());
            searchedUser = userRepo.findByEmail(user.email());
        } else if (user.phoneNo() != null) {
            PhoneNoUtil.validatePhoneNo(user.phoneNo());
            searchedUser = userRepo.findByPhoneNo(user.phoneNo());
        }

        if (searchedUser == null) {
            return null;
        }

        if (!PasswordUtil.equals(user.password(), searchedUser.password())) {
            return null;
        }

        return searchedUser;
    }
}
