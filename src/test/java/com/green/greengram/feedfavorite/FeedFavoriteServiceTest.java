package com.green.greengram.feedfavorite;

import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import com.green.greengram.userfollow.UserFollowServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({ FeedFavoriteServiceImpl.class })
class FeedFavoriteServiceTest {

    @MockBean
    private FeedFavoriteMapper mapper;

    @Autowired
    private FeedFavoriteService service;

    @Test
    void toggleFavorite() {
        // given - when - then
        FeedFavoriteToggleReq p1 = new FeedFavoriteToggleReq(1, 2); // return 0을 원함

        FeedFavoriteToggleReq p2 = new FeedFavoriteToggleReq(10, 20); // return 1을 원함


        given(mapper.delFeedFavorite(p1)).willReturn(0);
        given(mapper.delFeedFavorite(p2)).willReturn(1);

        given(mapper.insFeedFavorite(p1)).willReturn(100);
        given(mapper.insFeedFavorite(p2)).willReturn(200);

        int result1 = service.toggleFavorite(p1);
        assertEquals(100, result1);

        int result2 = service.toggleFavorite(p2);
        assertEquals(0, result2);

        verify(mapper, times(1)).delFeedFavorite(p1);
        verify(mapper, times(1)).delFeedFavorite(p2);

        verify(mapper, times(1)).insFeedFavorite(p1);

        verify(mapper, never()).insFeedFavorite(p2);

    }
}