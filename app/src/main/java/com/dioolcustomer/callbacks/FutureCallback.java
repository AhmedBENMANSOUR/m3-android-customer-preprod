package com.dioolcustomer.callbacks;

public interface FutureCallback<T> {
    /**
     * onCompleted is called by the Future with the result or exception of the asynchronous operation.
     * @param result NetworkResult returned from the operation
     */
    public void onCompleted(T result);
    public void onTimeout();
}