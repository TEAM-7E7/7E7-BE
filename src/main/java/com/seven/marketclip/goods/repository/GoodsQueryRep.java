package com.seven.marketclip.goods.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.OrderByDTO;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsOrderBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.seven.marketclip.exception.ResponseCode.INVALID_GOODS_ORDER;
import static com.seven.marketclip.exception.ResponseCode.INVALID_GOODS_STATUS;
import static com.seven.marketclip.goods.domain.QGoods.goods;
import static com.seven.marketclip.goods.enums.GoodsOrderBy.*;
import static com.seven.marketclip.goods.enums.GoodsStatus.*;
import static com.seven.marketclip.wish.domain.QWish.wish;


@Repository
public class GoodsQueryRep {
    private final JPAQueryFactory queryFactory;

    public GoodsQueryRep(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Page<Goods> pagingGoods(OrderByDTO orderByDTO, Pageable pageable) throws CustomException {
        List<GoodsCategory> goodsCategories = orderByDTO.getGoodsCategoryList();
        GoodsOrderBy goodsOrderBy = orderByDTO.getGoodsOrderBy();

        List<Goods> queryResult;
        int count;

        if (goodsOrderBy == ORDER_BY_WISHLIST_COUNT) {
            queryResult = queryFactory
                    .selectFrom(goods)
                    .leftJoin(wish)
                    .on(wish.goods.eq(goods))
                    .groupBy(goods.id)
                    .where(categoriesToExpression(goodsCategories))
                    .orderBy(wish.id.count().desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            count = queryFactory
                    .select(goods.id)
                    .from(goods)
                    .leftJoin(wish)
                    .on(wish.goods.eq(goods))
                    .where(categoriesToExpression(goodsCategories))
                    .fetch()
                    .size();

        } else if (goodsOrderBy == ORDER_BY_CREATED_AT || goodsOrderBy == ORDER_BY_VIEW_COUNT) {
            queryResult = queryFactory
                    .selectFrom(goods)
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
        } else {
            throw new CustomException(INVALID_GOODS_ORDER);
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

    public Page<Goods> findAllByAccountIdOrderByCreatedAtDesc(Long accountId, String goodsStatus, Pageable pageable) throws CustomException {
        List<Goods> queryResult;
        int count;

        if (goodsStatus.equals(SALE.name())) {
            queryResult = queryFactory
                    .selectFrom(goods)
                    .where(goods.account.id.eq(accountId)
                            .and(goods.status.eq(SALE)
                                    .or(goods.status.eq(RESERVED)))
                    )
                    .orderBy(goods.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            count = queryFactory
                    .select(goods.id)
                    .from(goods)
                    .where(goods.account.id.eq(accountId)
                            .and(goods.status.eq(SALE)
                                    .or(goods.status.eq(RESERVED)))
                    )
                    .fetch()
                    .size();

        } else if (goodsStatus.equals(SOLD_OUT.name())) {
            queryResult = queryFactory
                    .selectFrom(goods)
                    .where(goods.account.id.eq(accountId)
                            .and(goods.status.eq(SOLD_OUT))
                    )
                    .orderBy(goods.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            count = queryFactory
                    .select(goods.id)
                    .from(goods)
                    .where(goods.account.id.eq(accountId)
                            .and(goods.status.eq(SOLD_OUT))
                    )
                    .fetch()
                    .size();
        } else {
            throw new CustomException(INVALID_GOODS_STATUS);
        }
        return new PageImpl<>(queryResult, pageable, count);
    }


}
