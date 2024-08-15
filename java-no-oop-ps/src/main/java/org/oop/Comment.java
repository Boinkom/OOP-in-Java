package org.oop;

public class Comment {
    Long id;
    Long articleId;
    Long userId;
    String content;

    public Comment(Long id, Long articleId, Long userId, String content) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
    }

}
