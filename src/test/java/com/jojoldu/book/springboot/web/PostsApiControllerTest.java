package com.jojoldu.book.springboot.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// 검증 라이브러리
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class PostsApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @AfterEach
    public void tearDown() throws Exception {
        postsRepository.deleteAll(); // 모든 게시글 데이터 삭제
    }

    @Test
    public void Posts_등록된다() throws Exception {
        // given - 테스트에 필요한 데이터 준비
        String title = "title";
        String content = "content";

        // Builder 패턴으로 요청 DTO 생성
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then - 결과 검증
        // 1. HTTP 응답 상태코드가 200 OK인지 확인
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 2. 응답 body에 생성된 게시글 ID가 0보다 큰지 확인 (자동 생성된 ID)
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        assertThat(all.get(0).getTitle()).isEqualTo(title);       // 제목 확인
        assertThat(all.get(0).getContent()).isEqualTo(content);   // 내용 확인
    }

    @Test
    public void Posts_수정된다() throws Exception {
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        //then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }
}