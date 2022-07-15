package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.cloudServer.service.FileCloudService;
import com.seven.marketclip.cloudServer.service.S3CloudServiceImpl;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.files.service.FileService;
import com.seven.marketclip.files.domain.GoodsImage;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.GoodsCategory;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.dto.GoodsResDTO;
import com.seven.marketclip.goods.dto.GoodsTitleResDTO;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
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
    private final FileService fileService;

    public GoodsService(GoodsRepository goodsRepository, S3CloudServiceImpl s3CloudServiceImpl, FileService fileService) {
        this.goodsRepository = goodsRepository;
        this.fileCloudService = s3CloudServiceImpl;
        this.fileService = fileService;
    }

    // 게시글 전체 조회 - 대문사진만 보내주기
    public DataResponseCode findGoods(Pageable pageable) throws CustomException {
        Page<Goods> goodsList = goodsRepository.findAllByOrderByCreatedAtDesc(pageable);
        return new DataResponseCode(SUCCESS, pageToMap(goodsList));
    }

    // 이미지 파일 S3 저장
    public DataResponseCode addS3(List<MultipartFile> multipartFileList) throws CustomException {
        List<String> fileUrlList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            fileUrlList.add(fileCloudService.uploadFile(multipartFile));
        }
        return new DataResponseCode(SUCCESS, fileUrlList);
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
        fileService.saveGoodsImageList(goodsReqDTO.getFileUrls(), goods, detailsAccount);
        return SUCCESS;
    }

    // 상세페이지
    @Transactional  // plusView 메서드 때문에 필요하다
    public DataResponseCode findGoodsDetail(Long goodsId) throws CustomException {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        plusView(goodsId);
        return new DataResponseCode(SUCCESS, new GoodsResDTO(goods));
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

    // 게시글 수정 - 수정되면서 삭제된 이미지 파일을 S3에서 바로 지워주는 로직 제거함
    @Transactional
    public ResponseCode updateGoods(Long goodsId, GoodsReqDTO goodsReqDTO, UserDetailsImpl userDetails) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, userDetails);
        Account detailsAccount = new Account(userDetails);
        goods.update(goodsReqDTO);
//        List<GoodsImage> filesList = goods.getGoodsImages();   // FetchType.LAZY 여서 작동 안되는 것 같음
        List<String> urlList = goodsReqDTO.getFileUrls();
        fileService.deleteGoodsImages(goodsId);
        fileService.saveGoodsImageList(urlList, goods, detailsAccount);
        return SUCCESS;
    }

    // 내가 쓴 글 보기
    public DataResponseCode findMyGoods(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByAccountId(userDetails.getId(), pageable);
        Map<String, Object> resultMap = pageToMap(goodsList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 카테고리 별 조회
    public DataResponseCode findGoodsCategory(GoodsCategory category, Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByCategory(category, pageable);
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

    // 페이징된 결과를 response 형식으로 변환
    private Map<String, Object> pageToMap(Page<Goods> goodsList) {
        List<GoodsTitleResDTO> goodsTitleResDTOList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        for (Goods goods : goodsList) {
            goodsTitleResDTOList.add(new GoodsTitleResDTO(goods));
        }
        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsTitleResDTOList);
        return resultMap;
    }

}
