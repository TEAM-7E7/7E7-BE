package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.dto.OrderByDTO;
import com.seven.marketclip.image.service.ImageService;
import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.dto.GoodsResDTO;
import com.seven.marketclip.goods.dto.GoodsTitleResDTO;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish_list.domain.WishLists;
import com.seven.marketclip.wish_list.repository.WishListsRepository;
import com.seven.marketclip.wish_list.service.WishListsService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

import static com.seven.marketclip.exception.ResponseCode.*;
import static com.seven.marketclip.goods.enums.GoodsOrderBy.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final FileCloudService fileCloudService;
    private final ImageService imageService;
    private WishListsService wishListsService;

//    public GoodsService(GoodsRepository goodsRepository, S3CloudServiceImpl s3CloudServiceImpl, ImageService imageService, WishListsRepository wishListsRepository) {
//        this.goodsRepository = goodsRepository;
//        this.fileCloudService = s3CloudServiceImpl;
//        this.imageService = imageService;
//        this.wishListsRepository = wishListsRepository;
//    }

    @Autowired
    public void setWishListsService(WishListsService wishListsService){
        this.wishListsService = wishListsService;
    }

    public DataResponseCode pagingGoods(OrderByDTO orderByDTO, Pageable pageable) throws CustomException {
        Page<Goods> goodsPage = null;
        List<GoodsCategory> categoryList = orderByDTO.getGoodsCategoryList();
        List<String> querySentence = new ArrayList<>(Arrays.asList("where category = "));
        for (GoodsCategory goodsCategory : categoryList) {
            querySentence.add(goodsCategory.name());
        }
        String categoryListToQuery = "'" + String.join("' or '", querySentence) + "'";
        System.out.println(categoryListToQuery);

        if (orderByDTO.getGoodsOrderBy() == ORDER_BY_CREATED_AT) {
            goodsPage = goodsRepository.findAllByOrderByCreatedAtDesc(categoryListToQuery, orderByDTO.getGoodsOrderBy().getQuery(), pageable);
        } else if (orderByDTO.getGoodsOrderBy() == ORDER_BY_VIEW_COUNT) {
            goodsPage = goodsRepository.findAllByOrderByCreatedAtDesc(categoryListToQuery, orderByDTO.getGoodsOrderBy().getQuery(), pageable);
        } else if (orderByDTO.getGoodsOrderBy() == ORDER_BY_WISHLIST_COUNT) {

        } else {
            throw new CustomException(ORDER_BY_NOT_FOUND);
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
        goodsResDTO.setWishCount(wishListsService.wishListCount(goodsId));
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
        Page<WishLists> wishList = wishListsService.findMyWish(userDetails.getId(), pageable);
        Page<Goods> goodsList = wishList.map(WishLists::getGoods);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 카테고리 별 조회
    public DataResponseCode findGoodsCategory(GoodsCategory category, Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByCategoryOrderByCreatedAtDesc(category, pageable);
        Map<String, Object> resultMap = pageToMap(goodsList);
        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 즐겨찾기 갯수 순 조회
    public DataResponseCode goodsListFavorite(Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByOrderByWishListsCount(pageable);
        Map<String, Object> resultMap = pageToMap(goodsList);
        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 조회수 순 조회
    public DataResponseCode goodsListView(Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByOrderByViewCountDesc(pageable);
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
    
    // WishListService에서 호출
    public Goods findGoodsById(Long goodsId) throws CustomException {
        return goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
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
        List<GoodsTitleResDTO> goodsTitleResDTOList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        for (Goods goods : goodsList) {
            GoodsTitleResDTO goodsTitleResDTO = new GoodsTitleResDTO(goods);
            goodsTitleResDTO.setAccountImageUrl(goods.getAccount().getProfileImgUrl().getImageUrl());
            goodsTitleResDTO.setWishCount(wishListsService.wishListCount(goods.getId()));
            goodsTitleResDTOList.add(goodsTitleResDTO);
        }
        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsTitleResDTOList);
        resultMap.put("totalElements", goodsList.getTotalElements());
        return resultMap;
    }

}
