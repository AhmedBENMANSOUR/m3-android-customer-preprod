package com.dioolcustomer.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jumio.mobile.sdk.MissingPermissionException;
import com.jumio.mobile.sdk.MobileSDK;
import com.jumio.mobile.sdk.PlatformNotSupportedException;
import com.jumio.mobile.sdk.ResourceNotFoundException;
import com.jumio.mobile.sdk.enums.JumioDataCenter;
import com.jumio.netswipe.sdk.NetswipeCardInformation;
import com.jumio.netswipe.sdk.NetswipeSDK;
import com.jumio.netverify.sdk.NetverifyDocumentData;
import com.jumio.netverify.sdk.NetverifyMrzData;
import com.jumio.netverify.sdk.NetverifySDK;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.dioolcustomer.R;
import com.dioolcustomer.callbacks.FutureCallback;
import com.dioolcustomer.callbacks.HttpTask;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.utils.LinkedHashMapAdapter;
import com.dioolcustomer.utils.MMMUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment implements Validator.ValidationListener {
    public final String TAG = getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String EMAIL = "email";
    private static final String VERIF_CODE = "verifCode";
    private static final String USER_TYPE = "userType";

    // TODO: Rename and change types of parameters
    private String phoneNumber;
    private String email;
    private String verifCode;

    private OnFragmentInteractionListener mListener;

    private View fragmentView;
    private Validator validator;

    private TextView signupTitle;
    private Button signupButton;
    @NotEmpty
    private EditText editTextLastName;
    @NotEmpty
    private EditText editTextName;
    @Email
    private EditText editTextEmail;
    @NotEmpty
    private EditText editTextPhone;
    @NotEmpty
    @Password(min = 5, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE)
    private EditText editTextPassword;
    @NotEmpty
    private EditText editTextIdCard;
    @NotEmpty
    private EditText editTextExpIDCard;
    @NotEmpty
    private EditText editTextBusinessName;
    private EditText editTextNumRegistration;
    private EditText editTextNumTax;
    @NotEmpty
    private EditText editTextCityArea;
    @NotEmpty
    private EditText editTextCityName;
    @Checked
    private CheckBox cguCheckBox;
    private TextView cguLink;

    private Spinner spinnerSubdists;

    LinkedHashMap<String, String> hashMapSubdists;
    String selectedSubdistID = "nop";

    private CheckBox showPasswordCheckBox;

    private Date dateExpIDCard;
    private String userType = "customer";

    private static final int PERMISSION_REQUEST_CODE_NETSWIPE = 300;
    private static final int PERMISSION_REQUEST_CODE_NETVERIFY = 301;
    private NetverifySDK netverifySDK;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param phoneNumber phone Number.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String phoneNumber, String email, String verifCode, String userType) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(PHONE_NUMBER, phoneNumber);
        args.putString(EMAIL, email);
        args.putString(VERIF_CODE, verifCode);
        args.putString(USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(PHONE_NUMBER);
            email = getArguments().getString(EMAIL);
            verifCode = getArguments().getString(VERIF_CODE);
            userType = getArguments().getString(USER_TYPE);
        }
//        Analytics.with(getActivity()).screen("View", TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_signup, container, false);
        validator = new Validator(this);
        validator.setValidationListener(this);

        signupTitle = (TextView) fragmentView.findViewById(R.id.text_view_title_signup);
        signupButton = (Button) fragmentView.findViewById(R.id.btn_signup_user);
        editTextLastName = (EditText) fragmentView.findViewById(R.id.editTextLastNameSignup);
        editTextName = (EditText) fragmentView.findViewById(R.id.editTextNameSignup);
        editTextEmail = (EditText) fragmentView.findViewById(R.id.editTextEmailSignup);
        editTextPhone = (EditText) fragmentView.findViewById(R.id.editTextPhone);
        editTextPassword = (EditText) fragmentView.findViewById(R.id.editTextPasswordSignup);
        showPasswordCheckBox = (CheckBox) fragmentView.findViewById(R.id.checkbox_show_password);
        editTextIdCard = (EditText) fragmentView.findViewById(R.id.editTextIdCard);
        editTextExpIDCard = (EditText) fragmentView.findViewById(R.id.editTextExpIDCard);
        editTextBusinessName = (EditText) fragmentView.findViewById(R.id.editTextBusinessName);
        editTextNumRegistration = (EditText) fragmentView.findViewById(R.id.editTextNumRegistration);
        editTextNumTax = (EditText) fragmentView.findViewById(R.id.editTextTax);
        editTextCityArea = (EditText) fragmentView.findViewById(R.id.editTextCityArea);
        editTextCityName = (EditText) fragmentView.findViewById(R.id.editTextCityName);
        cguCheckBox = (CheckBox) fragmentView.findViewById(R.id.checkbox_cgu);
        cguLink = (TextView) fragmentView.findViewById(R.id.textView_CGU_link);

        new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setCustomImage(R.drawable.identify_verification_48)
                .setTitleText("Vérification d'identité")
                .setContentText("Voulez vous procéder à la vérification d'identité maintenant ?")
                .setCancelText("Non, ultérieurement!")
                .setConfirmText("Oui !")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        if (((SignupActivity) getActivity()).getVerifyWith().equals("SMS")) {
                            initializeNetverifySDK(phoneNumber.substring(1));
                            checkPermissionsAndStart(netverifySDK);
                        } else if (((SignupActivity) getActivity()).getVerifyWith().equals("email")) {
                            initializeNetverifySDK(email);
                            checkPermissionsAndStart(netverifySDK);
                        }
                    }
                })
                .show();

        if (userType.equals("merchant")) {
            /*spinner*/
            hashMapSubdists = new LinkedHashMap<String, String>();
            loadOpenSubdistributors();
            spinnerSubdists = (Spinner) fragmentView.findViewById(R.id.subdists_spinner);
//            LinkedHashMapAdapter subdistAdapter = new LinkedHashMapAdapter(getActivity(),android.R.layout.simple_spinner_dropdown_item, hashMapSubdists);
//            // initialize the adapter
//            spinnerSubdists.setAdapter(subdistAdapter);
            spinnerSubdists.setOnItemSelectedListener(new MyOnItemSelectedListener());
        } else {
            ((LinearLayout) fragmentView.findViewById(R.id.layout_subdistributor)).setVisibility(View.GONE);
        }


        cguLink.setMovementMethod(LinkMovementMethod.getInstance());
        if (userType.equals("customer")) {
            cguLink.setText(Html.fromHtml("<a href=" + GlobalConstants.CGM_URL_CUSTOMER + ">" + getResources().getString(R.string.cgu_link_text) + "</a>"));
        } else {
            cguLink.setText(Html.fromHtml("<a href=" + GlobalConstants.CGM_URL_MERCHANT + ">" + getResources().getString(R.string.cgu_link_text) + "</a>"));
        }

        if (((SignupActivity) getActivity()).getVerifyWith().equals("SMS")) {
            editTextPhone.setText(phoneNumber.substring(4), TextView.BufferType.EDITABLE);
            editTextPhone.setEnabled(false);
        } else if (((SignupActivity) getActivity()).getVerifyWith().equals("email")) {
            editTextEmail.setText(email, TextView.BufferType.EDITABLE);
            editTextEmail.setEnabled(false);
        }
        if (userType.equals("customer")) {
            editTextBusinessName.setVisibility(View.GONE);
            validator.removeRules(editTextBusinessName);
            editTextCityArea.setVisibility(View.GONE);
            validator.removeRules(editTextCityArea);
            editTextCityName.setVisibility(View.GONE);
            validator.removeRules(editTextCityName);
            editTextNumRegistration.setVisibility(View.GONE);
            validator.removeRules(editTextNumRegistration);
            editTextNumTax.setVisibility(View.GONE);
            validator.removeRules(editTextNumTax);

            signupTitle.setText(R.string.signup_title_customer);
        } else {
            signupTitle.setText(R.string.signup_title_merchant);
        }
        showPasswordCheckBox.setOnCheckedChangeListener(new MyCheckBoxChangeListener());

        editTextExpIDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth++;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String selecteddayString = "" + selectedday;
                        if (selectedday < 10) {
                            selecteddayString = "0" + selecteddayString;
                        }
                        String selectedmonthString = "" + selectedmonth;
                        if (selectedmonth < 10) {
                            selectedmonthString = "0" + selectedmonthString;
                        }
                        editTextExpIDCard.setText(selecteddayString + "/" + selectedmonthString + "/" + selectedyear);
                        try {
                            dateExpIDCard = sdf.parse(selecteddayString + "/" + selectedmonthString + "/" + selectedyear);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Veuillez choisir la date");
                mDatePicker.show();
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onValidationSucceeded() {
        //Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        String verifyWith = ((SignupActivity) getActivity()).getVerifyWith();
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();
        String[] params = {"createuser", userType, editTextEmail.getText().toString(), verifCode,
                verifyWith, editTextName.getText().toString(), editTextLastName.getText().toString(),
                editTextCityArea.getText().toString(), editTextCityName.getText().toString(),
                editTextPhone.getText().toString(), editTextPassword.getText().toString(), editTextPassword.getText().toString(),
                editTextBusinessName.getText().toString(), "companytype", editTextNumRegistration.getText().toString(),
                editTextNumTax.getText().toString(), editTextIdCard.getText().toString(), sdfDate.format(dateExpIDCard), selectedSubdistID
        };
        String createUserURL = MMMUtils.prepareSignupUrl(params);
        HttpTask httpTask = new HttpTask(getActivity().getAssets(), GlobalConstants.cat1Timeout);
        httpTask.setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(JsonObject result) {
                Log.i(TAG, "result " + result.toString());
                if (result.get("success").getAsString().equals("true")) {
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    Bundle extras = new Bundle();
                    if (((SignupActivity) getActivity()).getVerifyWith().equals("SMS")) {
                        extras.putString("login", GlobalConstants.PHONE_PREFIX + editTextPhone.getText().toString());
                    } else if (((SignupActivity) getActivity()).getVerifyWith().equals("email")) {
                        extras.putString("login", editTextEmail.getText().toString());
                    }
                    extras.putString("password", editTextPassword.getText().toString());
                    intent.putExtras(extras);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Echec")
                            .setContentText("Echec lors de la création de l'utilisateur")
                            .show();
                }
                dialog.dismiss();
            }

            //@Override
            public void onTimeout() {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.failed_connection_title))
                        .setContentText(getResources().getString(R.string.failed_connection))
                        .show();
                dialog.dismiss();
            }
        });
        httpTask.execute(createUserURL);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());
            if (view.equals(editTextPassword)) {
                message = "Le mot de passe doit contenir des chiffres et des lettres majuscules et miniscules";
            }
            if (view.equals(cguCheckBox)) {
                message = getResources().getString(R.string.cgu_check_validation_text);
            }
            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    class MyCheckBoxChangeListener implements CheckBox.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                editTextPassword.setTransformationMethod(null);
            } else {
                editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        }
    }

    public void loadOpenSubdistributors() {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();
        String[] params = {"getopensubdistributors"};
        String loadOpenSubdistsUrl = MMMUtils.prepareLoadOpenSubdistsUrl(params);
        HttpTask httpTask = new HttpTask(getActivity().getAssets(), GlobalConstants.cat1Timeout);
        httpTask.setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(JsonObject result) {
                Log.i(TAG, "result " + result.toString());
                hashMapSubdists = MMMUtils.JsonToSubdistHashMap(result);
                Log.i(TAG, "arraySubdists " + hashMapSubdists.size());
                LinkedHashMapAdapter subdistAdapter = new LinkedHashMapAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, hashMapSubdists);
                spinnerSubdists.setAdapter(subdistAdapter);
                dialog.dismiss();
            }

            //@Override
            public void onTimeout() {

                dialog.dismiss();
            }
        });
        httpTask.execute(loadOpenSubdistsUrl);
    }

    class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selectedSubdistID = ((LinkedHashMapAdapter) parent.getAdapter()).getItemKey(pos);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

    public void initializeNetverifySDK(String merchantScanReference) {
        try {
            // You can get the current SDK version using the method below.
            // NetverifySDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported (camera available,
            // ARMv7 processor with Neon, Android 4.0 or higher).
            // NetverifySDK.isSupportedPlatform(this);

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(getActivity(), GlobalConstants.ANONYMOUS_ID4, GlobalConstants.ANONYMOUS_ID5, JumioDataCenter.EU);
            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setRequireVerification(true);
            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code
            // String alpha3 = IsoCountryConverter.convertToAlpha3("AT");
            // netverifySDK.setPreselectedCountry("AUT");
            // ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
            // documentTypes.add(NVDocumentType.PASSPORT);
            // netverifySDK.setPreselectedDocumentTypes(documentTypes);
            // netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

            // To hide the country flag on the info bar, disable the following setting.
            // netverifySDK.setShowFlagOnInfoBar(false);

            // The merchant scan reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
            netverifySDK.setMerchantScanReference(merchantScanReference);

            // Use the following property to identify the scan in your reports (max. 100 characters).
            // netverifySDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

            // You can also set a customer identifier (max. 100 characters).
            // Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
            // netverifySDK.setCustomerId("CUSTOMERID");

            // Use the following method to pass first and last name to Fastfill for name match.
            // netverifySDK.setName("FIRSTNAME LASTNAME");

            // You can enable face match during the ID verification for a specific transaction. This setting overrides your default Jumio merchant settings.
            // netverifySDK.setRequireFaceMatch(true);

            // Use the following method to disable ePassport scanning
            // netverifySDK.setEnableEpassport(false);

            // Set the default camera position
            // netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Use the following method to only support IDs where data can be extracted on mobile only
            // netverifySDK.setDataExtractionOnMobileOnly(true);

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
            netverifySDK.setCallbackUrl(GlobalConstants.CALLBACK_URL_NETVERIFY);

            // Use the following method to explicitly send debug-info to Jumio. (default: false)
            // Only set this property to true if you are asked by our Jumio support personnel.
            // netverifySDK.sendDebugInfoToJumio(true);

        } catch (PlatformNotSupportedException e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), "Resource files are missing", Toast.LENGTH_LONG).show();
        }
    }

    public void checkPermissionsAndStart(MobileSDK sdk) {
        if (!MobileSDK.hasAllRequiredPermissions(getActivity())) {
            //acquire missing permissions
            String[] mp = MobileSDK.getMissingPermissions(getActivity());

            int code;
            if (sdk instanceof NetswipeSDK)
                code = PERMISSION_REQUEST_CODE_NETSWIPE;
            else if (sdk instanceof NetverifySDK)
                code = PERMISSION_REQUEST_CODE_NETVERIFY;
            else {
                Toast.makeText(getActivity(), "invalid sdk instance!", Toast.LENGTH_LONG).show();
                return;
            }

            ActivityCompat.requestPermissions(getActivity(), mp, code);
            //the result is received in onRequestPermissionsResult
        } else {
            startSdk(sdk);
        }
    }

    public void startSdk(MobileSDK sdk) {
        try {
            //sdk.start();
            startActivityForResult(netverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);
        } catch (MissingPermissionException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (data == null) {
                Log.d(TAG,"data null");
                return;
            }
            if (resultCode == NetverifySDK.RESULT_CODE_SUCCESS || resultCode == NetverifySDK.RESULT_CODE_BACK_WITH_SUCCESS) {
                String scanReference = (data == null) ? "" : data.getStringExtra(NetverifySDK.RESULT_DATA_SCAN_REFERENCE);
                Log.d(TAG,"scanReference:"+ scanReference);

                updateUserVerification(scanReference);
                NetverifyDocumentData documentData = (data == null) ? null : (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.RESULT_DATA_SCAN_DATA);
                if(documentData!=null){
                    editTextName.setText(documentData.getFirstName());
                    editTextLastName.setText(documentData.getLastName());
                    editTextIdCard.setText(documentData.getIdNumber());
                }
                NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;
            } else if (resultCode == NetverifySDK.RESULT_CODE_CANCEL) {
                String errorMessage = data.getStringExtra(NetverifySDK.RESULT_DATA_ERROR_MESSAGE);
                Log.d(TAG,"errorMessage:"+ errorMessage);
                int errorCode = data.getIntExtra(NetverifySDK.RESULT_DATA_ERROR_CODE, 0);
            }

            //at this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed
            if (netverifySDK != null) {
                netverifySDK.destroy();
                netverifySDK = null;
            }

        } else if (requestCode == NetswipeSDK.REQUEST_CODE) {
            if (data == null)
                return;
            ArrayList<String> scanAttempts = data.getStringArrayListExtra(NetswipeSDK.EXTRA_SCAN_ATTEMPTS);

            if (resultCode == Activity.RESULT_OK) {
                NetswipeCardInformation cardInformation = data.getParcelableExtra(NetswipeSDK.EXTRA_CARD_INFORMATION);

                cardInformation.clear();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                String errorMessage = data.getStringExtra(NetswipeSDK.EXTRA_ERROR_MESSAGE);
                int errorCode = data.getIntExtra(NetswipeSDK.EXTRA_ERROR_CODE, 0);
            }

            //at this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed
//            if (netswipeSDK != null) {
//                netswipeSDK.destroy();
//                netswipeSDK = null;
//            }
        }
    }

    /**
     * Guarantee the update of userVerification on prod even callBack wasn't called by jumio
     * @param jumioIdScanreference
     */
    public void updateUserVerification(String jumioIdScanreference){
        String userName="";
        if (((SignupActivity) getActivity()).getVerifyWith().equals("SMS")) {
            userName =phoneNumber.substring(1);
        } else if (((SignupActivity) getActivity()).getVerifyWith().equals("email")) {
            userName =email;
        }
        String urlString = GlobalConstants.API_URL + "?action=updateuserverificationstatus&merchantIdScanReference=" + userName + "&jumioIdScanReference=" + jumioIdScanreference;
        HttpTask httpTask = new HttpTask(getActivity().getAssets(), GlobalConstants.cat1Timeout);
        httpTask.setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(JsonObject result) {
                Log.i(TAG, "result " + result.toString());
            }

            @Override
            public void onTimeout() {
            }
        });
        httpTask.execute(urlString);
    }
}
