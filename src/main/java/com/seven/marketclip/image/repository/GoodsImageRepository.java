package com.seven.marketclip.image.repository;

import com.seven.marketclip.image.domain.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {
    void deleteAllByGoodsId(Long goodsId);
    List<GoodsImage> findAllByGoodsId(Long goodsId);

    @Query(value = "SELECT gi.imageUrl FROM GoodsImage gi where gi.goods is null")
    List<String> findAllByGoodsIdIsNull();

//    @Modifying
//    @Query(value = "DELETE FROM GoodsImage gi where gi.goods is null")
//    void deleteAllByGoodsIdIsNull();

    @Modifying
    @Query(value = "DELETE FROM GoodsImage gi where gi.goods is null and gi.createdAt < :localtime")
    void deleteAllByGoodsIdIsNull(@Param("localtime") LocalDateTime localDateTime);


    /*@Transactional
    @Query(value = "DELETE FROM goods_image as gi1 where gi1.image_url IN (SELECT temp_tbl.* FROM (select image_url from goods_image as gi2 WHERE gi2.goods_id is null) temp_tbl)", nativeQuery = true)
    List<String> findAndDeleteAllByGoodsIdIsNull();
    @Transactional
    @Query(value = "SELECT image_url FROM goods_image as gi1 where gi1.image_url IN (SELECT temp_tbl.* FROM (select image_url from goods_image as gi2 WHERE gi2.goods_id is null) temp_tbl)", nativeQuery = true)
    List<String> findAndDeleteAllByGoodsIdIsNull();*/

}
