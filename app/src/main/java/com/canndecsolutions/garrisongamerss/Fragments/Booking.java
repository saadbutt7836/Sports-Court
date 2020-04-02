package com.canndecsolutions.garrisongamerss.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.canndecsolutions.garrisongamerss.Activity.SportsBooking;
import com.canndecsolutions.garrisongamerss.Models.SportsUrls;
import com.canndecsolutions.garrisongamerss.R;
import com.squareup.picasso.Picasso;


public class Booking extends Fragment implements View.OnClickListener {


    private Button Cast_Cricket_View,
            Cast_Football_view,
            Cast_Badminton_View,
            Cast_Tennis_View,
            Cast_TableTennis_View,
            Cast_Squash;

    private ImageView Cast_Cricket_Img, Cast_Football_Img, Cast_Tennis_Img, Cast_TableTennis_Img, Cast_Badminton_Img, Cast_Squash_Img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        CastingWidgets(view);

        SetImages();

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Cricket_View:
                ViewInformation("Cricket");
                break;

            case R.id.Football_View:
                ViewInformation("Football");
                break;

            case R.id.Badminton_View:
                ViewInformation("Badminton");
                break;

            case R.id.Tennis_View:
                ViewInformation("Tennis");
                break;

            case R.id.TableTennis_View:
                ViewInformation("Table Tennis");
                break;

            case R.id.Squash_View:
                ViewInformation("Squash");
                break;

        }
    }


    //    ===================================================== CALLING METHODS ============================================================


    private void CastingWidgets(View view) {

        Cast_Cricket_Img = (ImageView) view.findViewById(R.id.Cricket_Img);
        Cast_Football_Img = (ImageView) view.findViewById(R.id.Football_Img);
        Cast_Tennis_Img = (ImageView) view.findViewById(R.id.Tennis_Img);
        Cast_TableTennis_Img = (ImageView) view.findViewById(R.id.TableTennis_Img);
        Cast_Badminton_Img = (ImageView) view.findViewById(R.id.Badminton_Img);
        Cast_Squash_Img = (ImageView) view.findViewById(R.id.Squash_Img);

        Cast_Cricket_View = (Button) view.findViewById(R.id.Cricket_View);
        Cast_Football_view = (Button) view.findViewById(R.id.Football_View);
        Cast_Badminton_View = (Button) view.findViewById(R.id.Badminton_View);
        Cast_TableTennis_View = (Button) view.findViewById(R.id.TableTennis_View);
        Cast_Tennis_View = (Button) view.findViewById(R.id.Tennis_View);
        Cast_Squash = (Button) view.findViewById(R.id.Squash_View);


//        CLICK LISTENERS

        Cast_Cricket_View.setOnClickListener(this);
        Cast_Football_view.setOnClickListener(this);
        Cast_Badminton_View.setOnClickListener(this);
        Cast_TableTennis_View.setOnClickListener(this);
        Cast_Tennis_View.setOnClickListener(this);
        Cast_Squash.setOnClickListener(this);


    }

    private void SetImages() {

        Picasso.get().load(SportsUrls.CRICKET).into(Cast_Cricket_Img);
        Picasso.get().load(SportsUrls.FOOTBALL).into(Cast_Football_Img);
        Picasso.get().load(SportsUrls.TENNIS).into(Cast_Tennis_Img);
        Picasso.get().load(SportsUrls.TABLE_TENNIS).into(Cast_TableTennis_Img);
        Picasso.get().load(SportsUrls.BADMINTON).into(Cast_Badminton_Img);
        Picasso.get().load(SportsUrls.SQUASH).into(Cast_Squash_Img);

    }

    private void ViewInformation(String view) {
        Intent intent = new Intent(getActivity(), SportsBooking.class);
        intent.putExtra("view", view);
        startActivity(intent);
    }
}