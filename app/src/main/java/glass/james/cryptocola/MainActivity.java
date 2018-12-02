package glass.james.cryptocola;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.nfc.NdefMessage;
import java.io.UnsupportedEncodingException;
import android.nfc.tech.Ndef;
import android.nfc.NdefRecord;
import java.util.Arrays;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.Button;

import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.tech.NfcA;
import android.widget.ImageButton;
import android.app.Activity;
import java.nio.charset.Charset;
import android.os.Parcelable;
import android.widget.LinearLayout;

import android.os.AsyncTask;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String selectedDrink = "";

    private TextView info;
    private NfcAdapter adapter;

    private ImageView cokeimg;
    private ImageView pepsiimg;
    private ImageView mtndewimg;
    private ImageView drpepperimg;
    private ImageView rootbeerimg;
    private ImageView lemonadeimg;

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

    int PERMISSIONS_REQUEST_READ_PHONE_STATE;
    private static final String url = "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3267945";
    private static final String user = "sql3267945";
    private static final String pass = "HmfsUH6Fzp";
    String serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) { }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }

        }
        else {
            // Permission has already been granted
        }

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

        if(adapter == null) {
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

        try {
            intentHandler(getIntent());
        }
        catch(UnsupportedEncodingException e) {
            Log.e("CryptoColaLog", "Unsupported Encoding", e);
        }
    }

    public void checkAccount() {
        handleAccount checkAccount = new handleAccount();
        checkAccount.execute("");
    }

    public void createAccount() {
        createAccountId accountCreate = new createAccountId();
        accountCreate.execute("");
    }

    public void checkUserCredits() {
        checkUserCredits userCreditCheck = new checkUserCredits();
        userCreditCheck.execute("");
    }
    public void updateDrinkCount(){
        updateDrinkInv drinkCountUpdate = new updateDrinkInv();
        drinkCountUpdate.execute("");
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
        try {
            intentHandler(intent);
        }
        catch(UnsupportedEncodingException e) {
            Log.e("CryptoColaLog", "Unsupported Encoding", e);
        }
    }

    private void intentHandler(Intent intent) throws UnsupportedEncodingException {
        //Log.e("CryptoColaLog", "Handle Time: " + intent.getAction());

        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            showVendingMachine();

            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Ndef ndef = Ndef.get(tagFromIntent);
            if(ndef == null) {
                // NDEF is not supported by this Tag.
                return;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();

            Log.e("CryptoColaLog", "---------------- SODA TIME ----------------");

            for(NdefRecord ndefRecord : records) {
                if(ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        byte[] payload = ndefRecord.getPayload();
                        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                        int languageCodeLength = payload[0] & 0063;

                        //this result is a string for the drink name. Ex: Pepsi
                        String result =  new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

                        showDrink(result);

                        Log.e("CryptoColaLog", result);
                    }
                    catch(UnsupportedEncodingException e) {
                        //Log.e(CryptoColaLog, "Unsupported Encoding", e);
                    }
                }
            }
            Log.e("CryptoColaLog", "-------------------------------------------");

            try {
                serialNumber = Build.getSerial();
            }
            catch(SecurityException e) {
                //Log.e("CryptoColaLog", "Permission not accepted", e);
            }

            checkAccount();

        }
        else {
            //Log.e("CryptoColaLog", "Intent is Bad");
        }
    }

    private class handleAccount extends AsyncTask<String, Void, String> {
        String res = "";
        Boolean found = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success");

                String result = "";
//                String result = "Database Connection Successful\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select distinct userid from Users");
                ResultSetMetaData rsmd = rs.getMetaData();

                while(rs.next()) {

                    if((rs.getString(1).toString()).equals(serialNumber)) {
                        found = true;
                    }
//                    result += rs.getString(1).toString() + "\n";


                }
                res = result;
                con.close();
            }
            catch(Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            if(found) {
                Toast.makeText(MainActivity.this, "Connected to account", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "No account found, creating now", Toast.LENGTH_SHORT).show();
                createAccount();
            }
        }
    }

    private class createAccountId extends AsyncTask<String, Void, String> {
        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Please wait creating account", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success2");

                String result = "";
                Statement st = con.createStatement();
                int rs = st.executeUpdate("insert into Users (userid, balance) values ('" + serialNumber + "' , 10)");

//                ResultSetMetaData rsmd = rs.getMetaData();
//                while (rs.next()) {}

                res = result;
                con.close();
            }
            catch(Exception e) {
                e.printStackTrace();
                res = e.toString();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, "Account created", Toast.LENGTH_SHORT).show();
        }
    }

    private class checkUserCredits extends AsyncTask<String, Void, String> {
        String res = "";
        Boolean currentBalanceCheck = false;
        Integer currentUserBalance;
        String currentUserBalanceString;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Checking Credits", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success2");

                String result = "";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select balance from Users where userid='" + serialNumber + "'");
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    result += rs.getString(1).toString();
                }


                currentUserBalance = Integer.valueOf(result);

                if (currentUserBalance >= 1) {

                    currentBalanceCheck = true;

                    currentUserBalance = currentUserBalance - 1;

                    currentUserBalanceString = String.valueOf(currentUserBalance);


                    Statement st2 = con.createStatement();
                    int rs2 = st2.executeUpdate("UPDATE Users SET balance='" + currentUserBalanceString + "' WHERE userid='" + serialNumber + "'");

                }

                con.close();
                res = result;
            }
            catch(Exception e) {

                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            if(currentBalanceCheck) {
                Toast.makeText(MainActivity.this, "Purchase Complete", Toast.LENGTH_SHORT).show();


            }
            else {
                Toast.makeText(MainActivity.this, "Not Enough Credits", Toast.LENGTH_SHORT).show();
            }

        }

    }


    private class updateDrinkInv extends AsyncTask<String, Void, String> {
        String res = "";
        Boolean drinkAvaibleCheck = false;
        Integer CurrentDrinkCount;
        String CurrentDrinkCountString;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Checking Inventory", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success3");

                String result = "";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select stock from Drinks where drink='" + selectedDrink + "'");
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    result += rs.getString(1).toString();
                }


                CurrentDrinkCount = Integer.valueOf(result);

                if (CurrentDrinkCount >= 1) {

                    drinkAvaibleCheck = true;

                    CurrentDrinkCount = CurrentDrinkCount - 1;

                    CurrentDrinkCountString = String.valueOf(CurrentDrinkCount);


                    Statement st2 = con.createStatement();
                    int rs2 = st2.executeUpdate("UPDATE Drinks SET stock='" + CurrentDrinkCountString + "' WHERE drink='" + selectedDrink + "'");

                }

                con.close();
                res = result;
            }
            catch(Exception e) {

                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            if(drinkAvaibleCheck) {
                Toast.makeText(MainActivity.this, "In stock, charging account", Toast.LENGTH_SHORT).show();
                checkUserCredits();
            }
            else {
                Toast.makeText(MainActivity.this, "Out of stock", Toast.LENGTH_SHORT).show();
            }

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
            updateDrinkCount();
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
