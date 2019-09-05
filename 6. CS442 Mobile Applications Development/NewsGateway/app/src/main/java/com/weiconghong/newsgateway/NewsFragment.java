package com.weiconghong.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsFragment extends Fragment implements View.OnClickListener {

    public static final String AUTHOR = "AUTHOR";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String URL = "URL";
    public static final String URLTOIMAGE = "URLTOIMAGE";
    public static final String PUBLISHEDAT = "PUBLISHEDAT";
    public static final String COUNT = "COUNT";

    public static NewsFragment newInstance (String author, String title, String description,
                                               String url, String urlToImage, String publishedAt, String count) {

        NewsFragment f = new NewsFragment();
        Bundle bdl = new Bundle(7);
        bdl.putString(AUTHOR, author);
        bdl.putString(TITLE, title);
        bdl.putString(DESCRIPTION, description);
        bdl.putString(URL, url);
        bdl.putString(URLTOIMAGE, urlToImage);
        bdl.putString(PUBLISHEDAT, publishedAt);
        bdl.putString(COUNT, count);
        f.setArguments(bdl);
        return f;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_news, container, false);

        String headline = getArguments().getString(TITLE);
        TextView headlineTextView = v.findViewById(R.id.articleHeadline);
        headlineTextView.setText(headline);
        headlineTextView.setOnClickListener(this);

        TextView dateTextView = v.findViewById(R.id.articleDate);
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            date = simpleDateFormat.parse( getArguments().getString(PUBLISHEDAT));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd, yyyy HH:mm");
        dateTextView.setText(simpleDateFormat.format(date));

        String author = getArguments().getString(AUTHOR);
        TextView authorTextView = v.findViewById(R.id.articleAuthor);
        authorTextView.setText(author);

        final ImageView imageView = v.findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        final String urlToImage = getArguments().getString(URLTOIMAGE);
        Picasso picasso = new Picasso.Builder(this.getContext()).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                final String changedUrl = urlToImage.replace("http:", "https:");
                picasso.load(changedUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);
            }
        }).build();
        picasso.load(urlToImage)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        String description = getArguments().getString(DESCRIPTION);
        TextView descriptionTextView = v.findViewById(R.id.articleText);
        descriptionTextView.setText(description);
        descriptionTextView.setOnClickListener(this);
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());

        String count = getArguments().getString(COUNT);
        TextView countTextView = v.findViewById(R.id.articleCount);
        countTextView.setText(count);

        return v;
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.articleHeadline || v.getId() == R.id.imageView || v.getId() == R.id.articleText) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getArguments().getString(URL)));
            startActivity(intent);
        }
    }
}
