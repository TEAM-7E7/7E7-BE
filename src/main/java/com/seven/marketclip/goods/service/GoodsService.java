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
import com.seven.marketclip.goods.dto.StringMultipart;
import com.seven.marketclip.goods.repository.FilesRepository;
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
        ArrayList<Object> goodsTitleResDTOList = new ArrayList<>();

        Map<String, Object> resultMap = new HashMap<>();

        for (Goods goods : goodsList) {
            goodsTitleResDTOList.add(new GoodsTitleResDTO(goods));
        }

        resultMap.put("endPage", goodsList.isLast());
        resultMap.put("goodsList", goodsTitleResDTOList);

        return new DataResponseCode(SUCCESS, resultMap);
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

        for (StringMultipart stringMultipart : goodsReqDTO.getFiles()) {
            if (stringMultipart instanceof MultipartFile) {
                MultipartFile multipartFile = stringMultipart.getMultipartFile();

                String fileUrl = s3Service.uploadFile(multipartFile);

                Files files = Files.builder()
                        .account(detailsAccount)
                        .goods(goods)
                        .fileUrl(fileUrl)
                        .build();

                filesRepository.save(files);
            }
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
        Goods goods = goodsAccountTest(goodsId, account);

        for (Files files : goods.getFilesList()) {
            s3Service.deleteFile(files.getFileUrl());
        }
        goodsRepository.deleteById(goodsId);

        return SUCCESS;
    }

    // 게시글 수정
    @Transactional
    public ResponseCode updateGoods(Long goodsId, GoodsReqDTO goodsReqDTO, UserDetailsImpl account) throws CustomException {
        Goods goods = goodsAccountTest(goodsId, account);
        Account detailsAccount = new Account(account);

        List<Files> filesList = filesRepository.findAllByGoods(goods);   // db의 파일리스트
//        List<Files> filesList = goods.getFilesList();   // db의 파일리스트
        Set<String> urlSet = new HashSet<>();   // db의 url은 이제 순서와 상관 없으므로 set 자료구조에 넣는다
        for (Files files : filesList) {
            urlSet.add(files.getFileUrl());
        }
        System.out.println("db의 모든 파일경로 url" + urlSet);

        // 파일 외 데이터 처리
        goods.update(goodsReqDTO);

        // DB의 값을 모두 지우고 새로 만들 것
        filesRepository.deleteAllByGoods(goods);

        System.out.println("goodsReqDTO: "+goodsReqDTO.getFiles());
        for (StringMultipart stringMultipart : goodsReqDTO.getFiles()) {
            if (stringMultipart instanceof MultipartFile) {
                System.out.println("multipart: "+stringMultipart.getMultipartFile().getOriginalFilename());
                MultipartFile multipartFile = stringMultipart.getMultipartFile();
                String fileUrl = s3Service.uploadFile(multipartFile);
                Files files = Files.builder()
                        .account(detailsAccount)
                        .goods(goods)
                        .fileUrl(fileUrl)
                        .build();
                filesRepository.save(files);
            } else {
                System.out.println("String URL: "+stringMultipart.getString());
                String url = stringMultipart.getString();  // 프론트에서 보낸 파일의 경로
                // DB에 존재하지 않는 url을 보낼 경우
                if (!urlSet.contains(url)) {
                    throw new CustomException(URL_NOT_FOUND);
                }
                // 기존의 urlSet 를 삭제될 url 들이 모인 list 로 만든다
                urlSet.remove(url);

                System.out.println("DB에 있는 url : " + url);

                Files file = Files.builder()
                        .account(detailsAccount)
                        .goods(goods)
                        .fileUrl(url)
                        .build();
                filesRepository.save(file);
            }
        }

        System.out.println("제외된 파일경로 url" + urlSet);

        // 수정된 파일에 포함되지 않는 url (S3삭제)
        for (String url : urlSet) {
            s3Service.deleteFile(url);
            filesRepository.deleteByFileUrl(url);
        }

//        goodsRepository.save(goods);
        return SUCCESS;
    }

    // 게시글 수정 & 삭제 - 상품 게시판 존재 여부, 작성자 아이디와 접속한 아이디 비교
    public Goods goodsAccountTest(Long goodsId, UserDetailsImpl account) {
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

    // 조회수 + 1
    @Transactional
    public void plusView(Long id) throws CustomException {
        if (!goodsRepository.existsById(id)) {
            throw new CustomException(GOODS_NOT_FOUND);
        }
        goodsRepository.updateView(id);
    }
}
