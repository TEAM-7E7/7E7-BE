package com.seven.marketclip.image.repository;

import com.seven.marketclip.image.domain.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {
    void deleteAllByGoodsId(Long goodsId);
    List<GoodsImage> findAllByGoodsId(Long goodsId);

    @Query(value = "SELECT gi.imageUrl FROM GoodsImage gi where gi.goods is null")
    List<String> findAllByGoodsIdIsNull();

    @Modifying
    @Query(value = "DELETE FROM GoodsImage gi where gi.goods is null and gi.createdAt < :localtime")
    void deleteAllByGoodsIdIsNull(@Param("localtime") LocalDateTime localDateTime);

}
