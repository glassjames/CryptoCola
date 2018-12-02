package glass.james.cryptocola;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import android.graphics.Typeface;

import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private String selectedDrink = "";

    public static final String TAG = "NfcDemo";

    private TextView info;
    private NfcAdapter adapter;

    private ImageView cokeimg;
    private ImageView pepsiimg;
    private ImageView mtndewimg;
    private ImageView drpepperimg;
    private ImageView rootbeerimg;
    private ImageView lemonadeimg;

//    TextView output;
//    ImageButton anw;
//    ImageButton coke;
//    ImageButton dp;
//    ImageButton fanta;
//    ImageButton mm;
//    ImageButton pepsi;

    private Button coke;
    private Button pepsi;
    private Button mtndew;
    private Button drpepper;
    private Button rootbeer;
    private Button lemonade;

    private Button vend;
    private LinearLayout vendingmachine;

    private LinearLayout cokeOption;
    private LinearLayout pepsiOption;
    private LinearLayout mtndewOption;
    private LinearLayout drpepperOption;
    private LinearLayout rootbeerOption;
    private LinearLayout lemonadeOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setFont((TextView) findViewById(R.id.appname));
//        setFont((TextView) findViewById(R.id.nfctext1));
//        setFont((TextView) findViewById(R.id.nfctext2));
//        setFont((TextView) findViewById(R.id.btcinfo));
//        setFont((TextView) findViewById(R.id.btcamount));

        ////////////////////////////////////////////////////////

        initializeComponents();

        hideVendingMachine();

        coke.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectCoke();
                }
            }
        );

        pepsi.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectPepsi();
                    }
                }
        );

        mtndew.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectMtnDew();
                    }
                }
        );

        drpepper.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectDrPepper();
                    }
                }
        );

        rootbeer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectRootBeer();
                    }
                }
        );

        lemonade.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectLemonade();
                    }
                }
        );

        vend.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payAndVend();
                    }
                }
        );

        deselectAll();

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

//    public void dispenseANW (View v){
//        output.setText("ANW");
//    }
//    public void dispenseCoke (View v){
//        output.setText("Coke");
//    }
//    public void dispenseDP (View v){
//        output.setText("DP");
//    }
//    public void dispenseFanta (View v){
//        output.setText("Fanta");
//    }
//    public void dispenseMM (View v){
//        output.setText("MM");
//    }
//    public void dispensePepsi (View v){
//        output.setText("Pepsi");
//    }



    public void activateButton(String drink){

//        if((drink.equals("Rootbeer"))){
//            anw.setEnabled(true);
//        }
//        else if((drink.equals("Coca-Cola"))){
//            coke.setEnabled(true);
//        }
//        else if((drink.equals("Dr. Pepper"))){
//            dp.setEnabled(true);
//        }
//        else if((drink.equals("Lemonade"))){
//            mm.setEnabled(true);
//        }
//        else if((drink.equals("Pepsi"))){
//            pepsi.setEnabled(true);
//        }
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
            showVendingMachine();

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

                        //activateButton(result);
                        showDrink(result);

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

    public void setFont(TextView tx) {
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/CatatanPerjalanan.ttf");
        tx.setTypeface(custom_font);
    }

    public void selectCoke() {
        deselectAll();

        if(selectedDrink.equals("Coca-Cola")) {
            cokeimg.setImageResource(R.drawable.emptybox);
            selectedDrink = "";
        }
        else {
            cokeimg.setImageResource(R.drawable.coke);
            selectedDrink = "Coca-Cola";
        }
    }

    public void selectPepsi() {
        deselectAll();

        if(selectedDrink.equals("Pepsi")) {
            pepsiimg.setImageResource(R.drawable.emptybox);
            selectedDrink = "";
        }
        else {
            pepsiimg.setImageResource(R.drawable.pepsi);
            selectedDrink = "Pepsi";
        }
    }

    public void selectMtnDew() {
        deselectAll();

        if(selectedDrink.equals("Mountain Dew")) {
            mtndewimg.setImageResource(R.drawable.emptybox);
            selectedDrink = "";
        }
        else {
            mtndewimg.setImageResource(R.drawable.mtndew);
            selectedDrink = "Mountain Dew";
        }
    }

    public void selectDrPepper() {
        deselectAll();

        if(selectedDrink.equals("Dr. Pepper")) {
            drpepperimg.setImageResource(R.drawable.emptybox);
            selectedDrink = "";
        }
        else {
            drpepperimg.setImageResource(R.drawable.drpepper);
            selectedDrink = "Dr. Pepper";
        }
    }

    public void selectRootBeer() {
        deselectAll();

        if(selectedDrink.equals("A&W Root Beer")) {
            rootbeerimg.setImageResource(R.drawable.emptybox);
            selectedDrink = "";
        }
        else {
            rootbeerimg.setImageResource(R.drawable.rootbeer);
            selectedDrink = "A&W Root Beer";
        }
    }

    public void selectLemonade() {
        deselectAll();

        if(selectedDrink.equals("Lemonade")) {
            lemonadeimg.setImageResource(R.drawable.emptybox);
            selectedDrink = "";
        }
        else {
            lemonadeimg.setImageResource(R.drawable.lemonade);
            selectedDrink = "Lemonade";
        }
    }

    public void deselectAll() {
        cokeimg.setImageResource(R.drawable.emptybox);
        pepsiimg.setImageResource(R.drawable.emptybox);
        mtndewimg.setImageResource(R.drawable.emptybox);
        drpepperimg.setImageResource(R.drawable.emptybox);
        rootbeerimg.setImageResource(R.drawable.emptybox);
        lemonadeimg.setImageResource(R.drawable.emptybox);
    }

    public void payAndVend() {
        if(!selectedDrink.equals("")) {
            //time to order drink
        }
        else {
            //no drink selected error
        }
    }

    public void hideVendingMachine() {
        vendingmachine.setVisibility(View.GONE);

        cokeOption.setVisibility(View.GONE);
        pepsiOption.setVisibility(View.GONE);
        mtndewOption.setVisibility(View.GONE);
        drpepperOption.setVisibility(View.GONE);
        rootbeerOption.setVisibility(View.GONE);
        lemonadeOption.setVisibility(View.GONE);
    }

    public void showVendingMachine() {
        vendingmachine.setVisibility(View.VISIBLE);
    }

    public void showDrink(String drink) {
        if(drink.equals("Coca-Cola")) {
            cokeOption.setVisibility(View.VISIBLE);
        }
        else if(drink.equals("Pepsi")) {
            pepsiOption.setVisibility(View.VISIBLE);
        }
        else if(drink.equals("Mountain Dew")) {
            mtndewOption.setVisibility(View.VISIBLE);
        }
        else if(drink.equals("Dr. Pepper")) {
            drpepperOption.setVisibility(View.VISIBLE);
        }
        else if(drink.equals("A&W Root Beer")) {
            rootbeerOption.setVisibility(View.VISIBLE);
        }
        else if(drink.equals("Lemonade")) {
            lemonadeOption.setVisibility(View.VISIBLE);
        }
        else {
            //error
        }
    }

    private void initializeComponents() {
        info = findViewById(R.id.info);

        adapter = NfcAdapter.getDefaultAdapter(this);

        cokeimg = findViewById(R.id.cokeimg);
        pepsiimg = findViewById(R.id.pepsiimg);
        mtndewimg = findViewById(R.id.mtndewimg);
        drpepperimg = findViewById(R.id.drpepperimg);
        rootbeerimg = findViewById(R.id.rootbeerimg);
        lemonadeimg = findViewById(R.id.lemonadeimg);

        coke = findViewById(R.id.coke);
        pepsi = findViewById(R.id.pepsi);
        mtndew = findViewById(R.id.mtndew);
        drpepper = findViewById(R.id.drpepper);
        rootbeer = findViewById(R.id.rootbeer);
        lemonade = findViewById(R.id.lemonade);

        vend = findViewById(R.id.vend);
        vendingmachine = findViewById(R.id.vendingmachine);

        cokeOption = findViewById(R.id.cokecontainer);
        pepsiOption = findViewById(R.id.pepsicontainer);
        mtndewOption = findViewById(R.id.mtndewcontainer);
        drpepperOption = findViewById(R.id.drpeppercontainer);
        rootbeerOption = findViewById(R.id.rootbeercontainer);
        lemonadeOption = findViewById(R.id.lemonadecontainer);
    }
}
