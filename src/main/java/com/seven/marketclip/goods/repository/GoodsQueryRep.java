package com.seven.marketclip.goods.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seven.marketclip.account.QAccount;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.OrderByDTO;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsOrderBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.seven.marketclip.goods.domain.QGoods.goods;
import static com.seven.marketclip.wish.domain.QWish.wish;
import static com.seven.marketclip.image.domain.QGoodsImage.goodsImage;
import static com.seven.marketclip.goods.enums.GoodsOrderBy.ORDER_BY_WISHLIST_COUNT;


@Repository
public class GoodsQueryRep {
    private final JPAQueryFactory queryFactory;

    public GoodsQueryRep(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Page<Goods> pagingGoods(OrderByDTO orderByDTO, Pageable pageable) {
        List<GoodsCategory> goodsCategories = orderByDTO.getGoodsCategoryList();
        GoodsOrderBy goodsOrderBy = orderByDTO.getGoodsOrderBy();

        List<Goods> queryResult;
        int count;
        QAccount subAccount = new QAccount("subAccount");

        if (goodsOrderBy == ORDER_BY_WISHLIST_COUNT) {
            queryResult = queryFactory
                    .select(goods)
                    .from(goods)
                    .leftJoin(wish)
                    .on(wish.goods.eq(goods))
                    .groupBy(goods.id)
                    .where(categoriesToExpression(goodsCategories))
                    .orderBy(wish.id.count().desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            count = queryFactory
                    .selectFrom(goods)
                    .leftJoin(wish)
                    .on(wish.goods.eq(goods))
                    .where(categoriesToExpression(goodsCategories))
                    .fetch()
                    .size();

        } else {
            queryResult = queryFactory
                    .select(goods)
                    .from(goods)
                    .leftJoin(goodsImage)
                    .on(goods.eq(goodsImage.goods))
                    .where(categoriesToExpression(goodsCategories))
                    .orderBy(orderByToExpression(goodsOrderBy))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            count = queryFactory
                    .select(goods.id)
                    .from(goods)
                    .where(categoriesToExpression(goodsCategories))
                    .fetch()
                    .size();
        }

        return new PageImpl<>(queryResult, pageable, count);
    }

    private BooleanExpression categoriesToExpression(List<GoodsCategory> goodsCategories) {
        if (goodsCategories.isEmpty()) {
            return null;
        }
        BooleanExpression result = goods.category.eq(goodsCategories.get(0));
        if (goodsCategories.size() > 1) {
            for (int i = 1; i < goodsCategories.size(); i++) {
                result = result.or(goods.category.eq(goodsCategories.get(i)));
            }
        }
        return result;
    }


    private OrderSpecifier<?> orderByToExpression(GoodsOrderBy goodsOrderBy) {
        switch (goodsOrderBy) {
            case ORDER_BY_CREATED_AT:
                return goods.createdAt.desc();
            case ORDER_BY_VIEW_COUNT:
                return goods.viewCount.desc();
        }
        return null;
    }

}
