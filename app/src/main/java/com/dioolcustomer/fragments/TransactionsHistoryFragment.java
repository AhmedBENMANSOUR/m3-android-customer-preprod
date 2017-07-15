package com.dioolcustomer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.activities.TransactionsHistoryActivity;
import com.dioolcustomer.adapters.TransactionsHistoryAdapter;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.History;
import com.dioolcustomer.models.HistoryTransaction;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.telpo.tps550.api.printer.ThermalPrinter;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class TransactionsHistoryFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    Encryption encryption  = new Encryption();

    int mSize;
    public String PdfFilePath = "";
    public String TAG = getClass().getSimpleName();
    int mNB = 0;

    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    //   String idToken;
    Token mUserToken;
    private SimpleDateFormat dateFormatter, showinDateFormat;
    boolean fromFlag = true;


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    TransactionsHistoryAdapter mHistoryAdapter;
    TextView mEmptyText, mLoadMoreText;
    EditText mStartDate, mEndDate;
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    TimePickerDialog fromTimePickerDialog, toTimePickerDialog;
    LinearLayout mLayoutDate;
    File myFile;

    String StartValue = "";// "2000-04-23T18:25:43.511Z";

    /*Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_YEAR, -5);*/
    Long tsLong = System.currentTimeMillis();
    String EndValue = "";
    Button mRecherchButton;
    Calendar newCalendar;
    private ArrayList<History> mListHistory = new ArrayList<>();
    private ArrayList<History> mListLoadedHistory = new ArrayList<>();
    protected Handler handler;
    private File pdfFile;
    BaseFont bfBold;

    int start = 0;
    int end = 15;

    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private final boolean isClose = false;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int NOPAPER = 3;
    private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.bmp";

    TextView mTextTotalTransfert, mNomreTransfert;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_transactions_history, container, false);

        mNomreTransfert = (TextView) mView.findViewById(R.id.texView_nb_transfert);
        mTextTotalTransfert = (TextView) mView.findViewById(R.id.texView_total_transfert);


        IntentFilter pIntentFilter = new IntentFilter();
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(printReceive, pIntentFilter);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view_transactions);
        mStartDate = (EditText) mView.findViewById(R.id.txt_start_date);
        mEndDate = (EditText) mView.findViewById(R.id.txt_end_date);

        mEmptyText = (TextView) mView.findViewById(R.id.text_empty);
        mLoadMoreText = (TextView) mView.findViewById(R.id.text_load_more);

        mLoadMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    LaunchHistoryProcess();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        handler = new Handler();

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        showinDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        newCalendar = Calendar.getInstance();
        EndValue = dateFormatter.format(newCalendar.getTime());


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        StartValue = dateFormatter.format(cal.getTime());

        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mLayoutDate = (LinearLayout) mView.findViewById(R.id.layout_date);
        mRecherchButton = (Button) mView.findViewById(R.id.history_action_button);


        //Prepare Preferences
        shared = getActivity().getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();

        gson = new Gson();

        String jsonToken = shared.getString("USER_TOKEN", "");

        mUserToken = gson.fromJson(jsonToken, Token.class);
        //  idToken = shared.getString("USER_ID_TOKEN","");

        String jsonProfile = null;
        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);




        try {
            LaunchHistoryProcess();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        mRecherchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    LaunchHistoryProcess();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //Timeout manegement
      /*  jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();


        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Transactions" + "</font>")));

        return mView;
    }


    private void LaunchHistoryProcess() throws Exception {
        if (MMMUtils.isConnectedTointernet(getActivity())) {
            if (mLayoutDate.getVisibility() == View.VISIBLE) {
                if (mStartDate.getText().toString().equals(""))
                    mStartDate.setError("Veuillez saisir la date de début des transactions");
                else if (mEndDate.getText().toString().equals(""))
                    mEndDate.setError("Veuillez saisir la date de fin des transactions");

                else {
                    /*Long UNE_HEURE = 60 * 60 * 1000L;
                    (dateFormatter.parse(mEndDate.getText().toString()) - dateFormatter.parse(mStartDate.getText().toString() + UNE_HEURE) / (UNE_HEURE * 24);*/

                    DateTimeFormatter dbDateFormat = DateTimeFormat
                            .forPattern(("yyyy-MM-dd HH:mm:ss"));

                    DateTime dateFin =  dbDateFormat.parseDateTime(mEndDate.getText().toString());
                    DateTime dateDeb =  dbDateFormat.parseDateTime(mStartDate.getText().toString());
                    if(dateFin.isAfter(dateDeb)){
                        int diffDate = Days.daysBetween(dateDeb,dateFin).getDays();
                        if(Math.abs(diffDate) > 3){
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText("la pèriode ne doit pas dépasser 3 jours")
                                    .show();
                            Timestamp time = new Timestamp(dateFin.toDate().getTime());
                            System.out.println("time stamp : " + time.getTime());
                            Long ts = time.getTime() - (86400000*3);
                            System.out.println("new time stamp : "+ new Date(ts));
                            String dateStr  = new Date(ts).toString();
                            DateFormat readFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy");

                            DateFormat writeFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
                            Date date = null;
                            try {
                                date = readFormat.parse( dateStr );
                            } catch ( ParseException e ) {
                                e.printStackTrace();
                            }

                            String formattedDate = "";
                            if( date != null ) {
                                formattedDate = writeFormat.format( date );
                            }

                            System.out.println(formattedDate);
                            //Date date =  dateFormatter.parse(new Date(ts).toString());
                            mStartDate.setText(formattedDate);
                            System.out.println();
                            //time.getTime()
                            // dateDeb.toDate().setTime(dateDeb));
                          /*  Calendar cal = (Date)dateDeb.getInstance();
                            int i  = dateDeb.getDayOfYear();
                          //  cal.add(i, -1);
                            System.out.println("cal.getTime(); "+cal.getTime());
                            StartValue = dateFormatter.format(cal.getTime());*/
                        }else{
                            start = 0;
                            end = 15;
                            mListHistory.clear();
                            getTransactionHistory(start, end);
                        }
                    }else{
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("DIOOL")
                                .setContentText("vérifier les dates sélectionnées SVP")
                                .show();
                    }




                }

            } else
                getTransactionHistory(start, end);

        } else
            Toast.makeText(getActivity(), "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(printReceive);
    }


    private void getTransactionHistory(final int startI, int EndI) throws Exception {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();


        //create table issuerParents
        List<String> parentsIdList = new ArrayList<>();
        Object [] issuerParents;
        JSONObject jsnobject = new JSONObject("{parent_ids:"+encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""))+"}");

        JSONArray jsonArray = jsnobject.getJSONArray("parent_ids");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            if(explrObject.has("parent_id")){
                parentsIdList.add(explrObject.getString("parent_id"));
            }
        }
        issuerParents  = parentsIdList.toArray();


        // create table issuerChildren


        String children = "";

        try{
            children  = encryption.decrypt(mUserToken.getIdToken(),shared.getString("CHILDREN", ""));
            System.out.println("children : "+children);
            if(!children.equals("[]") && children.equals("[]")){
                children  = children.substring(1,children.length()-2);
                System.out.println("children : "+children);
            }
        }catch (Exception e){

        }
        String [] issuerChildren = children.split(",");




        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate", StartValue);
        params.put("endDate", EndValue);
        params.put("status", "0");
        params.put("indexStart", startI + "");
        params.put("indexEnd", EndI + "");
        params.put("issuerParents",issuerParents);
        params.put("issuerChildren",issuerChildren);

        //params.put("operations","[AIRTIME, CASHIN, CASHOUT, INTERNAL_TRANSFER]");




        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_HISTORY, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mNB = 0;
                        System.out.println("response.toString() : " +response.toString());
                        dialog.dismiss();
                        //System.out.println("service relever des transacction");
                        HistoryTransaction mHistoryResponse = new HistoryTransaction().createHistory(response);


                        Log.e("size", mHistoryResponse.getMListHistory().size() + "");
                        Log.e("la liste est : ", String.valueOf(mHistoryResponse.getMListHistory()));
                        // Prepare next page
                        start = end + 1;
                        end = end + 15;

                        for (int i = 0; i < mHistoryResponse.getMListHistory().size(); i++)
                            mListHistory.add(mHistoryResponse.getMListHistory().get(i));


                        // Enable/Disable load more
                        if (mHistoryResponse.getMListHistory().size() >= 50)
                            mLoadMoreText.setVisibility(View.VISIBLE);
                        else mLoadMoreText.setVisibility(View.GONE);


                        if (mListHistory.size() > 0) {
                            mNB += (int) mHistoryResponse.getMNombre();

                            mNomreTransfert.setText(String.valueOf(mNB));
                            mTextTotalTransfert.setText(mHistoryResponse.getMTotal().toString());

                            if (!mHistoryResponse.getMLimited().toString().equals("null")) {
                                // Inform User That List is Not completed
                                Crouton.makeText(getActivity(), "La liste affichée est tronquée, veuillez charger plus des transactions!", Style.INFO).show();
                            }

                            mEmptyText.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLayoutDate.setVisibility(View.GONE);

                            try {
                                mHistoryAdapter = new TransactionsHistoryAdapter(mListHistory, getActivity());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mRecyclerView.setAdapter(mHistoryAdapter);

                        } else {
                            mEmptyText.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.toString());
                dialog.dismiss();

                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("Veuillez réessayer de nouveau!")
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
                    // Log.e("Autorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    headers.put("Authorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                    //  headers.put("Authorization", mUserToken.getType() + " " + idToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }




        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransactionsHistoryActivity.class.getCanonicalName());
    }




    @Override
    public void onClick(View v) {
        if (v == mStartDate) {
            fromDatePickerDialog = DatePickerDialog.newInstance(TransactionsHistoryFragment.this, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            fromDatePickerDialog.show(getActivity().getFragmentManager(), "datePicker");
        } else if (v == mEndDate) {
            toDatePickerDialog = DatePickerDialog.newInstance(TransactionsHistoryFragment.this, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            toDatePickerDialog.show(getActivity().getFragmentManager(), "datePicker");
        }
    }


    //@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getActivity().getMenuInflater().inflate(R.menu.history_merchant, menu);
           /* if (!Build.MODEL.equals("TPS390")) {
                menu.findItem(R.id.print).setVisible(false);
            }*/


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle
        switch (item.getItemId()) {
            case R.id.select_date: {
                if (mLayoutDate.getVisibility() == View.VISIBLE)
                    mLayoutDate.setVisibility(View.GONE);
                else
                    mLayoutDate.setVisibility(View.VISIBLE);
                return true;
            }

            case R.id.save: {
                if (MMMUtils.launchRequestPermissionProcess(getActivity())) {

                } else {
                    if (mListHistory.size() > 0) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage("Voulez-vous sauvegarder ce relevé de transactions au format pdf?")
                                .setTitle("DIOOL")
                                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            createPdf();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (DocumentException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

                    } else
                        Toast.makeText(getContext(), "Vous avez pas des transactions à sauvegarder!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

          /*  case R.id.print: {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransactionsHistoryActivity.this);
                alertDialogBuilder.setMessage("Voulez-vous imprimer votre fiche historique?")
                        .setTitle("DIOOL")
                        .setPositiveButton("Imprimer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new launchPrinter().start();
                                //startActivity(new Intent(TransactionsHistoryActivity.this, PrinterActivity.class));
                            }
                        }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            }*/

            case R.id.send_file: {
                try {
                    createPdf();
                    launchSharing(PdfFilePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            default:
                return true;
        }
    }

    private void launchSharing(String myFilePath) throws Exception {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(myFilePath);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + myFilePath));

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "DIOOL: Historique des transactions");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "La liste des transactions faite par " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")));

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        if (dialog == fromDatePickerDialog) {
            fromFlag = true;
            newCalendar.set(year, monthOfYear, dayOfMonth);
            String monthSelected = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
            StartValue = dateFormatter.format(newCalendar.getTime());
            mStartDate.setText(showinDateFormat.format(newCalendar.getTime()));

            // Launch Alert Date Start Selection
            fromTimePickerDialog = TimePickerDialog.newInstance(TransactionsHistoryFragment.this, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
            fromTimePickerDialog.show(getActivity().getFragmentManager(), "timePicker");


        } else if (dialog == toDatePickerDialog) {
            fromFlag = false;
            newCalendar.set(year, monthOfYear, dayOfMonth);
            String monthSelected = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
            EndValue = dateFormatter.format(newCalendar.getTime());
            mEndDate.setText(showinDateFormat.format(newCalendar.getTime()));

            // Launch Alert Date End Selection
            fromTimePickerDialog = TimePickerDialog.newInstance(TransactionsHistoryFragment.this, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
            fromTimePickerDialog.show(getActivity().getFragmentManager(), "timePicker");

        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        // Launch Alert Time Start Selection
        if (fromFlag) {
            newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            newCalendar.set(Calendar.MINUTE, minute);
            mStartDate.setText(showinDateFormat.format(newCalendar.getTime()));
            StartValue = dateFormatter.format(newCalendar.getTime());

        }
        // Launch Alert Time End Selection
        else {
            newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            newCalendar.set(Calendar.MINUTE, minute);
            mEndDate.setText(showinDateFormat.format(newCalendar.getTime()));
            EndValue = dateFormatter.format(newCalendar.getTime());

        }
    }


    private void createPdf() throws Exception {

        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();
        }

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MMM");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.e(TAG, "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        myFile = new File(pdfFolder + timeStamp + ".pdf");
        PdfFilePath = myFile.getAbsolutePath();

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter docWriter = PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content
        //list all the products sold to the customer
        float[] columnWidths = {0.5f, 0.5f, 0.5f, 0.5f, 0.5f};
        //create PDF table with the given widths
        PdfPTable table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setTotalWidth(500f);


        PdfPCell cell = new PdfPCell(new Phrase("Date"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Numéro"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Opérateur"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Montant"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Opération"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        table.setHeaderRows(1);

        Collections.sort(mListHistory, new TransactionsHistoryFragment.DateComparator());
        // Total lignes
        if (mListHistory.size() >= 10)
            mSize = 10;
        else
            mSize = mListHistory.size();


        for (int i = 0; i < mSize; i++) {

            // Init Date
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(mListHistory.get(i).getDate());
            String timeString = "";
            String HourString = "";
            String monthString = "";
            String dateString = "";
            String dayString = "";

            if (Integer.parseInt(String.valueOf(dateTime.getHourOfDay())) < 10)
                HourString = "0" + dateTime.getHourOfDay();
            else
                HourString = dateTime.getHourOfDay() + "";


            //Hours and Minutes
            if (Integer.parseInt(String.valueOf(dateTime.getMinuteOfHour())) < 10)
                timeString = HourString + ":0" + dateTime.getMinuteOfHour();
            else
                timeString = HourString + ":" + dateTime.getMinuteOfHour();

            //Days
            if (Integer.parseInt(String.valueOf(dateTime.getDayOfMonth())) < 10)
                dayString = "0" + dateTime.getDayOfMonth();
            else
                dayString = dateTime.getDayOfMonth() + "";

            //Months
            if (Integer.parseInt(String.valueOf(dateTime.getMonthOfYear())) < 10)
                monthString = "0" + dateTime.getMonthOfYear();
            else
                monthString = dateTime.getMonthOfYear() + "";


            dateString = dayString + "/" + monthString;
            table.addCell(dateString + "/" + dateTime.getYear() + " à " + timeString);

            // Init Numéro Phone
            String nProviderId = mListHistory.get(i).getRecipientProviderAccountId();
            String nSenderId = mListHistory.get(i).getSenderProviderAccountId();

            if ((!nProviderId.equals("null")) && !(nProviderId.equals(""))) {
                if (nProviderId.length() > 3)
                    table.addCell(nProviderId.substring(3, nProviderId.length()));
                else
                    table.addCell(nProviderId);
            } else if ((!nSenderId.equals("null")) && !(nSenderId.equals(""))) {
                if (nSenderId.length() > 3)
                    table.addCell(nSenderId.substring(3, nSenderId.length()));
                else
                    table.addCell(nSenderId);

            } else if ((!mListHistory.get(i).getSenderUserIdentifier().equals("null")) && !(mListHistory.get(i).getSenderUserIdentifier().equals(""))) {
                table.addCell(mListHistory.get(i).getSenderUserIdentifier());
            } else if ((!mListHistory.get(i).getRecipientUserIdentifier().equals("null")) && !(mListHistory.get(i).getRecipientUserIdentifier().equals(""))) {
                table.addCell(mListHistory.get(i).getRecipientUserIdentifier());
            }

            //init Opérateur
            if ((mListHistory.get(i).getSenderProviderIdentifier().equals(GlobalConstants.OCM_VALUE)) || (mListHistory.get(i).getRecipientProviderIdentifier().equals(GlobalConstants.OCM_VALUE)))
                table.addCell("ORANGE");
            else if ((mListHistory.get(i).getSenderProviderIdentifier().equals(GlobalConstants.MTNC_VALUES)) || (mListHistory.get(i).getRecipientProviderIdentifier().equals(GlobalConstants.MTNC_VALUES)))
                table.addCell("MTN");

            else if ((mListHistory.get(i).getSenderProviderIdentifier().equals(GlobalConstants.NXTL_VALUE)) || (mListHistory.get(i).getRecipientProviderIdentifier().equals(GlobalConstants.NXTL_VALUE)))
                table.addCell("NXTL");
            else table.addCell("---");


            // Init Amount
            table.addCell(String.valueOf(Html.fromHtml("<b>" + mListHistory.get(i).getAmount() + "</b> ")));

            // Init Type d'Opération
            if (mListHistory.get(i).getRecipientServiceType().toString().equals("AIRTIME"))
                table.addCell("AIRTIME");
            else if ((mListHistory.get(i).getSenderUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")))) || (mListHistory.get(i).getSenderUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")))))
                table.addCell("CASHIN");
            else if ((mListHistory.get(i).getRecipientUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")))) || (mListHistory.get(i).getRecipientUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")))))
                table.addCell("CASHOUT");
            else {
                table.addCell("CASHIN");
            }


        }


        //absolute location to print the PDF table from
        PdfContentByte mContentByte = docWriter.getDirectContent();
        table.writeSelectedRows(0, -1, document.leftMargin(), 650, mContentByte);

        //creating a sample invoice with some customer data
        createHeadings(mContentByte, 400, 780, "Utilisateur: " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")));
        createHeadings(mContentByte, 400, 765, "Service Client : helpdesk@mymoneymobile.com");

        // Adding MMM Logo
        try {
            InputStream inputStream = getActivity().getAssets().open("logowithtext.png");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = null;
            companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAbsolutePosition(25, 700);
            companyLogo.scalePercent(25);
            document.add(companyLogo);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Step 5: Close the document
        document.close();
        Toast.makeText(getContext(), "La fiche historique est sauvegardée avec succés!", Toast.LENGTH_SHORT).show();

    }


    private void createHeadings(PdfContentByte cb, float x, float y, String text) {

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("TransactionsHistory Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class DateComparator implements Comparator<History> {
        @Override
        public int compare(History o1, History o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }


    private class launchPrinter extends Thread {

        @Override
        public void run() {
            super.run();
            setName("Impression de la facture");
            try {
                ThermalPrinter.start(getActivity());
                ThermalPrinter.reset();
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
                File file = new File(picturePath);
                if (file.exists()) {
                    ThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath));
                    ThermalPrinter.walkPaper(100);
                } else {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getString(R.string.not_find_picture), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else if ((Result.equals("com.telpo.tps550.api.printer.DeviceNotOpenException")) || (Result.equals("com.telpo.tps550.api.printer.DeviceNotFoundException"))) {
                    Toast.makeText(getActivity(), "Veuillez branchez votre imprimante!", Toast.LENGTH_SHORT).show();
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }

                ThermalPrinter.stop(getActivity());
                Log.v(TAG, "The PrintPicture Progress End !!!");
                if (isClose) {
                    getActivity().finish();
                }
            }
        }
    }


    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                if (Build.MODEL.equals("TPS390")) {
                    if (level * 5 <= scale) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                        if (level * 5 <= scale) {
                            LowBattery = true;
                        } else {
                            LowBattery = false;
                        }
                    } else {
                        LowBattery = false;
                    }
                }
            }
        }
    };

   /* @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
        Log.d(TAG, "User interaction to " + this.toString());
    }*/


}
