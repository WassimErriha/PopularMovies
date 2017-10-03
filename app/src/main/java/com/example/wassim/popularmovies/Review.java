package com.example.wassim.popularmovies;

public class Review {
    private String reviewAuthor;
    private String reviewText;

    public Review(String author, String text) {
        reviewAuthor = author;
        reviewText = text;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewText() {
        return reviewText;
    }
}
