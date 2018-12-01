package glass.james.cryptocola;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

import android.nfc.tech.NfcA;
import android.os.AsyncTask;

import android.util.Log;

import java.nio.charset.Charset;

import android.os.Parcelable;
import android.nfc.NdefMessage;

import java.io.UnsupportedEncodingException;

import android.nfc.tech.Ndef;
import android.nfc.NdefRecord;

import java.util.Arrays;



public class MainActivity extends AppCompatActivity {

    public static final String TAG = "NfcDemo";

    private TextView info;
    private NfcAdapter adapter;

    TextView output;
    ImageButton anw;
    ImageButton coke;
    ImageButton dp;
    ImageButton fanta;
    ImageButton mm;
    ImageButton pepsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = findViewById(R.id.info);

        adapter = NfcAdapter.getDefaultAdapter(this);

        output = (TextView) findViewById(R.id.output);
        anw = (ImageButton) findViewById(R.id.anw);
        coke = (ImageButton) findViewById(R.id.coke);
        dp = (ImageButton) findViewById(R.id.dp);
        fanta = (ImageButton) findViewById(R.id.fanta);
        mm = (ImageButton) findViewById(R.id.mm);
        pepsi = (ImageButton) findViewById(R.id.pepsi);

        anw.setEnabled(false);
        coke.setEnabled(false);
        dp.setEnabled(false);
        fanta.setEnabled(false);
        mm.setEnabled(false);
        pepsi.setEnabled(false);


        if (adapter == null) {
            info.setText("NFC is not supported on this device.");
            finish();
            return;
        }
        else if (!adapter.isEnabled()) {
            info.setText("NFC is disabled.");
        }
        else {
            info.setText("NFC is enabled.");
        }

//        intentHandler(getIntent());
        try {
            intentHandler(getIntent());
        } catch (UnsupportedEncodingException e) {
            Log.e("CryptoColaLog", "Unsupported Encoding", e);
        }
    }

    public void dispenseANW (View v){
        output.setText("ANW");
    }
    public void dispenseCoke (View v){
        output.setText("Coke");
    }
    public void dispenseDP (View v){
        output.setText("DP");
    }
    public void dispenseFanta (View v){
        output.setText("Fanta");
    }
    public void dispenseMM (View v){
        output.setText("MM");
    }
    public void dispensePepsi (View v){
        output.setText("Pepsi");
    }



    public void activateButton(String drink){

        if((drink.equals("Rootbeer"))){
            anw.setEnabled(true);
        }
        else if((drink.equals("Coca-Cola"))){
            coke.setEnabled(true);
        }
        else if((drink.equals("Dr. Pepper"))){
            dp.setEnabled(true);
        }
        else if((drink.equals("Lemonade"))){
            mm.setEnabled(true);
        }
        else if((drink.equals("Pepsi"))){
            pepsi.setEnabled(true);
        }
    }

    public boolean checkAccount(){

//        Log.i("TAG","android.os.Build.SERIAL: " + Build.SERIAL);
//        String serialNumber = Build.getSerial();

        return true;
    }
    public boolean checkCredits(){
        return true;
    }
    public boolean checkStock(){

        return true;
    }
    public void subCredits(){

    }



    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = new Intent(this.getApplicationContext(), this.getClass());
        final PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);

        adapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        adapter.disableForegroundDispatch(this);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        intentHandler(intent);
        try {
            intentHandler(intent);
        } catch (UnsupportedEncodingException e) {
            Log.e("CryptoColaLog", "Unsupported Encoding", e);
        }
    }

    private void intentHandler(Intent intent) throws UnsupportedEncodingException {
        //Log.e("CryptoColaLog", "Handle Time: " + intent.getAction());

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Ndef ndef = Ndef.get(tagFromIntent);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();

            Log.e("CryptoColaLog", "---------------- SODA TIME ----------------");

            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        byte[] payload = ndefRecord.getPayload();
                        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                        int languageCodeLength = payload[0] & 0063;

                        //this result is a string for the drink name. Ex: Pepsi
                        String result =  new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

                        activateButton(result);
                        Log.e("CryptoColaLog", result);
                    } catch (UnsupportedEncodingException e) {
                        //Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }
            Log.e("CryptoColaLog", "-------------------------------------------");

        }
        else {
            //Log.e("CryptoColaLog", "Intent is Bad");
        }


    }

}
