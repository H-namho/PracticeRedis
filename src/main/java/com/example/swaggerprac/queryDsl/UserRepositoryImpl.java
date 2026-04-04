package com.example.swaggerprac.queryDsl;

import com.example.swaggerprac.entity.QUser;
import com.example.swaggerprac.entity.SearchRequestDto;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.repository.UserRepositryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.swaggerprac.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<User> search(SearchRequestDto searchReaquestDto) {

        return queryFactory.select(user)
                .where(user.username.eq(searchReaquestDto.username())
                        .and(user.age.eq(searchReaquestDto.age()))).fetch();

    }
}
