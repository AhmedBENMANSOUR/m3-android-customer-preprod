package com.dioolcustomer.callbacks;

import com.dioolcustomer.models.UserNetwork;

/**
 * Created by sihem.messaoui on 14/08/2016.
 */
public class OpenNetworkDetails {
    UserNetwork mNetwork;

    public OpenNetworkDetails(UserNetwork mNetwork) {
        this.mNetwork = mNetwork;
    }

    public UserNetwork getmNetwork() {
        return mNetwork;
    }

    public void setmNetwork(UserNetwork mNetwork) {
        this.mNetwork = mNetwork;
    }
}
