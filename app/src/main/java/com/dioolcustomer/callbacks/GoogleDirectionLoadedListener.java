package com.dioolcustomer.callbacks;


import java.util.HashMap;
import java.util.List;

public interface GoogleDirectionLoadedListener {

    public void onSuccessfullRouteFetch(List<List<HashMap<String, String>>> result);
    public void onFail();

}
