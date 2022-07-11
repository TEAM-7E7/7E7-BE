package com.seven.marketclip.goods.repository;

import com.seven.marketclip.goods.domain.Files;
import com.seven.marketclip.goods.domain.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilesRepository extends JpaRepository<Files, Long> {
    void deleteAllByGoods(Goods goods);
    void deleteByFileUrl(String url);
    List<Files> findAllByGoods(Goods goods);
}
