package com.dioolcustomer.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.activities.QuoteRequestActivity;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.constants.GlobalStatic;
import com.dioolcustomer.models.SendRedeemResponse;
import com.dioolcustomer.models.quoterequest.QuoteRequestResponse;
import com.dioolcustomer.security.Encryption;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by ASUS on 16/03/2017.
 */

public class MerchantQuoteRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,AdapterView.OnItemLongClickListener{



    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    Token mUserToken;
    Gson gson;
    JsonObjectRequest jsonObjReq;
    Encryption encryption = new Encryption();


    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public ArrayList<QuoteRequestResponse> mQouteRequestList;
    SweetAlertDialog mSweetTake;
    SweetAlertDialog mSweetPass;
    private Context context;
    GlobalStatic globalStatic = new GlobalStatic();

    @Override
    public void onClick(View v) {
    /*    if(v.getId() == mBtnTake.getId()){
           // startSelectQuoteRequest(mQouteRequestList.getAdapterPosition)
        }*/
    getItemCount();


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


        mSweetTake = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("DIOOL")
                .setContentText("")
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        startSelectQuoteRequest(mQouteRequestList.get(position).getId().toString());

                    }
                })
        ;

        mSweetTake.show();
        return false;
    }

    public static class MerchantQuoteRequestViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView textViewApplicant;
        public TextView textViewAmount;
        public CheckBox checkBoxSelect;
        public Button mBtnPass ;
        public Button mBtnTake ;
        public ImageView imageViewApplicant;
        public ImageView imageViewAmount;
        public TextView textViewAccept;
        public TextView textViewPass;
        public ImageView imageViewAccept;
        public ImageView imageViewPass;


        private SharedPreferences shared;
        SharedPreferences.Editor editor;
        Token mUserToken;
        Gson gson;





        public MerchantQuoteRequestViewHolder(View itemView) {
            super(itemView);
           textViewApplicant = (TextView) itemView.findViewById(R.id.texView_name_applicant);
           textViewAmount = (TextView) itemView.findViewById(R.id.textView_amount_quote_request);
         //   checkBoxSelect = (CheckBox) itemView.findViewById(R.id.checkBox_select_quote_request);
         //   mBtnTake = (Button) itemView.findViewById(R.id.btnTakeQR);
         //   mBtnPass = (Button) itemView.findViewById(R.id.btnPassQR);
            textViewApplicant.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

            SweetAlertDialog mSweetTake;




        }
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView textViewApplicant;
        public TextView texView_amount_quote_request;
      //  public Button mBtnTake;
       // public Button mBtnPass;
        public CheckBox checkBoxSelect;
        public ImageView imageViewApplicant;
        public ImageView imageViewAmount;
        SweetAlertDialog mSweetPass;
        SweetAlertDialog mSweetTake;



        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            textViewApplicant = (TextView) itemView.findViewById(R.id.texView_name_applicant);
            texView_amount_quote_request = (TextView) itemView.findViewById(R.id.textView_amount_quote_request);
          //  checkBoxSelect = (CheckBox) itemView.findViewById(R.id.checkBox_select_quote_request);
           // mBtnTake = (Button) itemView.findViewById(R.id.btnTakeQR);
           // mBtnPass = (Button) itemView.findViewById(R.id.btnPassQR);
            SweetAlertDialog mSweetTake;



            // mBtnTake = (Button) itemView.findViewById(R.id.button_take_quote_request);
            //imageViewApplicant = (ImageView) itemView.findViewById(R.id.imageViewApplicantIcon);
           // imageViewAmount = (ImageView) itemView.findViewById(R.id.imageView_amount_quote_request);

           /* checkBoxSelect.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    System.out.println("le checkbox est selectionné");
                }

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // selected item
                    String selectedItem = ((TextView) view).getText().toString();
                    System.out.println("le checkbox est selectionné");

                }
                });*/






        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.merchant_quote_request_item, parent, false);

            vh = new MerchantQuoteRequestViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_loading, parent, false);

            vh = new ProgressViewHolder(v);
          //  vh = null;
        }
        vh.setIsRecyclable(false);





        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof MerchantQuoteRequestViewHolder) {

            boolean bo = false;
            int  i = 0;
            int j = 0;

          /*  while(i < globalStatic.getIdQRTokenList().size() && !bo){
                while( j < mQouteRequestList.size() && !bo){
                    if(globalStatic.getIdQRTokenList().get(i) == mQouteRequestList.get(j).getId()){
                        mQouteRequestList.remove(j);
                        bo = true;
                    }
                    j++;
                }
                i++;
            }*/


            final String name  = String.valueOf(mQouteRequestList.get(position).getFirstName())+" "+String.valueOf(mQouteRequestList.get(position).getLastName());
            final String operation = String.valueOf(mQouteRequestList.get(position).getAmount())+" | "+String.valueOf(mQouteRequestList.get(position).getServiceType())+" XAF";
            ((MerchantQuoteRequestViewHolder) holder).textViewApplicant.setText(String.valueOf(mQouteRequestList.get(position).getFirstName())+" "+String.valueOf(mQouteRequestList.get(position).getLastName()));
            ((MerchantQuoteRequestViewHolder) holder).textViewAmount.setText(String.valueOf(mQouteRequestList.get(position).getServiceType())+" | "+String.valueOf(mQouteRequestList.get(position).getAmount())+" XAF");
            if(bo){
               // ((MerchantQuoteRequestViewHolder) holder).textViewAmount.setBackgroundColor(Color.GRAY);
               // ((MerchantQuoteRequestViewHolder) holder).textViewApplicant.setBackgroundColor(Color.GRAY);
            }
            final boolean b = bo;


            ((MerchantQuoteRequestViewHolder) holder).textViewAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b){
                        mSweetTake = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("DIOOL")
                                .setContentText("tu as déjà choisi cette demande")
                                .setConfirmText("Ok")

                        ;

                        mSweetTake.show();
                    }else{
                        mSweetTake = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("DIOOL")
                                .setContentText(name + "\n"+ operation)
                                .setCancelText("Pass")
                                .setConfirmText("Take")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        startSelectQuoteRequest(mQouteRequestList.get(position).getId().toString());
                                        globalStatic.getIdQRTokenList().add(mQouteRequestList.get(position).getId());
                                        mQouteRequestList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, getItemCount());

                                        //((MerchantQuoteRequestViewHolder) holder).textViewAmount.setBackgroundColor(Color.GRAY);
                                        // ((MerchantQuoteRequestViewHolder) holder).textViewApplicant.setBackgroundColor(Color.GRAY);

                                        //    bo = true;


                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        passQuoteRequest(mQouteRequestList.get(position).getId().toString(), position);
                                        globalStatic.getIdQRPassList().add(mQouteRequestList.get(position).getId());
                                        mQouteRequestList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, getItemCount());


                                    }
                                })
                        ;

                        mSweetTake.show();
                    }
                }
            });

            ((MerchantQuoteRequestViewHolder) holder).textViewApplicant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b){
                        mSweetTake = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("DIOOL")
                                .setContentText("tu as déjà choisi cette demande")
                                .setConfirmText("Ok")

                        ;

                        mSweetTake.show();
                    }else{
                        mSweetTake = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("DIOOL")
                                .setContentText(name + "\n"+ operation)
                                .setCancelText("Pass")
                                .setConfirmText("Take")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        startSelectQuoteRequest(mQouteRequestList.get(position).getId().toString());
                                        globalStatic.getIdQRTokenList().add(mQouteRequestList.get(position).getId());
                                        mQouteRequestList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, getItemCount());

                                        //((MerchantQuoteRequestViewHolder) holder).textViewAmount.setBackgroundColor(Color.GRAY);
                                        // ((MerchantQuoteRequestViewHolder) holder).textViewApplicant.setBackgroundColor(Color.GRAY);

                                        //    bo = true;


                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        passQuoteRequest(mQouteRequestList.get(position).getId().toString(), position);
                                        globalStatic.getIdQRPassList().add(mQouteRequestList.get(position).getId());
                                        mQouteRequestList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, getItemCount());


                                    }
                                })
                        ;

                        mSweetTake.show();
                    }
                }
            });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(b){
                            mSweetTake = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText("tu as déjà choisi cette demande")
                                    .setConfirmText("Ok")

                            ;

                            mSweetTake.show();
                        }else{
                            mSweetTake = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(name + "\n"+ operation)
                                    .setCancelText("Pass")
                                    .setConfirmText("Take")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                            startSelectQuoteRequest(mQouteRequestList.get(position).getId().toString());
                                            globalStatic.getIdQRTokenList().add(mQouteRequestList.get(position).getId());
                                            mQouteRequestList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, getItemCount());

                                            //((MerchantQuoteRequestViewHolder) holder).textViewAmount.setBackgroundColor(Color.GRAY);
                                           // ((MerchantQuoteRequestViewHolder) holder).textViewApplicant.setBackgroundColor(Color.GRAY);

                                        //    bo = true;


                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            passQuoteRequest(mQouteRequestList.get(position).getId().toString(), position);
                                            globalStatic.getIdQRPassList().add(mQouteRequestList.get(position).getId());
                                            mQouteRequestList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, getItemCount());


                                        }
                                    })
                            ;

                            mSweetTake.show();
                        }


                    }
                });


        }






    }

    @Override
    public int getItemCount() {
        System.out.println("mQouteRequestList.size() : "+mQouteRequestList.size());
        return mQouteRequestList.size();
    }




    public MerchantQuoteRequestAdapter(ArrayList<QuoteRequestResponse> mQouteRequestList, Context context, Token mUserToken, SharedPreferences shared){
        this.mQouteRequestList = mQouteRequestList;
        this.context = context;
        this.mUserToken = mUserToken;
        this.shared = shared;


    }

    @Override
    public int getItemViewType(int position) {
        return mQouteRequestList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }




    /////////////////////////////////select quote request /////////////////////////////

    public void startSelectQuoteRequest(String idRequest) {




        // Personalize View when loading WS
       /* final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();*/

        // Sending parameters
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("quoteRequestId",idRequest);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_SELECT_QUOTE_REQUEST, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG, response.toString());
                       // dialog.dismiss();
                         SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                          if (String.valueOf(mRedeem.getCode()).equals("0")) {
                              mSweetTake.dismissWithAnimation();

                              System.out.println("le quote request est selectioné");
                              SweetAlertDialog mSweet =
                                      new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                              .setTitleText("DIOOL")
                                              .setContentText("Success");




                              mSweet.show();
                          }else{

                              SweetAlertDialog mSweet =
                                      new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                              .setTitleText("DIOOL")
                                              .setContentText(mRedeem.getMessage());

                              mSweet.show();

                          }



                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               /* Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();*/

                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
                        .show();
               System.out.println("erreur lors de la selection");
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
                    System.out.println(mUserToken.getType() + " " +encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                    headers.put("Authorization", mUserToken.getType() + " " +encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, QuoteRequestResponse.class.getCanonicalName());
    }



    public void updateData(ArrayList<QuoteRequestResponse> dataList){
        mQouteRequestList.clear();
        this.mQouteRequestList = dataList;
        notifyDataSetChanged();

         new MerchantQuoteRequestAdapter( mQouteRequestList,  context,  mUserToken,  shared);
    }


    /////////////////////////////pass quote request ////////////////////////////////////////



    public void passQuoteRequest(String idRequest, final int position) {




        // Personalize View when loading WS
       /* final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();*/

        // Sending parameters
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("quoteRequestId",idRequest);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_PASS_QUOTE_REQUEST, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG, response.toString());
                        // dialog.dismiss();
                          SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                          if (String.valueOf(mRedeem.getCode()).equals("0")) {
                              mSweetTake.dismissWithAnimation();
                              System.out.println("le quote request est supprimé");
                              SweetAlertDialog mSweet =
                                      new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                              .setTitleText("DIOOL")
                                              .setContentText("Quote Request Deleted");




                              mSweet.show();

                              // mQouteRequestList.remove(position);
                              QuoteRequestActivity mQuoteRequestActivity = new QuoteRequestActivity();

                              new MerchantQuoteRequestAdapter(mQouteRequestList, mQuoteRequestActivity.getContext(),mUserToken,shared);

                          }
                          else{
                              new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                      .setTitleText("DIOOL")
                                      .setContentText(mRedeem.getMessage())
                                      .show();
                          }
                       // mSweetPass.dismissWithAnimation();





                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               /* Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();*/

                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
                        .show();
                System.out.println("erreur lors de la selection");
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
                    System.out.println(mUserToken.getType() + " " +encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                    headers.put("Authorization", mUserToken.getType() + " " +encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, QuoteRequestResponse.class.getCanonicalName());
    }








}
