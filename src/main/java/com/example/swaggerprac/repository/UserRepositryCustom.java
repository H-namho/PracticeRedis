package com.example.swaggerprac.repository;

import com.example.swaggerprac.entity.SearchRequestDto;
import com.example.swaggerprac.entity.User;

import java.util.List;

public interface UserRepositryCustom {

    List<User> search(SearchRequestDto searchRequestDto);
}
