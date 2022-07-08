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

        for (Object object : goodsReqDTO.getFiles()) {
            MultipartFile multipartFile = (MultipartFile) object;

            String fileUrl = s3Service.uploadFile(multipartFile);

            Files files = Files.builder()
                    .account(detailsAccount)
                    .goods(goods)
                    .fileUrl(fileUrl)
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
        Goods goods = goodsAccountTest(goodsId, account);

        for (Files files : goods.getFilesList()) {
            s3Service.deleteFile(files.getFileName());
        }
        goodsRepository.deleteById(goodsId);

        return SUCCESS;
    }

    // 게시글 수정
    // todo 수정할 때 url로 순서를 바꾸는 로직은 잘 작동 되지만, 다른 string을 요청해도 그것으로 업데이트 되므로 해당 url이 db에 존재하는 url인지 확인하는 로직이 필요하다
    // todo 수정 시 없어지는 파일들은 S3에서도 삭제하고 DB에서도 삭제해야 한다 - 사라진 url추적
    @Transactional
    public ResponseCode updateGoods(Long goodsId, GoodsReqDTO goodsReqDTO, UserDetailsImpl account) throws CustomException {
        Goods goods = goodsAccountTest(goodsId, account);
        Account detailsAccount = new Account(account);

        List<Files> filesList = goods.getFilesList();

        // 파일 외 데이터 처리
        goods.update(goodsReqDTO);

        // 파일 처리 - 수정 할 데이터를 받아서 순서대로 처리한다
        for (int i = 0; i < goodsReqDTO.getFiles().size(); i++) {
            Object object = goodsReqDTO.getFiles().get(i);

            String url;
            MultipartFile multipartFile;

            if (object instanceof MultipartFile) {
                multipartFile = (MultipartFile) object;

                String fileUrl = s3Service.uploadFile(multipartFile);

                Files files = Files.builder()
                        .account(detailsAccount)
                        .goods(goods)
                        .fileUrl(fileUrl)
                        .build();

                filesRepository.save(files);
            } else {
                url = (String) object;

                Files files = filesList.get(i);
                files.updateFilesUrl(url);
            }
        }

        goodsRepository.save(goods);
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
