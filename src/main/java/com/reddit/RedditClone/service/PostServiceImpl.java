package com.reddit.RedditClone.service;

import com.reddit.RedditClone.model.Image;
import com.reddit.RedditClone.model.Post;
import com.reddit.RedditClone.model.Subreddit;
import com.reddit.RedditClone.repository.PostRepository;
import com.reddit.RedditClone.repository.SubredditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements  PostService{

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SubredditRepository subredditRepository;

    @Autowired
    private AWSService awsService;

    @Override
    public Post savePost(Post post) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        Optional<Subreddit> subreddit = subredditRepository.findById(post.getSubredditId());
        String url = awsService.uploadFile(post.getImage());
        List<Image> images = new ArrayList<>();
        Image image = new Image();
        image.setUrls(url);
        images.add(image);
        post.setImages(images);
        post.setCreatedAt(timestamp);
        post.setUpdatedAt(timestamp);
        post.setSubredditId(subreddit.get().getId());
        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getBySubReditId(Long id) {
        return postRepository.findAllBySubredditId(id);
    }
}