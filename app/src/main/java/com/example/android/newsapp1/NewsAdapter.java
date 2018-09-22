
/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.newsapp1;

//This adapter will create the the view of the variables and get them linked to the news_listiem.xml file.
//It will also connect my variables and start my arraylist to see how many items can be displayed at a time.

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

        public NewsAdapter (@NonNull Context context, @NonNull List<News> news) {
            super(context, 0, news);
        }

        //I used the list view because I am not comfortable with using the gridview for ui.
        @Override
    public View getView (int position, View convertView, @NonNull ViewGroup parent) {
    View listItemView = convertView;
            if (listItemView == null) {
        listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_listitem, parent, false);
    }

            //Get the latest article details and then returning it into a view. and the View will then be repeated until ListView is full
            News currentNews = getItem(position);

    //Now have to get the object located at this position in the list view.
    TextView articleHeadline = listItemView.findViewById(R.id.headline);
    TextView articleDate = listItemView.findViewById(R.id.date);
    TextView articleSection = listItemView.findViewById(R.id.section);
    TextView articleAuthor = listItemView.findViewById(R.id.author);

    articleHeadline.setText(currentNews.getArticleHeadline());
    articleDate.setText(currentNews.getArticleDate());
    articleSection.setText(currentNews.getArticleSection());
    articleAuthor.setText(currentNews.getArticleAuthor());
    return listItemView;
}

}
