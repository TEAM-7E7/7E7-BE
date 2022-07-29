package com.seven.marketclip.chat.repository;

import com.seven.marketclip.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
//    Optional<ChatRoom> findByGoodsIdAndAccountId(Long goodsId, Long buyerId);                   //사용중인 것들 바꿔야할예정
                                                                                                //에러 말고 api 호출 있는건 어떤지?
    @Query("select c from ChatRoom c where c.account.id = :id or c.goods.account.id = :id")
    List<ChatRoom> roomsFindQuery(@Param("id") Long id);
    @Query("select c from ChatRoom c " +
            "where (c.goods.id = :goodsId and c.account.id = :loginId) or " +
            "(c.goods.id = :goodsId and c.account.id = :partnerId)")
    Optional<ChatRoom> roomFindQuery(Long goodsId, Long loginId, Long partnerId);
}