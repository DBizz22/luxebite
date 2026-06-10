package com.dbizz.model;

import java.time.LocalDateTime;

//TODO: do later
public record RememberToken(int id, int userId, LocalDateTime expiresAt) {

}
