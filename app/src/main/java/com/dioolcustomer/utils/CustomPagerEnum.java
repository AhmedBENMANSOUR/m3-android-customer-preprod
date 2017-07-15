package com.dioolcustomer.utils;

import com.dioolcustomer.R;

/**
 * Created by sihem.messaoui on 03/06/2016.
 */
public enum CustomPagerEnum {

    RED(R.string.value_customer, R.layout.view_value_customer),
    BLUE(R.string.value_merchant, R.layout.view_value_merchant);

    private int mTitleResId;
    private int mLayoutResId;

    CustomPagerEnum(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}

