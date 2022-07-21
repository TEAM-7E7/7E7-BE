package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.*;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsOrderBy;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.image.service.ImageService;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish.domain.Wish;
import com.seven.marketclip.wish.service.WishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

import static com.seven.marketclip.exception.ResponseCode.*;
import static com.seven.marketclip.goods.enums.GoodsOrderBy.ORDER_BY_WISHLIST_COUNT;

@Service
@Slf4j
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final FileCloudService fileCloudService;
    private final ImageService imageService;
    private final WishService wishService;
    private final EntityManager em;

    public GoodsService(GoodsRepository goodsRepository, S3CloudServiceImpl s3CloudServiceImpl, ImageService imageService, WishService wishService, EntityManager em) {
        this.goodsRepository = goodsRepository;
        this.fileCloudService = s3CloudServiceImpl;
        this.imageService = imageService;
        this.wishService = wishService;
        this.em = em;
    }

    public DataResponseCode pagingGoods(OrderByDTO orderByDTO, Pageable pageable) throws CustomException {
        String categoryListToQuery = categoryListToQuery(orderByDTO.getGoodsCategoryList());
//        String categoryListToQuery = orderByDTO.getGoodsCategoryList().get(0).getType();
        GoodsOrderBy goodsOrderBy = orderByDTO.getGoodsOrderBy();

        System.out.println("카테고리 쿼리 :" + categoryListToQuery);
        System.out.println("정렬 쿼리 :" + goodsOrderBy.getQuery());

        Page<Goods> goodsPage = null;

        String jpqlQuery = "select g from Goods as g where " + categoryListToQuery + goodsOrderBy.getQuery();
        em.createQuery(jpqlQuery,Goods.class)
                .getResultList();

        if (orderByDTO.getGoodsOrderBy() == null) {
            throw new CustomException(ORDER_BY_NOT_FOUND);
        }
        if (orderByDTO.getGoodsOrderBy() == ORDER_BY_WISHLIST_COUNT) {
//            goodsPage = goodsRepository.pagingGoodsWishList(categoryListToQuery, goodsOrderBy.getQuery(), pageable);
        } else {
            goodsPage = goodsRepository.pagingGoods(categoryListToQuery, goodsOrderBy.getQuery(), pageable);
        }

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
    public ResponseCode addGoods(GoodsReqDTO goodsReqDTO, UserDetailsImpl userDetails) throws CustomException {
        Account detailsAccount = new Account(userDetails);
        Goods goods = Goods.builder()
                .title(goodsReqDTO.getTitle())
                .account(detailsAccount)
                .description(goodsReqDTO.getDescription())
                .category(goodsReqDTO.getCategory())
                .sellPrice(goodsReqDTO.getSellPrice())
                .build();
        imageService.updateGoodsImageList(goodsReqDTO.getFileIdList(), goods, detailsAccount);
        goodsRepository.save(goods);
        return SUCCESS;
    }

    // 상세페이지
    @Transactional
    public DataResponseCode findGoodsDetail(Long goodsId) throws CustomException {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );

        plusView(goodsId);
        GoodsResDTO goodsResDTO = new GoodsResDTO(goods);
        goodsResDTO.setWishIds(wishService.goodsWishLists(goodsId));
        goodsResDTO.setAccountImageUrl(goods.getAccount().getProfileImgUrl().getImageUrl());
        return new DataResponseCode(SUCCESS, goodsResDTO);
    }

    // 게시글 삭제
    @Transactional
    public ResponseCode deleteGoods(Long goodsId, UserDetailsImpl userDetails) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, userDetails);
        for (GoodsImage goodsImage : goods.getGoodsImages()) {
            fileCloudService.deleteFile(goodsImage.getImageUrl());
        }
        goodsRepository.deleteById(goodsId);
        return SUCCESS;
    }

    // 게시글 수정
    @Transactional
    public ResponseCode updateGoods(Long goodsId, GoodsReqDTO goodsReqDTO, UserDetailsImpl userDetails) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, userDetails);
        Account detailsAccount = new Account(userDetails);
        List<Long> idList = goodsReqDTO.getFileIdList();
        imageService.deleteGoodsImages(goodsId);
        imageService.updateGoodsImageList(idList, goods, detailsAccount);
        goods.update(goodsReqDTO);
        return SUCCESS;
    }

    // 내가 쓴 글 보기
    public DataResponseCode findMyGoods(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByAccountIdOrderByCreatedAtDesc(userDetails.getId(), pageable);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 내가 즐겨찾기 한 글 보기
    public DataResponseCode findMyWish(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Wish> wishList = wishService.findMyWish(userDetails.getId(), pageable);
        Page<Goods> goodsList = wishList.map(Wish::getGoods);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 조회수 + 1
    @Transactional
    public void plusView(Long id) throws CustomException {
        if (!goodsRepository.existsById(id)) {
            throw new CustomException(GOODS_NOT_FOUND);
        }
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

    private String categoryListToQuery(List<GoodsCategory> goodsCategories) {
        if (goodsCategories.isEmpty()) {
            return "";
        } else {
            List<String> querySentence = new ArrayList<>();
            for (GoodsCategory goodsCategory : goodsCategories) {
                querySentence.add("'" + goodsCategory.name() + "'");
            }
            return " g.category = " + String.join(" or ", querySentence);
        }
    }

    // 페이징된 결과를 response 형식으로 변환
    private Map<String, Object> pageToMap(Page<Goods> goodsList) {
        List<GoodsTitleResDTO> goodsTitleResDTOList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        for (Goods goods : goodsList) {
            GoodsTitleResDTO goodsTitleResDTO = new GoodsTitleResDTO(goods);
            goodsTitleResDTO.setAccountImageUrl(goods.getAccount().getProfileImgUrl().getImageUrl());
            goodsTitleResDTO.setWishIds(wishService.goodsWishLists(goods.getId()));
            goodsTitleResDTOList.add(goodsTitleResDTO);
        }
        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsTitleResDTOList);
        resultMap.put("totalElements", goodsList.getTotalElements());
        return resultMap;
    }

}
