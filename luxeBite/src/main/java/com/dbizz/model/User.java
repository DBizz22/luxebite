package com.dbizz.model;

import java.time.LocalDateTime;
import com.dbizz.util.EmailUtil;
import com.dbizz.util.IDCheck;
import com.dbizz.util.PasswordUtil;
import com.dbizz.util.TimeUtils;
import com.dbizz.util.NameUtil;
import com.dbizz.util.PhoneNoUtil;

//WARN: Next time transient filed for sensitive data like password
public record User(int id, String username, String email, String phoneNo, String password, LocalDateTime createdAt)
        implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return id == other.id &&
                (username == null ? other.username == null : username.equals(other.username)) &&
                (email == null ? other.email == null : email.equals(other.email)) &&
                (phoneNo == null ? other.phoneNo == null : phoneNo.equals(other.phoneNo)) &&
                (password == null ? other.password == null : password.equals(other.password));
    }
}
