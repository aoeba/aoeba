package com.devsss.aoeba.service.mapper;


import com.devsss.aoeba.service.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserInfo getUserInfo(String username, String password);

    UserInfo getUserInfoByName(String username);
}