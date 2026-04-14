package com.example.swaggerprac.queryDsl;

import com.example.swaggerprac.dto.post.PostSearchDto;
import com.example.swaggerprac.entity.PostEntity;
import com.example.swaggerprac.entity.QPostEntity;
import com.example.swaggerprac.entity.QUser;
import com.example.swaggerprac.entity.enumtype.PostSearchType;
import com.example.swaggerprac.repository.PostRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostEntity> SearchPost(PostSearchDto dto) {

        QPostEntity post = QPostEntity.postEntity;
        QUser user = QUser.user;

        return queryFactory.selectFrom(post).join(post.writer,user)
                .fetchJoin().where(search(dto, post)).orderBy(post.createdAt.desc(),post.postId.desc())
                .offset((long)dto.size()*dto.page()).limit(dto.size()).fetch();
    }

    @Override
    public long countSearch(PostSearchDto dto) {
        QPostEntity post = QPostEntity.postEntity;
        return queryFactory.select(post.count())
                .from(post).where(search(dto,post)).fetchCount();
    }

    private BooleanExpression search(PostSearchDto dto,QPostEntity post) {
        PostSearchType type =dto.type();
        String keyword = dto.keyword();
        if(type == null || keyword==null || keyword.isEmpty()) return null;
        if(type==PostSearchType.CONTENT){
            return post.content.contains(keyword);
        }
        if(type==PostSearchType.TITLE){
            return post.title.contains(keyword);
        }
        if(type == PostSearchType.TITLE_CONTENT) {
            return post.title.contains(keyword).or(post.content.contains(keyword));
        }
        return null;
    }

}
