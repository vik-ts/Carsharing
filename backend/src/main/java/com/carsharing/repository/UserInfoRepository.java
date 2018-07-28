package com.carsharing.repository;

import org.springframework.data.repository.CrudRepository;

import com.carsharing.model.UserInfo;

public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
}