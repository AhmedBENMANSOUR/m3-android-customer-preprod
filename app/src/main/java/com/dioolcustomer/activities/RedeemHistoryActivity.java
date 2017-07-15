package com.dioolcustomer.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
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
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.adapters.RedeemHistoryAdapter;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.Redeem;
import com.dioolcustomer.models.RedeemHistoryTransaction;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RedeemHistoryActivity extends ActionBarActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    int mSize;
    public String PdfFilePath = "";
    public String TAG = getClass().getSimpleName();
    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;
    private SimpleDateFormat dateFormatter, showinDateFormat;
    boolean fromFlag = true;
    int mNB = 0;

    Encryption encryption = new Encryption();


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    RedeemHistoryAdapter mHistoryAdapter;
    TextView mEmptyText, mLoadMoreText;
    EditText mStartDate, mEndDate;
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    TimePickerDialog fromTimePickerDialog, toTimePickerDialog;
    LinearLayout mLayoutDate;
    File myFile;

    String StartValue = "2000-04-23T18:25:43.511Z";
    String EndValue = "";
    Button mRecherchButton;
    Calendar newCalendar;
    private ArrayList<Redeem> mListHistory = new ArrayList<>();
    private ArrayList<Redeem> mListLoadedHistory = new ArrayList<>();
    protected Handler handler;
    private File pdfFile;
    BaseFont bfBold;

    int start = 0;
    int end = 9;

    TextView mTextTotalTransfert, mNomreTransfert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_history);

        mNomreTransfert = (TextView) findViewById(R.id.texView_nb_transfert);
        mTextTotalTransfert = (TextView) findViewById(R.id.texView_total_transfert);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_recuperations);
        mStartDate = (EditText) findViewById(R.id.txt_start_date);
        mEndDate = (EditText) findViewById(R.id.txt_end_date);

        mEmptyText = (TextView) findViewById(R.id.text_empty);
        mLoadMoreText = (TextView) findViewById(R.id.text_load_more);

        mLoadMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchRedeemHistoryProcess();
            }
        });
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        showinDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        newCalendar = Calendar.getInstance();
        EndValue = dateFormatter.format(newCalendar.getTime());


        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mLayoutDate = (LinearLayout) findViewById(R.id.layout_date);
        mRecherchButton = (Button) findViewById(R.id.history_action_button);


        //Prepare Preferences
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

        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);



        LaunchRedeemHistoryProcess();

        mRecherchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchRedeemHistoryProcess();
            }
        });


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\" face=\"normal|bold|italic\">" + "Relevé des récupérations" + "</font>")));
    }





    private void LaunchRedeemHistoryProcess() {
        if (MMMUtils.isConnectedTointernet(RedeemHistoryActivity.this)) {
            if (mLayoutDate.getVisibility() == View.VISIBLE) {
                if (mStartDate.getText().toString().equals(""))
                    mStartDate.setError("Veuillez saisir la date de début des récupérations");
                else if (mEndDate.getText().toString().equals(""))
                    mEndDate.setError("Veuillez saisir la date de fin des récupérations");

                else {
                    int start = 0;
                    int end = 9;
                    mListHistory.clear();
                    getRedeemHistory(start, end);
                }

            } else
                getRedeemHistory(start, end);

        } else
            Toast.makeText(RedeemHistoryActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void getRedeemHistory(final int startI, int EndI) {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(RedeemHistoryActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();


        // Sending parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("startDate", StartValue);
        params.put("endDate", EndValue);
        params.put("status", "0");
        params.put("indexStart", startI + "");
        params.put("indexEnd", EndI + "");


        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_REDEEM_HISTORY, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       // mNB = 0;
                        Log.e(TAG, response.toString());
                        dialog.dismiss();


                        RedeemHistoryTransaction mHistoryResponse = new RedeemHistoryTransaction().createRedeemHistory(response);

                        // Prepare next page
                        start = end + 1;
                        end = end + 11;

                        for (int i = 0; i < mHistoryResponse.getMListHistory().size(); i++)
                            mListHistory.add(mHistoryResponse.getMListHistory().get(i));


                        // Enable/Disable load more

                        if (mHistoryResponse.getMListHistory().size() >= 10)
                            mLoadMoreText.setVisibility(View.VISIBLE);
                        else mLoadMoreText.setVisibility(View.GONE);


                        if (mListHistory.size() > 0) {
                            mNB += (int) mHistoryResponse.getMListHistory().size();

                           // mNomreTransfert.setText(String.valueOf(mNB));
                            mNomreTransfert.setText(String.valueOf(mNB));

                            //mNomreTransfert.setText(mHistoryResponse.getNbRedeems().toString());

                            mTextTotalTransfert.setText(mHistoryResponse.getTotalRedeems().toString());


                            if (!mHistoryResponse.getMLimited().toString().equals("null")) {
                                // Inform User That List is Not completed
                                Crouton.makeText(RedeemHistoryActivity.this, "", Style.ALERT).show();
                            }

                            mEmptyText.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLayoutDate.setVisibility(View.GONE);

                            mHistoryAdapter = new RedeemHistoryAdapter(mListHistory, RedeemHistoryActivity.this);
                            mRecyclerView.setAdapter(mHistoryAdapter);

                        } else {
                            mEmptyText.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(RedeemHistoryActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                    Log.e("Autorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    headers.put("Authorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }


        };


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, RedeemHistoryActivity.class.getCanonicalName());
    }


    @Override
    public void onClick(View v) {
        if (v == mStartDate) {
            fromDatePickerDialog = DatePickerDialog.newInstance(RedeemHistoryActivity.this, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            fromDatePickerDialog.show(getFragmentManager(), "datePicker");
        } else if (v == mEndDate) {
            toDatePickerDialog = DatePickerDialog.newInstance(RedeemHistoryActivity.this, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            toDatePickerDialog.show(getFragmentManager(), "datePicker");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.history_merchant, menu);

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
                if (MMMUtils.launchRequestPermissionProcess(RedeemHistoryActivity.this)) {

                } else {
                    if (mListHistory.size() > 0) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RedeemHistoryActivity.this);
                        alertDialogBuilder.setMessage("Voulez-vous sauvegarder ce relevé de récupérations au format pdf?")
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
                        Toast.makeText(this, "Vous avez pas des récupérations à sauvegarder!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

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
                    "DIOOL: Historique des récupérations");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "La liste des récupérations faite par " +  encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")));

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
            fromTimePickerDialog = TimePickerDialog.newInstance(RedeemHistoryActivity.this, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
            fromTimePickerDialog.show(getFragmentManager(), "timePicker");


        } else if (dialog == toDatePickerDialog) {
            fromFlag = false;
            newCalendar.set(year, monthOfYear, dayOfMonth);
            String monthSelected = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
            EndValue = dateFormatter.format(newCalendar.getTime());
            mEndDate.setText(showinDateFormat.format(newCalendar.getTime()));

            // Launch Alert Date End Selection
            fromTimePickerDialog = TimePickerDialog.newInstance(RedeemHistoryActivity.this, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
            fromTimePickerDialog.show(getFragmentManager(), "timePicker");

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


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
    }


    private void createPdf() throws Exception {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'à' HH:mm:ss "); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // give a timezone reference for formating (see comment at the bottom

        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Echec dans lors du sauvegarde de la fiche récupération !", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Echec dans lors du sauvegarde de la fiche récupération !", Toast.LENGTH_SHORT).show();
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
        float[] columnWidths = {0.5f, 0.5f};
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

        table.setHeaderRows(1);

        Collections.sort(mListHistory, new DateComparator());

        // Total lignes
        if (mListHistory.size() >= 10)
            mSize = 10;
        else
            mSize = mListHistory.size();


        for (int i = 0; i < mSize; i++) {


            long unixSeconds = Long.parseLong(mListHistory.get(i).getMDateRedeem().toString());
            Date dateRedeem = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
            String formattedDate = sdf.format(dateRedeem);

            table.addCell(formattedDate);
            table.addCell(mListHistory.get(i).getAmount().toString() + " XAF");


        }


        //absolute location to print the PDF table from
        PdfContentByte mContentByte = docWriter.getDirectContent();
        table.writeSelectedRows(0, -1, document.leftMargin(), 650, mContentByte);

        //creating a sample invoice with some customer data
        createHeadings(mContentByte, 400, 780, "Utilisateur: " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")));
        createHeadings(mContentByte, 400, 765, "Service Client : helpdesk@mymoneymobile.com");

        // Adding MMM Logo
        try {
            InputStream inputStream = getAssets().open("logowithtext.png");
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
        Toast.makeText(this, "La fiche historique des récupérations est sauvegardée avec succés!", Toast.LENGTH_SHORT).show();

    }


    private void createHeadings(PdfContentByte cb, float x, float y, String text) {

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();

    }


    public class DateComparator implements Comparator<Redeem> {
        @Override
        public int compare(Redeem o1, Redeem o2) {
            return o2.getMDateRedeem().toString().compareTo(o1.getMDateRedeem().toString());
        }
    }

}
