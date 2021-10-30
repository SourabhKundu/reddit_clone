package com.reddit.RedditClone.controller;

import com.amazonaws.services.apigateway.model.Model;
import com.reddit.RedditClone.model.Comment;
import com.reddit.RedditClone.model.Post;
import com.reddit.RedditClone.model.User;
import com.reddit.RedditClone.repository.CommentRepository;
import com.reddit.RedditClone.repository.PostRepository;
import com.reddit.RedditClone.service.CommentService;
import com.reddit.RedditClone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class CommentController {

    @Autowired
    public CommentService commentService;

    @Autowired
    public PostRepository postRepo;

    @Autowired
    public UserService userService;

    @Autowired
    public CommentRepository commentRepository;


    @ResponseBody
    public List<Comment> getComments(@PathVariable Long postId) {
        List<Comment> findByPostId = commentService.findByPostId(postId);
        return findByPostId;
    }

    @PostMapping("/saveComment/{postId}/comments")
    public String postComment(@PathVariable Long postId, Comment rootComment,
                              @RequestParam(required = false) Long parentId,
                              @RequestParam(required = false) String childCommentText) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }


        commentService.saveComment(postId, rootComment, parentId, childCommentText);
        return "redirect:/viewPost/" + postId;
    }

    @RequestMapping("/deleteComment/{id}")
    private String deleteComment(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        comment.setComment(null);
        commentRepository.save(comment);
        commentService.deleteById(id);
        return "redirect:/viewPost/" + comment.getPost().getId();
    }


    @GetMapping("/commentUpvote/{cmtId}")
    public String upvoteComment(@PathVariable(name = "cmtId") Long cmtId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        Comment comment = commentService.getById(cmtId);
        Set<User> upVotes = comment.getUpVotes();
        Set<User> downVotes = comment.getDownVotes();

        if (upVotes.contains(user)) {
            upVotes.remove(user);
        } else {
            if (downVotes.contains(user)) {
                downVotes.remove(user);
            }
            upVotes.add(user);
        }
        comment.setUpVotes(upVotes);
        comment.setDownVotes(downVotes);
        commentRepository.save(comment);
        return "redirect:/viewPost/" + comment.getPost().getId();
    }

    @GetMapping("/commentDownvote/{cmtId}")
    public String commentDownvote(@PathVariable(name = "cmtId") Long cmtId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        Comment comment = commentService.getById(cmtId);
        Set<User> upVotes = comment.getUpVotes();
        Set<User> downVotes = comment.getDownVotes();

        if (downVotes.contains(user)) {
            downVotes.remove(user);
        } else {
            if (upVotes.contains(user)) {
                upVotes.remove(user);
            }
            downVotes.add(user);
        }

        comment.setUpVotes(upVotes);
        comment.setDownVotes(downVotes);
        commentRepository.save(comment);
        return "redirect:/viewPost/" + comment.getPost().getId();
    }


}
