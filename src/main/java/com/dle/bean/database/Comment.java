package com.dle.bean.database;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "COMMENT")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "creator")
    String creator;

    @Column(name = "lan")
    Integer lan;

    @Column(name = "commentator")
    String commentator;

    @Column(name = "type")// CHAT_MESSAGE, SOCIAL_MESSAGE
    String type;

    @Column(name = "content")
    String content;

    @Column(name = "createDate")
    Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public Integer getLan() {
        return lan;
    }

    public void setLan(Integer lan) {
        this.lan = lan;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCommentator() {
        return commentator;
    }

    public void setCommentator(String commentator) {
        this.commentator = commentator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
