package com.green.greengram.feed;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.feed.model.FeedPicPostDto;
import com.green.greengram.feed.model.FeedPostReq;
import com.green.greengram.feed.model.FeedPostRes;
import com.green.greengram.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
@Import({ FeedServiceImpl.class })
@TestPropertySource(
        properties = {
                "file.directory=D:/2024-1/download/greengram_ver3tdd/"
        }
)
class FeedServiceTest {
    @Autowired FeedService service;
    @MockBean FeedMapper mapper;
    @MockBean CustomFileUtils customFileUtils;
    @Value("${file.directory}") String uploadPath;

    @Test
    void postFeed() throws Exception{
        FeedPostReq p = new FeedPostReq();
        // feedId 값 주입시켜줘야 한다. why -> mapper.postFeed를 통해 sql에서 keyProperty="feedId" 으로
        // pk값을 받아서 사용하는데 @MockBean이라 값을 인위적으로 넣어줘야 함.
        p.setFeedId(10);

        List<MultipartFile> pics = new ArrayList<>();
        MultipartFile fm1 = new MockMultipartFile(
                "pic", "a.jpg", "image/jpg",
                new FileInputStream(String.format("%s/test/a.jpg", uploadPath))
        );

        MultipartFile fm2 = new MockMultipartFile(
                "pic", "b.jpg", "image/jpg",
                new FileInputStream(String.format("%s/test/b.jpg", uploadPath))
        );
        pics.add(fm1);
        pics.add(fm2);
        String randomFileNm1 = "a1b2.jpg";
        String randomFileNm2 = "c1d2.jpg";
        String[] randomFileArr = { randomFileNm1, randomFileNm2 };
        given(customFileUtils.makeRandomFileName(fm1)).willReturn(randomFileNm1);
        given(customFileUtils.makeRandomFileName(fm2)).willReturn(randomFileNm2);

        FeedPostRes res = service.postFeed(pics, p);
        verify(mapper).postFeed(p);
        String path = String.format("feed/%d", p.getFeedId());
        verify(customFileUtils).makeFolders(path);

        FeedPicPostDto picDto = FeedPicPostDto
                                .builder()
                                .feedId(p.getFeedId())
                                .build();

        for(int i = 0; i < pics.size(); i++) {
            MultipartFile f = pics.get(i);
            verify(customFileUtils).makeRandomFileName(f);
            String fileNm = randomFileArr[i];
            String target = String.format("%s/%s", path, randomFileArr[i]);
            picDto.getFileNames().add(fileNm);
            verify(customFileUtils).transferTo(f, target);

        }
        verify(mapper).postFeedPics(picDto);
        FeedPostRes wantedRes = FeedPostRes
                .builder()
                .feedId(p.getFeedId())
                .pics(picDto.getFileNames())
                .build();
        assertEquals(wantedRes, res, "리턴값이 다름");
    }

    @Test
    void getFeed() {
    }
}