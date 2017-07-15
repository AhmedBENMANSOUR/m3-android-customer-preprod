package com.dioolcustomer.callbacks;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.dioolcustomer.utils.NoSSLv3SocketFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class HttpTask extends AsyncTask<String, Void, JsonObject> {
	public String TAG =getClass().getSimpleName();
	Context context;
	private AssetManager assetManager;
	private int operationTimeout;
	public static void sendHttp(Map<String, Object> requestparams)
			throws IOException {

	}
    FutureCallback<JsonObject> futureCallback;

	public HttpTask(AssetManager assetManager,int timeout) {
		this.assetManager = assetManager;
		this.operationTimeout=timeout;
	}

	@Override
	protected JsonObject doInBackground(String... reqparams){
		HttpResponse response1 = null;
		try {
			String urlString = reqparams[0];

			//HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);

			URL url = new URL(urlString);
            if( urlString.contains("https")){

                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                InputStream caInput = new BufferedInputStream(assetManager.open("emonize.cer"));
                java.security.cert.Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLSv1");
                context.init(null, tmf.getTrustManagers(), null);

                // Tell the URLConnection to use a SocketFactory from our SSLContext
                SSLSocketFactory noSSLv3Factory = new NoSSLv3SocketFactory(context.getSocketFactory());

                HttpsURLConnection urlConnection =
                        (HttpsURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(operationTimeout);
                urlConnection.setReadTimeout(operationTimeout);

				urlConnection.setSSLSocketFactory(noSSLv3Factory);

                urlConnection.setRequestMethod("POST");
                InputStream in = urlConnection.getInputStream();


                StringWriter writer = new StringWriter();
                IOUtils.copy(in, writer, "UTF-8");
                String result = writer.toString();
                JsonParser parser = new JsonParser();
                return parser.parse(result.toString()).getAsJsonObject();
			}else{
                HttpURLConnection urlConnection =
                        (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(operationTimeout);
                urlConnection.setReadTimeout(operationTimeout);
                urlConnection.setRequestMethod("POST");
                InputStream in = urlConnection.getInputStream();


                StringWriter writer = new StringWriter();
                IOUtils.copy(in, writer, "UTF-8");
                String result = writer.toString();
                JsonParser parser = new JsonParser();
                return parser.parse(result.toString()).getAsJsonObject();
            }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    public void setCallback(FutureCallback<JsonObject> futureCallback){
        this.futureCallback=futureCallback;
    }

    @Override
    protected void onPostExecute(JsonObject jsonObject) {
        if(jsonObject!=null) {
            futureCallback.onCompleted(jsonObject);
        }else{
			futureCallback.onTimeout();
			Log.i(TAG, "Timeout");
		}
    }
}
