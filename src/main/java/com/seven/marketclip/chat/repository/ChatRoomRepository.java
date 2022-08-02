package com.seven.marketclip.chat.repository;

import com.seven.marketclip.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
//    Optional<ChatRoom> findByGoodsIdAndAccountId(Long goodsId, Long buyerId);                   //사용중인 것들 바꿔야할예정

    @Override
    @Query(value = "select * from chat_room", nativeQuery = true)
    List<ChatRoom> findAll();

    @Query("select c from ChatRoom c where c.account.id = :id or c.goods.account.id = :id")
    List<ChatRoom> roomsFindQuery(@Param("id") Long id);

    @Query("select c from ChatRoom c " +
            "where (c.goods.id = :goodsId and c.account.id = :loginId) or " +
            "(c.goods.id = :goodsId and c.account.id = :partnerId)")
    Optional<ChatRoom> roomFindQuery(Long goodsId, Long loginId, Long partnerId);

    @Query("select c from ChatRoom c " +
            "where c.goods.id = :goodsId and c.account.id = :buyerId")
    Optional<ChatRoom> oneRoomFindQuery(Long goodsId, Long buyerId);
    @Query("select count(c.id) from ChatRoom c " +
            "where c.id = :chatRoomId or " +
            "(c.goods.id = :goodsId and c.account.id = :loginId)")
    Long myRoomFindQuery(String chatRoomId, Long goodsId, Long loginId);
    //1번째 줄 방이름이 같은 경우 2번째 줄 판매 상품에 대한 방이 이미 있는경우
}