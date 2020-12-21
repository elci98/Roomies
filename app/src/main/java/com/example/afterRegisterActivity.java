package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.provider.ContactsContract.Contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class afterRegisterActivity extends AppCompatActivity
{

    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int REQUEST_READ_CONTACTS = 1;
    private static final int REQUEST_SEND_SMS = 2;
    private static final String DEBUG_TAG = "MY_DEBUG";
    private Button create, join;
    private EditText etAprName;
    private Dialog d;
    private Map<String, String> contacts = new HashMap<>();
    private ProgressBar pb;
    private String aprKey, Name;
    private DatabaseReference dbRef, reference;
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_register);
        create = findViewById(R.id.createApr);
        join = findViewById(R.id.joinApr);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Name=snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        join.setOnClickListener((v)->{

            Intent i=new Intent(afterRegisterActivity.this, JoinAprActivity.class);
            i.putExtra("com.example.roomies.Name",Name);
            i.putExtra("com.example.roomies.Uid",mAuth.getUid());
            startActivity(i);
        });
        create.setOnClickListener((v)->
        {
            d = new Dialog(this);
            d.setContentView(R.layout.create_apartment);
            d.setTitle("Create apartment");
            d.setCancelable(true);
            d.show();
            pb = d.findViewById(R.id.progressBar);
            etAprName = d.findViewById(R.id.apartmentName);
            dbRef = FirebaseDatabase.getInstance().getReference("/Apartments");
            aprKey = dbRef.push().getKey();
            (d.findViewById(R.id.createAprSms)).setOnClickListener((v1 ->
            {
                boolean permissionRequested = false;
                pb.setVisibility(View.VISIBLE);
                String aprName = etAprName.getText().toString();
                if(TextUtils.isEmpty(aprName))
                {
                    etAprName.setError("you must fill this field");
                    pb.setVisibility(View.GONE);
                    return;
                }
                if(!contacts.isEmpty())//IF YOU CHOOSE CONTACTS TO SEND SMS TO
                {
                    if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                    {
                        permissionRequested = true;
                        pb.setVisibility(View.GONE);
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
                    }
                    else
                    {
                        sendSms();
                        createApr();
                    }
                }
                if(!permissionRequested) {
                    createApr();
                }
            }));


        });
    }

    private void createApr()
    {
        dbRef.child("/"+aprKey+"/roommates").child(mAuth.getUid()).setValue(Name);
        dbRef.child("/"+aprKey+"/Manager").setValue(mAuth.getUid());
        dbRef.child("/"+aprKey+"/Name").setValue(etAprName.getText().toString());
        reference.child("Apartment_key").setValue(aprKey);
        pb.setVisibility(View.GONE);
        Toast.makeText(this,"Apartment created successfully!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(afterRegisterActivity.this, ApartmentActivity.class);
        intent.putExtra("com.example.rommies.aprKey", aprKey);
        startActivity(intent);
        finish();
        d.dismiss();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void handleAdd(View v)//check whether read contacts permission is enabled
    {
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        else
            LaunchContactPicker();
    }

    //handle the permission request results
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LaunchContactPicker();
                }
                break;
            }
            case REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms();
                    createApr();
                }
                break;
            }

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceType")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)//read the details of selected contact
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CONTACT_PICKER_RESULT:
                    Cursor phones = null, names = null;
                    String number = null, name;
                    try {
                        Uri result = data.getData();
                        Log.v(DEBUG_TAG, "Got a contact result: " + result.toString());
                        String id = result.getLastPathSegment();
                        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                        if(phones.moveToFirst())
                        {
                            name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.v(DEBUG_TAG, "Got phone number: " + number);
                            Log.v(DEBUG_TAG, "Got User name: " + name);
                            if(contacts.containsValue(number)) {
                                Toast.makeText(this, "This phone number is already on the list", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            contacts.put(name, number);

                            LinearLayout ll = d.findViewById(R.id.linearLayout);
                            RelativeLayout rl = new RelativeLayout(d.getContext());
                            //TextView 1
                            TextView tv1 = new TextView(d.getContext());
                            tv1.setText(name);
                            tv1.setTextAppearance(getApplicationContext(),R.style.TextAppearance_AppCompat_Medium);
                            tv1.setId(1);
                            //remove "button"

                            Button bt = new Button(d.getContext(),null,android.R.style.Widget_Material_Light_Button_Small);
                            bt.setBackgroundResource(R.drawable.ic_baseline_delete_24);
                            bt.setOnClickListener((v)->
                            {
                                contacts.remove(name);
                                ll.removeView(rl);
                            });
                            RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            RelativeLayout.LayoutParams bt_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            tv_lp.setMargins(40, 0,0,0);
                            bt_lp.setMargins(30,0,0,0);
                            bt_lp.addRule(RelativeLayout.RIGHT_OF, 1);
                            rl.addView(tv1,tv_lp);
                            rl.addView(bt,bt_lp);
                            ll.addView(rl, rl_lp);
                        } else {
                            Log.w(DEBUG_TAG, "No results");
                        }

                    } catch (Exception e) {
                        Log.e(DEBUG_TAG, "Failed to get phone number data", e);
                    } finally {
                        if (phones != null) {
                            phones.close();
                        }
                        if (number.length() == 0) {
                            Toast.makeText(this, "No phone number found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                break;
            }
        }
        else {
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }

    private void LaunchContactPicker()//open contacts
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    private void sendSms()//send sms to selected roommates
    {
        Log.v(DEBUG_TAG,"ENTERED sendSms");
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        ArrayList<PendingIntent> sentPI = new ArrayList<>();
        sentPI.add(PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0));
        ArrayList<PendingIntent> deliveredPI = new ArrayList<>();
        deliveredPI.add(PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0));

        for(Map.Entry<String,String> entry: contacts.entrySet())
        {
            String message = "hey " + entry.getKey() +"\n Come and join my apartment.\n press this link and start enjoying Roomies app.\n "+
                    "https://rommies.page.link/"+ aprKey;
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(entry.getValue(),null, parts,sentPI, deliveredPI );
            Log.v(DEBUG_TAG,message);
        }

    }

}
