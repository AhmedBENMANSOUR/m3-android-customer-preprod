package com.dioolcustomer.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.gson.JsonObject;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.dioolcustomer.R;
import com.dioolcustomer.callbacks.FutureCallback;
import com.dioolcustomer.callbacks.HttpTask;
import com.dioolcustomer.constants.GlobalConstants;

import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupVerificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupVerificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class SignupVerificationFragment extends Fragment implements Validator.ValidationListener {
    public final String TAG = getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View fragmentView;
    private String userType="merchant";

    private Validator validator;
    @NotEmpty
    @Email
    EditText verifEmail;


    private OnFragmentInteractionListener mListener;

    public SignupVerificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignupVerificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupVerificationFragment newInstance(String userType) {
        SignupVerificationFragment fragment = new SignupVerificationFragment();
        Bundle args = new Bundle();
        args.putString("userType",userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().getString("userType")!=null){
                userType=getArguments().getString("userType");
            }
        }
//        Analytics.with(getActivity()).screen("View", TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_signup_verification, container, false);
        validator = new Validator(this);
        validator.setValidationListener(this);
        ((SignupActivity) getActivity()).setVerifyWith("SMS");
        //Test without digits
        //successDigits("+237676349555");

        //clear cache
        //CacheUtils.deleteCache(getActivity());

        // Inflate the layout for this fragment
        DigitsAuthButton digitsButton = (DigitsAuthButton) fragmentView.findViewById(R.id.auth_button);
        AuthCallback authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {

                if (phoneNumber != null) {
                    successDigits(phoneNumber);
                }else if (session.getPhoneNumber()!=null){
                    successDigits(session.getPhoneNumber());
                }
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        };
        digitsButton.setCallback(authCallback);
        digitsButton.setAuthTheme(R.style.CustomDigitsTheme);

        Button sendVerifEmailButton = (Button) fragmentView.findViewById(R.id.buttonSendVerifEMail);
        verifEmail = (EditText) fragmentView.findViewById(R.id.editTextEmail);
        sendVerifEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        return fragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Called when Digits verification is successful
     *
     * @param phoneNumber
     */
    private void successDigits(final String phoneNumber) {
        ((SignupActivity) getActivity()).setVerifyWith("SMS");
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();
        String urlString = GlobalConstants.API_URL + "?action=verifyPhoneExistance&phoneDigits=" + phoneNumber;
        HttpTask httpTask = new HttpTask(getActivity().getAssets(),GlobalConstants.cat1Timeout);
        httpTask.setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(JsonObject result) {
                Log.i(TAG, "result " + result.toString());
                if (result.get("code").getAsString().equals("notexist")) {
                    //Attach signup user fragment
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    SignupFragment signupFragment = SignupFragment.newInstance(phoneNumber, null, null, userType);

                    fragmentTransaction.replace(R.id.fragment_signup_container, signupFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Failed")
                            .setContentText(getResources().getString(R.string.failed_operation))
                            .show();
                }
                dialog.dismiss();
            }
            //@Override
            public void onTimeout(){
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.failed_connection_title))
                        .setContentText(getResources().getString(R.string.failed_connection))
                        .show();
                dialog.dismiss();
            }
        });
        httpTask.execute(urlString);
    }

    @Override
    public void onValidationSucceeded() {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();
        String urlString=GlobalConstants.API_URL + "?action=sendverificationemail&email=" + verifEmail.getText().toString()+"&usertype="+userType;

        HttpTask httpTask = new HttpTask(getActivity().getAssets(),GlobalConstants.cat1Timeout);
        httpTask.setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(JsonObject result) {
                Log.i(TAG, "result " + result.toString());
                if (("true").equals(result.get("success").getAsString())) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Email envoyé")
                            .setContentText("Veuillez utiliser le lien de vérification envoyé sur votre email")
                            .show();
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Email non envoyé")
                            .setContentText("Email non envoyé, peut etre vous etes deja inscrit")
                            .show();
                }
                dialog.dismiss();
            }
            //@Override
            public void onTimeout(){
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.failed_connection_title))
                        .setContentText(getResources().getString(R.string.failed_connection))
                        .show();
                dialog.dismiss();
            }
        });
        httpTask.execute(urlString);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Toast.makeText(getActivity(), "Il faut entrer un email valide",Toast.LENGTH_SHORT).show();

    }


}
