package com.seven.marketclip.image.repository;

import com.seven.marketclip.image.domain.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {
    void deleteAllByGoodsId(Long goodsId);
    List<GoodsImage> findAllByGoodsId(Long goodsId);

    @Query(value = "SELECT gi FROM GoodsImage gi where gi.goods is null")
    List<GoodsImage> findAllByGoodsIdIsNull();
}
