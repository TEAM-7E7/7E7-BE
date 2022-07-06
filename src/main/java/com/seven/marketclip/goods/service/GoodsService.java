package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Files;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.dto.GoodsResDTO;
import com.seven.marketclip.goods.dto.GoodsTitleResDTO;
import com.seven.marketclip.goods.repository.FilesRepository;
import com.seven.marketclip.goods.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
@Slf4j
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final FilesRepository filesRepository;
    private final AccountRepository accountRepository;
    private final S3Uploader s3Uploader;

    public GoodsService(GoodsRepository goodsRepository, FilesRepository filesRepository, AccountRepository accountRepository, S3Uploader s3Uploader) {
        this.goodsRepository = goodsRepository;
        this.filesRepository = filesRepository;
        this.accountRepository = accountRepository;
        this.s3Uploader = s3Uploader;
    }

    // 게시글 전체 조회 - 대문사진만 보내주기
    // todo 테스트 완료 - GoodsResTitleDTO를 새로 만든게 마음에 안들어서 기존의 GoodsResDTO에 메소드를 작성해서 테스트 해봄
    public DataResponseCode findGoods() throws CustomException {
        List<Goods> goodsList = goodsRepository.findAllByOrderByCreatedAtDesc();
        List<GoodsTitleResDTO> goodsTitleResDTOList = new ArrayList<>();

        for (Goods goods : goodsList) {
            goodsTitleResDTOList.add(new GoodsTitleResDTO(goods));
        }
        return new DataResponseCode(GOODS_BOARD_SUCCESS, goodsTitleResDTOList);
    }

//    // todo 테스트 완료 - GoodsResDTO로 return하면 하나의 리스트에 url을 넣어 보내준다
//    public DataResponseCode findGoods() throws CustomException {
//        List<Goods> goodsList = goodsRepository.findAllByOrderByCreatedAtDesc();
//        List<GoodsResDTO> goodsResDTOList = new ArrayList<>();
//
//        for(Goods goods : goodsList){
//            goodsResDTOList.add(new GoodsResDTO().getFirstTitleDTO(goods));
//        }
//        return new DataResponseCode(GOODS_BOARD_SUCCESS, goodsResDTOList);
//    }

    // 게시글 작성
    @Transactional
    public ResponseCode addGoods(GoodsReqDTO goodsReqDTO) throws CustomException {

        // todo 회원정보를 이용하는 테스트는 아직 못했음
//    public ResponseCode addGoods(GoodsReqDTO goodsReqDTO, Account account) throws CustomException {

        Goods goods = Goods.builder()
                .title(goodsReqDTO.getTitle())
                .description(goodsReqDTO.getDescription())
                .category(goodsReqDTO.getCategory())
                .sellPrice(goodsReqDTO.getSellPrice())
                .build();

        goodsRepository.save(goods);

        for (MultipartFile multipartFile : goodsReqDTO.getFiles()) {
            String fileUrl = multipartFile == null ? null : s3Uploader.uploadFile(multipartFile);

            Files files = Files.builder()
                    .goods(goods)
                    .fileURL(fileUrl)
                    .build();

            filesRepository.save(files);
        }

        return GOODS_POST_SUCCESS;

    }

    /*@Transactional
    public ResponseCode addGoods(GoodsReqDTO goodsReqDTO, Account account) throws CustomException {
//    public ResponseCode addGoods(GoodsReqDTO goodsReqDTO, Account account) throws CustomException {

//        String fileUrl = goodsReqDTO.getFile() == null ? null: s3Uploader.uploadFile(goodsReqDTO.getFile());
//        log.error(fileUrl);

        Goods goods = Goods.builder()
                .account(account)
                .title(goodsReqDTO.getTitle())
                .description(goodsReqDTO.getDescription())
                .category(goodsReqDTO.getCategory())
                .sellPrice(goodsReqDTO.getSellPrice())
                .build();

        goodsRepository.save(goods);

        for (MultipartFile multipartFile : goodsReqDTO.getFiles()) {
            String fileUrl = multipartFile == null ? null : s3Uploader.uploadFile(multipartFile);

            Files files = Files.builder()
                    .account(account)
                    .goods(goods)
                    .fileURL(fileUrl)
                    .build();

            filesRepository.save(files);
        }

        return GOODS_POST_SUCCESS;

//            goodsRepository.save(new Goods(goodsReqDTO, fileUrl, account));
    }*/

    // 상세페이지
    public DataResponseCode findGoodsDetail(Long goodsId, Account account) throws CustomException {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        accountRepository.findById(account.getId()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        plusView(goodsId);
        GoodsResDTO goodsResDTO = new GoodsResDTO(goods);

        return new DataResponseCode(GOODS_DETAIL_SUCCESS, goodsResDTO);
    }

    // 게시글 삭제
    public ResponseCode deleteGoods(Long goodsId, Account account) throws CustomException {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        if (goods.getAccount().equals(account)) {
            goodsRepository.deleteById(goodsId);
        } else {
            throw new CustomException(NOT_AUTHOR);
        }
        return GOODS_DELETE_SUCCESS;
    }

    // 게시글 수정
    @Transactional
    public ResponseCode updateGoods(Long id, GoodsReqDTO goodsReqDTO, Account account) throws CustomException {
        Goods goods = goodsRepository.findById(id).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
//        if (goods.getFileUrl() != null) s3Uploader.deleteFile(goods.getFileUrl());
//        String updateFileUrl = goodsReqDTO.getFile() == null ? null : s3Uploader.uploadFile(goodsReqDTO.getFile());
//        goods.update(goodsReqDTO, updateFileUrl);

        goodsRepository.save(goods);
        return GOODS_UPDATE_SUCCESS;
    }

    @Transactional
    public void plusView(Long id) throws CustomException {
        if (!goodsRepository.existsById(id)) {
            throw new CustomException(GOODS_NOT_FOUND);
        }
        goodsRepository.updateView(id);
    }
}
