package com.example.securitystudy.user.service;

import com.example.securitystudy.user.UserDao;
import com.example.securitystudy.user.UserProvider;
import com.example.securitystudy.user.entity.User;
import com.example.securitystudy.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

import static com.example.securitystudy.util.BaseResponseStatus.*;

@Slf4j
@Service
public class UserService {

    private final UserProvider userProvider;
    private final UserDao userDao;

    @Autowired
    public UserService( UserProvider userProvider, UserDao userDao) {
        this.userProvider = userProvider;
        this.userDao = userDao;
    }

    public User createUser(User user) throws BaseException {
        if (userProvider.checkEmailProvider(user.getEmail(), user.getProvider()) == 1)
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        try {
            return this.userDao.insertUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public User updateUser(User user) throws BaseException {
        User existingUser = userDao.selectById(user.getId());

        if (existingUser == null) {
            throw new BaseException(INVALID_AUTH);
        }

        // 원하는 필드만 변경
        existingUser.setName(user.getName());
        existingUser.setNickname(user.getNickname());

        return userDao.save(existingUser);
    }

    public User findById(Long id) throws BaseException {
        User user = userDao.selectById(id);

        if (user == null) {
            throw new BaseException(INVALID_AUTH);
        }

        return user;
    }

}
