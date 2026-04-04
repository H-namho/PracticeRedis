package com.example.swaggerprac.repository;

import com.example.swaggerprac.entity.SearchRequestDto;
import com.example.swaggerprac.entity.User;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> search(SearchRequestDto searchRequestDto);
}
