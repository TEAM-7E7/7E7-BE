package com.seven.marketclip.goods.repository;

import com.seven.marketclip.goods.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilesRepository extends JpaRepository<Files, Long> {
    void deleteAllByGoods_Id(Long goodsId);
    void deleteByFileUrl(String url);
    List<Files> findAllByGoods_Id(Long goodsId);
}
