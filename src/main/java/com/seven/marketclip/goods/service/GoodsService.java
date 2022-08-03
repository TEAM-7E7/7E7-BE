package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.chat.repository.ChatRoomRepository;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.comments.GoodsReviewRepository;
import com.seven.marketclip.comments.domain.GoodsReview;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.dto.GoodsResDTO;
import com.seven.marketclip.goods.dto.GoodsTitleResDTO;
import com.seven.marketclip.goods.dto.OrderByDTO;
import com.seven.marketclip.goods.enums.GoodsStatus;
import com.seven.marketclip.goods.repository.GoodsQueryRep;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.image.service.ImageService;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish.domain.Wish;
import com.seven.marketclip.wish.repository.WishRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
@Slf4j
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final FileCloudService fileCloudService;
    private final ImageService imageService;
    private final WishRepository wishRepository;
    private final GoodsQueryRep goodsQueryRep;
    private final GoodsReviewRepository goodsReviewRepository;
    private final ChatRoomRepository chatRoomRepository;

    public GoodsService(GoodsRepository goodsRepository, S3CloudServiceImpl s3CloudServiceImpl, ImageService imageService, WishRepository wishRepository, GoodsQueryRep goodsQueryRep, GoodsReviewRepository goodsReviewRepository, ChatRoomRepository chatRoomRepository) {
        this.goodsRepository = goodsRepository;
        this.fileCloudService = s3CloudServiceImpl;
        this.imageService = imageService;
        this.wishRepository = wishRepository;
        this.goodsQueryRep = goodsQueryRep;
        this.goodsReviewRepository = goodsReviewRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    // 게시글 전체 조회 -> 동적 쿼리
    @Transactional
    public DataResponseCode pagingGoods(OrderByDTO orderByDTO, Pageable pageable) throws CustomException {
        Page<Goods> goodsPage = goodsQueryRep.pagingGoods(orderByDTO, pageable);

        return new DataResponseCode(SUCCESS, pageToMap(goodsPage));
    }

    // 게시물 이미지 파일 S3 저장
    public DataResponseCode addS3(List<MultipartFile> multipartFileList, Long accountId) throws CustomException {
        Account account = Account.builder().id(accountId).build();
        List<String> fileUrlList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            fileUrlList.add(fileCloudService.uploadFile(multipartFile));
        }
        List<Long> idList = imageService.saveGoodsImageList(fileUrlList, null, account);

        List<Map<String, Object>> idUrlMapList = new ArrayList<>();
        for (int i = 0; i < fileUrlList.size(); i++) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id", idList.get(i));
            tempMap.put("url", fileUrlList.get(i));

            idUrlMapList.add(tempMap);
        }
        return new DataResponseCode(SUCCESS, idUrlMapList);
    }

    // 게시글 작성
    @Transactional
    @CacheEvict(key = "'id:' + #userDetails.id + '__status:' + 'SALE'", cacheNames = "myGoodsCache")
    public ResponseCode addGoods(GoodsReqDTO goodsReqDTO, UserDetailsImpl userDetails) throws CustomException {
        Account detailsAccount = new Account(userDetails);
        Goods goods = Goods.builder()
                .title(goodsReqDTO.getTitle())
                .account(detailsAccount)
                .description(goodsReqDTO.getDescription())
                .category(goodsReqDTO.getCategory())
                .sellPrice(goodsReqDTO.getSellPrice())
                .build();

        // 리뷰테이블 생성
        goodsReviewRepository.save(GoodsReview.builder()
                .goods(goods)
                .build());
        imageService.updateGoodsImageList(goodsReqDTO.getFileIdList(), goods, detailsAccount);
        goodsRepository.save(goods);
        return SUCCESS;
    }

    // 상세페이지
    @Transactional
    @Cacheable(key = "#goodsId", cacheNames = "goodsCache")
    public DataResponseCode findGoodsDetail(Long goodsId) throws CustomException {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        GoodsResDTO goodsResDTO = new GoodsResDTO(goods);
        return new DataResponseCode(SUCCESS, goodsResDTO);
    }

    // 게시글 삭제
    @Transactional
    @Caching(evict = { @CacheEvict(key = "#goodsId", cacheNames = "goodsCache"),
            @CacheEvict(key = "'id:' + #userDetails.id + '__status:SOLD_OUT'", cacheNames = "myGoodsCache"),
            @CacheEvict(key = "'id:' + #userDetails.id + '__status:SALE'", cacheNames = "myGoodsCache")
    })
    public ResponseCode deleteGoods(Long goodsId, UserDetailsImpl userDetails) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, userDetails);
        // 여기에 붙여주세요
        chatRoomRepository.deleteAllByGoodsId(goodsId);
        for (GoodsImage goodsImage : goods.getGoodsImages()) {
            fileCloudService.deleteFile(goodsImage.getImageUrl());
        }
        goodsRepository.deleteById(goodsId);
        return SUCCESS;
    }

    // 게시글 수정
    @Transactional
    @Caching(evict = { @CacheEvict(key = "#goodsId", cacheNames = "goodsCache"),
            @CacheEvict(key = "'id:' + #userDetails.id + '__status:SOLD_OUT'", cacheNames = "myGoodsCache"),
            @CacheEvict(key = "'id:' + #userDetails.id + '__status:SALE'", cacheNames = "myGoodsCache")
    })
    public ResponseCode updateGoods(Long goodsId, GoodsReqDTO goodsReqDTO, UserDetailsImpl userDetails) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, userDetails);
        Account detailsAccount = new Account(userDetails);
        imageService.updateGoodsImageList(goodsReqDTO.getFileIdList(), goods, detailsAccount);
        goods.update(goodsReqDTO);
        return SUCCESS;
    }

    // 내가 쓴 글 보기
    @Cacheable(key = "'id:' + #userDetails.id + '__status:' + #goodsStatus.name()", cacheNames = "myGoodsCache", condition = "#pageable.pageNumber == 0")
    public DataResponseCode findMyGoods(UserDetailsImpl userDetails, GoodsStatus goodsStatus, Pageable pageable) {
        Page<Goods> goodsList = goodsQueryRep.findAllByAccountIdOrderByCreatedAtDesc(userDetails.getId(), goodsStatus.name(), pageable);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 내가 구매한 글 보기
    @Cacheable(key = "#userDetails.id", cacheNames = "myPurchaseCache", condition = "#pageable.pageNumber == 0")
    public DataResponseCode findMyPurchase(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllPurchaseByAccountIdOrderByCreatedAtDesc(userDetails.getId(), pageable);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 내가 즐겨찾기 한 글 보기
    @Cacheable(key = "#userDetails.id", cacheNames = "myWishCache")
    public DataResponseCode findMyWish(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Wish> wishList = wishRepository.findAllByAccountIdOrderByCreatedAtDesc(userDetails.getId(), pageable);
        Page<Goods> goodsList = wishList.map(Wish::getGoods);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 조회수 + 1
    @Transactional
    public void plusView(Long id) throws CustomException {
        goodsRepository.updateView(id);
    }

    // 게시글 수정 & 삭제 - 상품 게시판 존재 여부/ 작성자 아이디와 접속한 아이디 비교/ 둘 다 true 시 Goods 반환
    private Goods goodsAccountCheck(Long goodsId, UserDetailsImpl userDetails) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        if (!Objects.equals(goods.getAccount().getId(), userDetails.getId())) {
            throw new CustomException(NOT_AUTHORED);
        }
        return goods;
    }

    // 페이징된 결과를 response 형식으로 변환
    private Map<String, Object> pageToMap(Page<Goods> goodsList) {
        Map<String, Object> resultMap = new HashMap<>();
        List<GoodsTitleResDTO> goodsTitleResDTOList = goodsList.map(GoodsTitleResDTO::new).toList();

        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsTitleResDTOList);
        resultMap.put("totalElements", goodsList.getTotalElements());
        return resultMap;
    }

}
