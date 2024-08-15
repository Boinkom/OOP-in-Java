package org.oop.api;

import org.oop.model.Comment;

import java.util.List;

public interface ICommentService {
    boolean addComment(String articleId, Comment comment);
    List<Comment> getComments(String articleId);
}