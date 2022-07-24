package com.seven.marketclip.image.repository;

import com.seven.marketclip.image.domain.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {
    List<GoodsImage> findAllByGoodsId(Long goodsId);

    @Query(value = "SELECT gi FROM GoodsImage gi where gi.goods is null and gi.createdAt < :localtime")
    List<GoodsImage> findAllByGoodsIdIsNull(@Param("localtime") LocalDateTime localDateTime);

    @Query(value = "SELECT gi FROM GoodsImage gi where gi.goods.id = :goodsId and gi.sequence = 1")
    GoodsImage findFirstByGoodsId(Long goodsId);

    @Query(value = "SELECT gi FROM GoodsImage gi where gi.goods.id = :goodsId order by gi.sequence asc ")
    List<GoodsImage> findAllByGoodsIdSequence(Long goodsId);
}
