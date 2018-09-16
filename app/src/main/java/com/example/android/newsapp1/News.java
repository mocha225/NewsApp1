package com.example.android.newsapp1;


/**
     * {@News} represents an news event. It holds the details
     * of that event such as headline
     * as well as date, author and section of where the article is.
     */
    public class News {
        private String mArticleHeadline, mArticleDate, mArticleSection, mArticleAuthor, mArticleUrl;

        public News (String articleHeadline, String articleDate, String articleSection, String articleAuthor, String articleUrl) {
         mArticleHeadline = articleHeadline;
         mArticleDate = articleDate;
         mArticleSection = articleSection;
         mArticleAuthor = articleAuthor;
            String mArticleUrl = articleUrl;
        }

    public String getArticleHeadline() {
        return mArticleHeadline;
    }

    public String getArticleDate() {
        return mArticleDate;
    }

    public String getArticleSection() {
        return mArticleSection;
    }

    public String getArticleAuthor() {
        return mArticleSection;
    }

    public String getUrl() {
        return mArticleUrl;
    }
}
