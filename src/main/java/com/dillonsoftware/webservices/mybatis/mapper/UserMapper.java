package com.dillonsoftware.webservices.mybatis.mapper;

import com.dillonsoftware.webservices.bean.User;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

	List<User> list();

	User fetch(@Param("userId") Integer userId);
}
