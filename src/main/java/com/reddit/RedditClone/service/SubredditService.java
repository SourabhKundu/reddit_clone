package com.reddit.RedditClone.service;

import com.reddit.RedditClone.model.Subreddit;

import java.util.List;

public interface SubredditService {
    Subreddit saveSubreddit(Subreddit subreddit);

    List<Subreddit> findAllSubreddits();

    Subreddit getRedditById(Long id);

    List<Subreddit> getSearchedPosts(String keyword);
}
