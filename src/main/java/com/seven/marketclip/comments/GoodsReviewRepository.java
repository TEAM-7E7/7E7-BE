package com.seven.marketclip.comments;

import com.seven.marketclip.comments.domain.GoodsReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsReviewRepository extends JpaRepository<GoodsReview,Long> {



}
