package com.dioolcustomer.callbacks;

import com.dioolcustomer.models.UserNetwork;

/**
 * Created by sihem.messaoui on 21/08/2016.
 */
public class OpenChildTransaction {
    UserNetwork mNetwork;


    public OpenChildTransaction(UserNetwork mNetwork) {
        this.mNetwork = mNetwork;
    }

    public UserNetwork getmNetwork() {
        return mNetwork;
    }

    public void setmNetwork(UserNetwork mNetwork) {
        this.mNetwork = mNetwork;
    }
}
