package com.blackwaterpragmatic.webservices.mybatis.mapper;

import com.blackwaterpragmatic.webservices.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

	List<User> list(@Param("name") String name);

	User fetch(@Param("userId") Integer userId);

	Integer create(@Param("user") User user);

	Integer delete(@Param("userId") Integer userId);
}
