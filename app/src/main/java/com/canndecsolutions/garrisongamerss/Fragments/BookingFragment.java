package com.canndecsolutions.garrisongamerss.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Activity.SportsInfoActivity;
import com.canndecsolutions.garrisongamerss.Activity.UsersProfileActivity;
import com.canndecsolutions.garrisongamerss.R;


public class BookingFragment extends Fragment implements View.OnClickListener {


    private Button Cast_Cricket_View,
            Cast_Football_view,
            Cast_Badminton_View,
            Cast_Tennis_View,
            Cast_TableTennis_View,
            Cast_Squash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        CastingWidgets(view);
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


    private void ViewInformation(String view) {
        Intent intent = new Intent(getActivity(), SportsInfoActivity.class);
        intent.putExtra("view", view);
        startActivity(intent);
    }
}