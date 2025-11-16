package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.Post;
import jbch.org.sideproject.repository.PostRepository;
import jbch.org.sideproject.request.PostCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void write(PostCreate postCreate) {
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();

        postRepository.save(post);
    }
}
