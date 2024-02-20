package com.hanghae.newsfeed.user.repository;

import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.user.entity.QUser;
import com.hanghae.newsfeed.user.entity.User;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<User> searchByNickname(String nickname, Boolean isActive, Pageable pageable) {
        // QueryDSL에서 사용하는 User 엔티티에 대한 Query 타입
        QUser user = QUser.user;
        // jpaQueryFactory를 사용하여 JPQL 쿼리 생성
        JPQLQuery<User> query = jpaQueryFactory
                .selectFrom(user)
                .where(user.nickname.containsIgnoreCase(nickname));

        if (isActive != null) {
            query.where(user.active.eq(isActive));
        }

        if (query.fetchCount() == 0) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        Sort sort = pageable.getSort();
        // 정렬 정보를 이용해 쿼리에 정렬 조건 추가
        sort.stream().forEach(order -> {
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    new PathBuilder(User.class, "user").get(order.getProperty()));
            query.orderBy(orderSpecifier);
        });

        // 페이지네이션을 위한 쿼리 설정
        JPQLQuery<User> pageableQuery = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<User> users = pageableQuery.fetch();

        // 결과 리스트, pageable 객체, 총 데이터 개수를 이용해 Page 객체를 생성하고 반환
        return new PageImpl<>(users, pageable, query.fetchCount());
    }
}
