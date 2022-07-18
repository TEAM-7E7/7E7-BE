package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.image.service.ImageService;
import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.dto.GoodsResDTO;
import com.seven.marketclip.goods.dto.GoodsTitleResDTO;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wishList.service.WishListsService;
import lombok.extern.slf4j.Slf4j;
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
    private final WishListsService wishListsService;

    public GoodsService(GoodsRepository goodsRepository, S3CloudServiceImpl s3CloudServiceImpl, ImageService imageService, WishListsService wishListsService) {
        this.goodsRepository = goodsRepository;
        this.fileCloudService = s3CloudServiceImpl;
        this.imageService = imageService;
        this.wishListsService = wishListsService;
    }

    // 게시글 전체 조회 - 대문사진만 보내주기
    public DataResponseCode findGoods(Pageable pageable) throws CustomException {
        Page<Goods> goodsList = goodsRepository.findAllByOrderByCreatedAtDesc(pageable);
        return new DataResponseCode(SUCCESS, pageToMap(goodsList));
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
        for(int i = 0; i < fileUrlList.size(); i++){
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id",idList.get(i));
            tempMap.put("url",fileUrlList.get(i));

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
        goodsRepository.save(goods);
        imageService.updateGoodsImageList(goodsReqDTO.getFileIdList(), goods, detailsAccount);
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

    //

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
