package com.example.moslah_hamza.finance;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Created by Moslah_Hamza on 23/04/2017.
 */

public class ConvertFrame extends Fragment {
    String TAG = "Response";
    Button bt;
    Spinner from;
    Spinner to;
    EditText cur;
    String getFrom, getTo;
    SoapPrimitive resultString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.convert_frame,container,false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bt = (Button) view.findViewById(R.id.bt);
        cur = (EditText) view.findViewById(R.id.cur);
        from = (Spinner) view.findViewById(R.id.from);
        to = (Spinner) view.findViewById(R.id.to);
        List<Currency> currencies1 = new ArrayList<>();
        List<String> currencies = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            currencies1.addAll(Currency.getAvailableCurrencies());
        }

        for(Currency currency : currencies1){
            currencies.add(currency.getCurrencyCode());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, currencies);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        from.setAdapter(dataAdapter);
        to.setAdapter(dataAdapter);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFrom = from.getSelectedItem().toString();
                getTo = to.getSelectedItem().toString();
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
        });
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            calculate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            double res = Double.valueOf(resultString.toString()) * Double.valueOf(cur.getText().toString());
            Toast.makeText(getActivity(), "Response " + res, Toast.LENGTH_LONG).show();
        }

    }

    public void calculate() {
        String SOAP_ACTION = "http://www.webserviceX.NET/ConversionRate";
        String METHOD_NAME = "ConversionRate";
        String NAMESPACE = "http://www.webserviceX.NET/";
        String URL = "http://www.webservicex.net/CurrencyConvertor.asmx";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("FromCurrency", getFrom);
            Request.addProperty("ToCurrency", getTo);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Converion: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
}

