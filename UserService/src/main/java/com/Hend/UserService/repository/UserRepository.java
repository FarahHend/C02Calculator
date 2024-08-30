package com.Hend.UserService.repository;

import com.Hend.UserService.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}