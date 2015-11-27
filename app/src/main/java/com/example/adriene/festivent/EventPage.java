package com.example.adriene.festivent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventPage extends AppCompatActivity {

    public double latitude;
    public double longitude;
    public TextView startTime, address, website, description;
    public String title, describe, sTime, eTime, adresses, url, imageUrl;
    public EventInfo event;
    public Bitmap image;
    public ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        //Intialize Image Loader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(EventPage.this));

        //get intent data
        Intent i = getIntent();
        event = (EventInfo) i.getSerializableExtra("event");
        title = event.getEventName();
        describe = event.getDescription();
        sTime = event.getStartDate();
        url = event.getUrl();
        imageUrl = event.getImageUrl();

        //load the image from url
        loadImage();

        //intialize the top toolbar and set title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        picture = (ImageView) findViewById(R.id.backImage);
        startTime = (TextView)  findViewById(R.id.textTime);
        address = (TextView) findViewById(R.id.textAddress);
        website = (TextView) findViewById(R.id.textUrl);
        description = (TextView) findViewById(R.id.textDescription);

        startTime.setText(format(sTime));
        website.setText(url);
        description.setText(describe);
        picture.setImageBitmap(image);



        try {
            latitude = Double.parseDouble(i.getStringExtra("latitude"));
            longitude = Double.parseDouble(i.getStringExtra("longitude"));
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?f=d&daddr=%f,%f?z=12", latitude, longitude);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(i);
            }
        });


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Intent i = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                 .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, sTime)
                                 .putExtra(CalendarContract.Events.TITLE, title)
                                  .putExtra(CalendarContract.Events.ALL_DAY, true)
                                  .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                          startActivity(i);

            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?f=d&daddr=%f,%f?z=12", latitude, longitude);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(i);
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventPage.this, "Description was clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String format(String sTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(event.getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String formatted = format.format(newDate);
        return formatted;
    }

    public void loadImage() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                image = loadedImage;
            }
        });
    }
}
