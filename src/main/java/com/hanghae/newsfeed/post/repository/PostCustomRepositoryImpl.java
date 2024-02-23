package com.hanghae.newsfeed.post.repository;

import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.entity.QPost;
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
public class PostCustomRepositoryImpl implements PostCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> searchByTitleAndContent(String keyword, Pageable pageable) {
        QPost post = QPost.post;
        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .where(post.title.containsIgnoreCase(keyword)
                .or(post.content.containsIgnoreCase(keyword)));

        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    new PathBuilder(Post.class, "post").get(order.getProperty()));
            query.orderBy(orderSpecifier);
        });

        JPQLQuery<Post> pageableQuery = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Post> posts = pageableQuery.fetch();

        return new PageImpl<>(posts, pageable, query.fetchCount());
    }
}
