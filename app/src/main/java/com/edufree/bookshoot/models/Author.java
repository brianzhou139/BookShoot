package com.edufree.bookshoot.models;

import java.util.List;

public class Author {
    private String author_name;
    private List<String> author_categories;
    private String author_thumbnail;
    private String author_id;

    public Author(){}

    public Author(String author_name, List<String> author_categories, String author_thumbnail) {
        this.author_name = author_name;
        this.author_categories = author_categories;
        this.author_thumbnail = author_thumbnail;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public List<String> getAuthor_categories() {
        return author_categories;
    }

    public void setAuthor_categories(List<String> author_categories) {
        this.author_categories = author_categories;
    }

    public String getAuthor_thumbnail() {
        return author_thumbnail;
    }

    public void setAuthor_thumbnail(String author_thumbnail) {
        this.author_thumbnail = author_thumbnail;
    }
}
