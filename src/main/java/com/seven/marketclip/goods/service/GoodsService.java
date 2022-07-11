package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Files;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.GoodsCategory;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.dto.GoodsResDTO;
import com.seven.marketclip.goods.dto.GoodsTitleResDTO;
import com.seven.marketclip.goods.dto.StringMultipart;
import com.seven.marketclip.goods.repository.FilesRepository;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
@Slf4j
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final FilesRepository filesRepository;
    private final AccountRepository accountRepository;
    private final S3Service s3Service;

    public GoodsService(GoodsRepository goodsRepository, FilesRepository filesRepository, AccountRepository accountRepository, S3Service s3Service) {
        this.goodsRepository = goodsRepository;
        this.filesRepository = filesRepository;
        this.accountRepository = accountRepository;
        this.s3Service = s3Service;
    }

    // 게시글 전체 조회 - 대문사진만 보내주기
    public DataResponseCode findGoods(Pageable pageable) throws CustomException {
        Page<Goods> goodsList = goodsRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<Object> goodsTitleResDTOList = new ArrayList<>();

        Map<String, Object> resultMap = new HashMap<>();

        for (Goods goods : goodsList) {
            goodsTitleResDTOList.add(new GoodsTitleResDTO(goods));
        }

        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsTitleResDTOList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 이미지 파일 S3 저장
    public DataResponseCode addS3(MultipartFile multipartFile) throws CustomException {
        String fileUrl = s3Service.uploadFile(multipartFile);
        return new DataResponseCode(SUCCESS, fileUrl);
    }

    // 게시글 작성
    @Transactional
    public ResponseCode addGoods(GoodsReqDTO goodsReqDTO, UserDetailsImpl account) throws CustomException {
        Account detailsAccount = new Account(account);

        Goods goods = Goods.builder()
                .title(goodsReqDTO.getTitle())
                .account(detailsAccount)
                .description(goodsReqDTO.getDescription())
                .category(goodsReqDTO.getCategory())
                .sellPrice(goodsReqDTO.getSellPrice())
                .build();

        goodsRepository.save(goods);

        // 이미지 url 정리
        List<String> urlList = goodsReqDTO.getFileUrls();

        for (String url : urlList) {
            Files files = Files.builder()
                    .fileUrl(url)
                    .goods(goods)
                    .account(detailsAccount)
                    .build();

            filesRepository.save(files);
        }
        return SUCCESS;
    }

    // 상세페이지
    @Transactional  // plusView 메서드 때문에 필요하다
    public DataResponseCode findGoodsDetail(Long goodsId, UserDetailsImpl account) throws CustomException {

        if (accountRepository.findById(account.getId()).isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );

        plusView(goodsId);
        GoodsResDTO goodsResDTO = new GoodsResDTO(goods);

        return new DataResponseCode(SUCCESS, goodsResDTO);
    }

    // 게시글 삭제
    @Transactional
    public ResponseCode deleteGoods(Long goodsId, UserDetailsImpl account) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, account);

        for (Files files : goods.getFilesList()) {
            s3Service.deleteFile(files.getFileUrl());
        }
        goodsRepository.deleteById(goodsId);

        return SUCCESS;
    }

    // 게시글 수정
    @Transactional
    public ResponseCode updateGoods(Long goodsId, GoodsReqDTO goodsReqDTO, UserDetailsImpl account) throws CustomException {
        Goods goods = goodsAccountCheck(goodsId, account);
        Account detailsAccount = new Account(account);

        // 파일 외 데이터 처리
        goods.update(goodsReqDTO);

        // 이미지 url 정리
//        List<Files> filesList = goods.getFilesList();   // FetchType.LAZY 여서 작동 안되는 것 같음
        List<Files> filesList = filesRepository.findAllByGoods(goods);   // db의 파일리스트
        Set<String> existUrlSet = new HashSet<>();
        for (Files files : filesList) {
            existUrlSet.add(files.getFileUrl());
        }
        System.out.println("db의 모든 파일경로 url" + existUrlSet);

        List<String> urlList = goodsReqDTO.getFileUrls();

        for (int i = 0; i < urlList.size(); i++) {
            String url = urlList.get(i);
            Files files = filesList.get(i);
            files.updateUrl(url);
            existUrlSet.remove(url);
        }

        for (String url : existUrlSet) {
            filesRepository.deleteByFileUrl(url);
        }

        System.out.println("제외된 파일경로 url" + existUrlSet);

        // 수정된 파일에 포함되지 않는 url (S3삭제)
        for (String url : existUrlSet) {
            s3Service.deleteFile(url);
            filesRepository.deleteByFileUrl(url);
        }
//        goodsRepository.save(goods);
        return SUCCESS;
    }

    // 내가 쓴 글 보기
    public DataResponseCode findMyGoods(UserDetailsImpl userDetails, Pageable pageable) {
        Account account = new Account(userDetails);
        Page<Goods> goodsList = goodsRepository.findAllByAccount(account, pageable);

        ArrayList<Object> goodsResDTOList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        for (Goods goods : goodsList) {
            goodsResDTOList.add(new GoodsResDTO(goods));
        }

        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsResDTOList);

        return new DataResponseCode(SUCCESS, resultMap);
    }

    // 카테고리 별 조회
    public DataResponseCode findGoodsCategory(GoodsCategory category, Pageable pageable) {
        Page<Goods> goodsList = goodsRepository.findAllByCategory(category, pageable);
        List<Object> goodsTitleResDTOList = new ArrayList<>();

        Map<String, Object> resultMap = new HashMap<>();

        for (Goods goods : goodsList) {
            goodsTitleResDTOList.add(new GoodsTitleResDTO(goods));
        }

        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsTitleResDTOList);

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

    // 게시글 수정 & 삭제 - 상품 게시판 존재 여부, 작성자 아이디와 접속한 아이디 비교
    private Goods goodsAccountCheck(Long goodsId, UserDetailsImpl account) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        Account goodsAccount = goods.getAccount();
        if (goodsAccount == null) {
            throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
        }
        if (!Objects.equals(goodsAccount.getId(), account.getId())) {
            throw new CustomException(NOT_AUTHORED);
        }
        return goods;
    }

}
