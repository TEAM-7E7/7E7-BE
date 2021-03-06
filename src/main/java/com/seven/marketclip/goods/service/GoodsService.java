package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
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
import com.seven.marketclip.image.repository.GoodsImageRepository;
import com.seven.marketclip.image.service.ImageService;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish.domain.Wish;
import com.seven.marketclip.wish.service.WishService;
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
    private final WishService wishService;
    private final GoodsQueryRep goodsQueryRep;
    private final GoodsImageRepository goodsImageRepository;

    public GoodsService(GoodsRepository goodsRepository, S3CloudServiceImpl s3CloudServiceImpl, ImageService imageService, WishService wishService, GoodsQueryRep goodsQueryRep, GoodsImageRepository goodsImageRepository) {
        this.goodsRepository = goodsRepository;
        this.fileCloudService = s3CloudServiceImpl;
        this.imageService = imageService;
        this.wishService = wishService;
        this.goodsQueryRep = goodsQueryRep;
        this.goodsImageRepository = goodsImageRepository;
    }

    // ????????? ?????? ?????? -> ?????? ??????
    public DataResponseCode pagingGoods(OrderByDTO orderByDTO, Pageable pageable) throws CustomException {
        Page<Goods> goodsPage = goodsQueryRep.pagingGoods(orderByDTO, pageable);

        return new DataResponseCode(SUCCESS, pageToMap(goodsPage));
    }

    // ????????? ????????? ?????? S3 ??????
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

    // ????????? ??????
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

    // ???????????????
    @Transactional
    public DataResponseCode findGoodsDetail(Long goodsId) throws CustomException {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );

        plusView(goodsId);
        GoodsResDTO goodsResDTO = new GoodsResDTO(goods);
        goodsResDTO.setImageMapList(goodsImageRepository.findAllByGoodsIdSequence(goodsId));
        goodsResDTO.setWishIds(wishService.goodsWishLists(goodsId));
        goodsResDTO.setAccountImageUrl(goods.getAccount().getProfileImgUrl().getImageUrl());
        return new DataResponseCode(SUCCESS, goodsResDTO);
    }

    // ????????? ??????
    @Transactional
    public ResponseCode deleteGoods(Long goodsId, UserDetailsImpl userDetails) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, userDetails);
        for (GoodsImage goodsImage : goods.getGoodsImages()) {
            fileCloudService.deleteFile(goodsImage.getImageUrl());
        }
        goodsRepository.deleteById(goodsId);
        return SUCCESS;
    }

    // ????????? ??????
    @Transactional
    public ResponseCode updateGoods(Long goodsId, GoodsReqDTO goodsReqDTO, UserDetailsImpl userDetails) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, userDetails);
        Account detailsAccount = new Account(userDetails);
        imageService.updateGoodsImageList(goodsReqDTO.getFileIdList(), goods, detailsAccount);
        goods.update(goodsReqDTO);
        return SUCCESS;
    }

    // ?????? ??? ??? ??????
    public DataResponseCode findMyGoods(UserDetailsImpl userDetails, GoodsStatus goodsStatus, Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByAccountIdOrderByCreatedAtDesc(userDetails.getId(), goodsStatus.name(), pageable);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // ?????? ???????????? ??? ??? ??????
    public DataResponseCode findMyWish(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Wish> wishList = wishService.findMyWish(userDetails.getId(), pageable);
        Page<Goods> goodsList = wishList.map(Wish::getGoods);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // ????????? + 1
    @Transactional
    public void plusView(Long id) throws CustomException {
        if (!goodsRepository.existsById(id)) {
            throw new CustomException(GOODS_NOT_FOUND);
        }
        goodsRepository.updateView(id);
    }

    // ????????? ?????? & ?????? - ?????? ????????? ?????? ??????/ ????????? ???????????? ????????? ????????? ??????/ ??? ??? true ??? Goods ??????
    private Goods goodsAccountCheck(Long goodsId, UserDetailsImpl userDetails) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        if (!Objects.equals(goods.getAccount().getId(), userDetails.getId())) {
            throw new CustomException(NOT_AUTHORED);
        }
        return goods;
    }

    // ???????????? ????????? response ???????????? ??????
    private Map<String, Object> pageToMap(Page<Goods> goodsList) {
        List<GoodsTitleResDTO> goodsTitleResDTOList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        for (Goods goods : goodsList) {
            GoodsTitleResDTO goodsTitleResDTO = new GoodsTitleResDTO(goods);
            goodsTitleResDTO.setGoodsImageUrl(goodsImageRepository.findFirstByGoodsId(goods.getId()).getImageUrl());
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
