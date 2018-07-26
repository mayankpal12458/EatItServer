package com.example.dell.eatitserver;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dell.eatitserver.Common.Common;
import com.example.dell.eatitserver.Common.DirectionJSONParser;
import com.example.dell.eatitserver.Remote.IGeoCoordinates;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{



    private GoogleMap mMap;
    private final static int Play_Services_Resolution_Request=1000;
    private final static int Location_Request=1001;

    private Location lastlocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static int Update_Interval=1000;
    private static int Fastest_Interval=5000;
    private static int Displacement=10;
    private IGeoCoordinates mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);
        mService= Common.getGeoCodeService();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            requestruntimepermission();
        }else{
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
            }

        }
        displaylocation();

       SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displaylocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            requestruntimepermission();
        }
        else
        {
            lastlocation=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(lastlocation!=null)
            {
                double latitude=lastlocation.getLatitude();
                double longitude=lastlocation.getLongitude();

                LatLng yourloc=new LatLng(latitude,longitude);
                mMap.addMarker(new MarkerOptions().position(yourloc).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourloc));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                //After adding marker for your loc add marker for this loc and draw route
                drawroute(yourloc,Common.currentrequest.getAddress());
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Could not get Location",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void drawroute(final LatLng yourloc, String address) {

        mService.getgeocode(address).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    JSONObject jsonobj=new JSONObject(response.body().toString());

                    String lat=((JSONArray)jsonobj.get("results"))
                            .getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();

                    String lon=((JSONArray)jsonobj.get("results"))
                            .getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lon").toString();

                    LatLng orderloc=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.homegooglemap);
                    bitmap=Common.scaleBitmap(bitmap,70,70);

                    MarkerOptions marker=new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title("Order of"+Common.currentrequest.getPhone())
                            .position(orderloc);

                    mMap.addMarker(marker);

                    //draw route
                    mService.getDirections(yourloc.latitude+","+yourloc.longitude,orderloc.latitude+","+orderloc.longitude)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    new ParserTask().execute(response.body().toString());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void createLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(Update_Interval);
        locationRequest.setFastestInterval(Fastest_Interval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(Displacement);
    }

    protected void buildGoogleApiClient() {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

    }

    private boolean checkPlayServices() {
        int resultcode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultcode!=ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultcode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultcode,this,Play_Services_Resolution_Request).show();
            }else{
                Toast.makeText(getApplicationContext(),"Not Supported in this device....",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void requestruntimepermission() {
        ActivityCompat.requestPermissions(this,new String[]
                {

                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },Location_Request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case Location_Request:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displaylocation();
                    }
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displaylocation();
        startlocationupdates();

    }

    private void startlocationupdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation=location;
        displaylocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient!=null)
            googleApiClient.connect();
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
        ProgressDialog mdialog=new ProgressDialog(TrackingOrder.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mdialog.setMessage("Please Wait....");
            mdialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {
            JSONObject jobj;
            List<List<HashMap<String, String>>> routes=null;
            try{
                jobj=new JSONObject(params[0]);
                DirectionJSONParser parser=new DirectionJSONParser();
                routes=parser.parse(jobj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;

        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mdialog.dismiss();
            ArrayList points=null;
            PolylineOptions options=null;
            for(int i=0;i< lists.size();i++)
            {
                points=new ArrayList();
                options=new PolylineOptions();

                List<HashMap<String, String>> path=lists.get(i);

                for(int j=0;j<path.size();j++)
                {
                    HashMap<String,String> point=path.get(j);

                    double lat=Double.parseDouble(point.get("lat"));
                    double lon=Double.parseDouble(point.get("lon"));

                    LatLng position=new LatLng(lat,lon);

                    points.add(position);

                }

                options.addAll(points);
                options.width(12);
                options.color(Color.BLUE);
                options.geodesic(true);

            }

            mMap.addPolyline(options);
        }
    }
}
