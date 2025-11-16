package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.Post;
import jbch.org.sideproject.repository.PostRepository;
import jbch.org.sideproject.request.PostCreate;
import jbch.org.sideproject.request.PostEdit;
import jbch.org.sideproject.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Page<PostResponse> list(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostResponse::new);
    }

    public PostResponse read(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        return new PostResponse(post);
    }

    public void write(PostCreate postCreate) {
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void modify(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        post.modify(postEdit);
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        postRepository.delete(post);
    }
}
