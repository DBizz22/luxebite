package com.dbizz.repo;

import java.util.List;

import com.dbizz.model.User;

public interface UserRepo {
    public int create(User user) throws Exception;

    public User findById(int id) throws Exception;

    public List<User> findByUsername(String username) throws Exception;

    public User findByEmail(String email) throws Exception;

    public User findByPhoneNo(String phoneNo) throws Exception;

    public int update(User user) throws Exception;

    public int delete(int id) throws Exception;
}
