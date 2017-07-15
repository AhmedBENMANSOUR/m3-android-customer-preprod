package com.dioolcustomer.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.bumptech.glide.Glide;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.fragments.BalanceFragment;
import com.dioolcustomer.fragments.FindShopFragment;
import com.dioolcustomer.fragments.ListNetworkFragment;
import com.dioolcustomer.fragments.PaymentFragment;
import com.dioolcustomer.fragments.RequestFragment;
import com.dioolcustomer.fragments.TabLayoutFragment;
import com.dioolcustomer.fragments.TransactionsHistoryFragment;
import com.dioolcustomer.fragments.TransfertFragment;
import com.dioolcustomer.fragments.UserProfilFragment;
import com.dioolcustomer.models.ShopsByOwnerId;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.ui.AlertDialogRadio;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ServiceSelectionActivity extends AppCompatActivity implements AlertDialogRadio.AlertPositiveListener
        ,AlertDialogRadio.AlertNegativeListener{

    public final String TAG = getClass().getSimpleName();

    ImageView mPhotoUser;
    TextView mTextBalance, mTextRevenu;

    String daBalance = "";
    String revBalance = "";


    UserProfile mUserProfile;
    Token mUserToken;
    Gson gson;
    JsonObjectRequest jsonObjReq;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;

    Encryption encryption = new Encryption();

    private NavigationView nvDrawer;
    private DrawerLayout mDrawer;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    String[] names = {"android","java","spring","html"};
    DrawerLayout drawerLayout;
    NavigationView navigation;

    private ActionBarDrawerToggle drawerToggle;


    String mFirstName = "";
    String mLastName = "";
    String mPhone = "";
    String mAdresseMail = "";
    String mAdress = "";


    private ShopsByOwnerId mShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   // getSupportActionBar().setDisplayShowTitleEnabled(true);
      /*  ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_service_selection2);
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();
        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);
        String jsonProfile = null;
        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }













        ////////////// navigation drawer ///////////


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(ServiceSelectionActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.setHomeAsUpIndicator(R.color.white);


        nvDrawer = (NavigationView)findViewById(R.id.navigation_view);
        mPhotoUser = (ImageView) findViewById(R.id.photo_user);
        TextView name = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.name);
        TextView email = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.email);
        ImageView profilePictureView = (ImageView) nvDrawer.getHeaderView(0).findViewById(R.id.photo_user);
        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);
        Glide.with(this).load(mUserProfile.getPictureURL().toString()).into(profilePictureView);

        try {
            mFirstName = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mLastName = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_LASTNAME_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mAdresseMail = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }



        try {
            if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("super_merchant"))
                nvDrawer.getMenu().findItem(R.id.navigation_item_4).setVisible(true);
            else
                nvDrawer.getMenu().findItem(R.id.navigation_item_4).setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = TabLayoutFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        name.setText(mFirstName+" "+mLastName);
        email.setText(mAdresseMail);
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                Class fragmentClass = null;

                if(menuItem.isChecked()){
                    menuItem.setChecked(false);
                } else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();



                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.navigation_item_1:
                        fragmentClass = TabLayoutFragment.class;
                          /*  Toast.makeText(getApplicationContext(),"Inbox Selected",Toast.LENGTH_SHORT).show();
                        ContentFragment fragment = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,fragment);
                        fragmentTransaction.commit();*/
                       // Toast.makeText(getApplicationContext(),"Inbox navigation_item_1",Toast.LENGTH_SHORT).show();
                       // return true;
                    // For rest of the options we just show a toast on click

                         break;
                    case R.id.navigation_item_2:
                        fragmentClass = UserProfilFragment.class;
                       // Toast.makeText(getApplicationContext(),"Stared navigation_item_2",Toast.LENGTH_SHORT).show();
                        break;
                       // return true;
                    case R.id.navigation_item_3:
                        fragmentClass = FindShopFragment.class;

                        //Toast.makeText(getApplicationContext(),"Send navigation_item_3",Toast.LENGTH_SHORT).show();
                      //  return true;
                        break;

                    case R.id.navigation_item_4:
                        fragmentClass = ListNetworkFragment.class;

                        //  return true;
                        break;
                    case R.id.navigation_item_5:
                    fragmentClass = TransactionsHistoryFragment.class;

                    //  return true;
                    break;

                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                       // return true;
                }

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();


                 return true;
            }
        });




        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        //setupDrawerContent(nvDrawer);



        // Find our drawer view
      /* mDrawer = (DrawerLayout) findViewById(R.id.main_content);
        drawerToggle = setupDrawerToggle();*/


        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle);

    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
      //  getSupportActionBar().setHomeAsUpIndicator(new ColorDrawable(getResources().getColor(R.color.white))));

        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Diool" + "</font>")));
    }










    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.insertNewFragment(new TransfertFragment());
        adapter.insertNewFragment(new PaymentFragment());
        adapter.insertNewFragment(new RequestFragment());
        adapter.insertNewFragment(new BalanceFragment());
        viewPager.setAdapter(adapter);
    }

   /* @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        System.out.println("id  + "+id);




        return true;
    }*/

    public void setFragment(Fragment fragment){
        if(fragment!=null){
            FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment);
            ft.commit();
        }
       // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void insertNewFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }


   /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
          switch (item.getItemId()) {

          case R.id.action_settings:
                Intent settingsIntent = new Intent(this, TabSimple.class);
                startActivity(settingsIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
        item.getItemId();
        return super.onOptionsItemSelected(item);
    }*/

    //////////////////// navigation drawer /////////////////////

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                   //  selectDrawerItem(menuItem);
                        System.out.println("pas d execution de fonction");

                        int id = menuItem.getItemId();
                        Fragment fragment = null;

                        Class fragmentClass = null;
                        switch (id) {
                            case R.id.navigation_item_1:
                                //Do some thing here
                                // add navigation drawer item onclick method here
                                fragmentClass = UserProfilFragment.class;

                                break;
                            case R.id.navigation_item_2:
                                //Do some thing here
                                // add navigation drawer item onclick method here
                                break;
                            case R.id.navigation_item_3:
                                //Do some thing here
                                // add navigation drawer item onclick method here
                                break;
                            case R.id.navigation_item_4:
                                //Do some thing here
                                // add navigation drawer item onclick method here
                                break;
                            case R.id.navigation_item_5:
                                //Do some thing here
                                // add navigation drawer item onclick method here
                                break;
                        }


                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_tab_content, fragment).commit();

                        // Highlight the selected item has been done by NavigationView
                        menuItem.setChecked(true);
                        // Set action bar title
                        setTitle(menuItem.getTitle());
                        // Close the navigation drawer
                        mDrawer.closeDrawers();
                        return true;
                    }

                });
        System.out.println("pas d execution de fonction");
    }





    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.navigation_item_1) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    ////////////////////////////////////////assign shop for merchant //////////////////////////////////////////


    public void startAssignShopForMerchant() throws Exception{

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(ServiceSelectionActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();




        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();
        // params.put("owner_id",issuerParents[issuerParents.length - 1]);
        params.put("merchantId", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID", "")));
        params.put("shopId", UserProfilFragment.mShops.getMListShop().get(position).getId());
        params.put("deassignOthers", false);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_ASSIGN_SHOP_FOR_MERCHANT, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        ShopsByOwnerId mShops = new ShopsByOwnerId().createShopsByOwnerId(response);

                        if(String.valueOf(mShops.getCode()).equals("200"))
                        {
                            new SweetAlertDialog(ServiceSelectionActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mShops.getMessage())
                                    .show();


                        }

                        else
                        {
                            new SweetAlertDialog(ServiceSelectionActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mShops.getMessage())
                                    .show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(ServiceSelectionActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
                        .show();
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                try {
                    headers.put("Authorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }


        };

        // Define time out request
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.cat2Timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertFragment.class.getCanonicalName());
    }






    /** Stores the selected item's position */
    int position = 0;
    @Override
    public void onPositiveClick(int position) {
        this.position = position;

        try {
            startAssignShopForMerchant();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNegativeClick(int position) {

    }












}
