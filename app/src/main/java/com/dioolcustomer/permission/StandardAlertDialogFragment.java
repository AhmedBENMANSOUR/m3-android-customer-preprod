package com.dioolcustomer.permission;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dioolcustomer.R;
import com.dioolcustomer.utils.StringUtils;


public class StandardAlertDialogFragment extends DialogFragment {


    public static final String TITLE = "title";
    private static final String POSITIVE_RES_ID = "positive_res_id";
    private static final String NEGATIVE_RES_ID = "negative_res_id";
    private static final String CONTENT = "content";
    private static final String WITH_ASK_AGAIN_OPTION = "with_ask_again_option";
    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;


    private TextView mContentTextView;
    private TextView mTitleTextView;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private LinearLayout mAskAgainOptionContainer;
    private ToggleButton mAskAgainOption;

    public static StandardAlertDialogFragment newInstance(String title, String content, int positiveResId,
                                                          int negativeResId, OnClickListener positiveListener,
                                                          OnClickListener negativeListener, boolean withAskAgainOption) {
        StandardAlertDialogFragment dialogFragment = new StandardAlertDialogFragment();
        dialogFragment.setPositiveListener(positiveListener);
        dialogFragment.setNegativeListener(negativeListener);
        Bundle args = new Bundle();
        if (StringUtils.isEmpty(title) == false) {
            args.putString(TITLE, title);
        }
        if (positiveResId > 0) {
            args.putInt(POSITIVE_RES_ID, positiveResId);
        }
        if (negativeResId > 0) {
            args.putInt(NEGATIVE_RES_ID, negativeResId);
        }
        if (StringUtils.isEmpty(content) == false) {
            args.putString(CONTENT, content);
        }
        args.putBoolean(WITH_ASK_AGAIN_OPTION, withAskAgainOption);

        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    private void setPositiveListener(OnClickListener listener) {
        mPositiveListener = listener;
    }

    private void setNegativeListener(OnClickListener listener) {
        mNegativeListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_general_alert, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        String title = getArguments().getString(TITLE);
        int positiveResId = getArguments().getInt(POSITIVE_RES_ID);
        int negativeResId = getArguments().getInt(NEGATIVE_RES_ID);
        String content = getArguments().getString(CONTENT);
        boolean withAskAgainOption = getArguments().getBoolean(WITH_ASK_AGAIN_OPTION);
        initView(view);
        bindView(title, content, positiveResId, negativeResId, withAskAgainOption);
        return view;
    }

    private void initView(View view) {
        mContentTextView = (TextView) view.findViewById(R.id.dialog_fragment_general_alert_content);
        mTitleTextView = (TextView) view.findViewById(R.id.dialog_fragment_general_alert_title);
        mPositiveButton = (Button) view.findViewById(R.id.dialog_fragment_general_alert_positive_btn);
        mNegativeButton = (Button) view.findViewById(R.id.dialog_fragment_general_alert_negative_btn);
        mAskAgainOptionContainer =
                (LinearLayout) view.findViewById(R.id.dialog_fragment_general_alert_ask_again_container);
        mAskAgainOption = (ToggleButton) view.findViewById(R.id.dialog_fragment_general_alert_ask_again_check_box);
    }


    /**
     * @param title
     * @param positiveResId
     * @param negativeResId
     * @param content
     */
    private void bindView(String title, String content, int positiveResId, int negativeResId,
                          boolean withAskAgainOption) {
        if (StringUtils.isEmpty(title) == false) {
            mTitleTextView.setText(title);
        }

        if (withAskAgainOption == true) {
            mAskAgainOptionContainer.setVisibility(View.VISIBLE);
        } else {
            mAskAgainOptionContainer.setVisibility(View.GONE);
        }
        if (positiveResId > 0) {
            mPositiveButton.setVisibility(View.VISIBLE);
            mPositiveButton.setText(positiveResId);
            mPositiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mPositiveListener != null) {
                        mPositiveListener.onClick(v, mAskAgainOption.isChecked() == false);
                    }
                    StandardAlertDialogFragment.this.dismiss();
                }
            });
        } else {
            mPositiveButton.setVisibility(View.GONE);
        }
        if (negativeResId > 0) {
            mNegativeButton.setVisibility(View.VISIBLE);
            mNegativeButton.setText(negativeResId);
            mNegativeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mNegativeListener != null) {
                        mNegativeListener.onClick(v, mAskAgainOption.isChecked() == false);
                    }
                    StandardAlertDialogFragment.this.dismiss();
                }
            });
        } else {
            mNegativeButton.setVisibility(View.GONE);
        }
        if (StringUtils.isEmpty(content) == false) {
            mContentTextView.setText(content);
        }

    }


    public interface OnClickListener {

        boolean onClick(View v, boolean askAgain);
    }

}
