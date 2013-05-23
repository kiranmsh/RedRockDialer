package com.redrockdialer;

import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactDetailsActivity extends Activity {
  private TextView nameTv, numTv, emailTv;
  String contactId = null;
  private LinearLayout mainLl;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_details);
    nameTv = (TextView) findViewById(R.id.contact_name_val);
    // numTv = (TextView) findViewById(R.id.contact_num_val);
    mainLl = (LinearLayout) findViewById(R.id.contact_main_ll);

    String phoneNumber = getIntent().getExtras().getString("phoneNumber");
    uploadContactPhoto(phoneNumber);
    if (contactId != null) {
      retrieveContactNumber();
      retriveEmail(contactId);
    }
  }

  private void uploadContactPhoto(String number) {

    String name = null;
    InputStream input = null;
    String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER,
        ContactsContract.PhoneLookup.TYPE };
    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

    if (cursor.moveToFirst()) {
      do {
        contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
        name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        // String num = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER));
        // String type = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.TYPE));

        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);

        nameTv.setText(name);
        // numTv.setText(num);
      } while (cursor.moveToNext());
    } else {
      System.out.println("Started uploadcontactphoto: Contact Not Found @ " + number);
      return; // contact not found

    }

    // Only continue if we found a valid contact photo:
    if (input == null) {
      System.out.println("Started uploadcontactphoto: No photo found, id = " + contactId + " name = " + name);
      return; // no photo
    } else {
      System.out.println("Started uploadcontactphoto: Photo found, id = " + contactId + " name = " + name);
    }
  }

  private void retriveEmail(String contactId) {
    Cursor cursor = null;
    try {
      ArrayList<String> arrEmail = new ArrayList<String>();
      cursor = getContentResolver().query(Email.CONTENT_URI, null, Email.CONTACT_ID + "=?", new String[] { contactId }, null);

      int emailIdx = cursor.getColumnIndex(Email.DATA);
      int emailType = cursor.getColumnIndex(Email.TYPE);
      if (cursor.moveToFirst()) {
        do {
          String email = cursor.getString(emailIdx);
          String emailTypeStr = cursor.getString(emailType);
          createNewRow("Email (" + getEmailType(emailTypeStr) + ") : ", email, "email");
          arrEmail.add(email);
        } while (cursor.moveToNext());
      } else {
        System.out.println("No results");
      }

    } catch (Exception e) {
      System.out.println("Failed to get email data");
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  @SuppressLint("NewApi")
  private void createNewRow(String header, final String value, String type) {
    System.out.println("Creating new Email...");
    LinearLayout linearLayout = new LinearLayout(this);
    int id = (int) (Math.random() * 5000);
    linearLayout.setId(id);
    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    llp.topMargin = 10;
    llp.bottomMargin = 10;
    linearLayout.setLayoutParams(llp);
    linearLayout.setBackground(getResources().getDrawable(R.drawable.rectangle));
    linearLayout.setPadding(10, 10, 10, 10);

    // Creating a new TextView
    final TextView tv = new TextView(this);
    llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    // llp.weight = (float) 0.70;
    tv.setLayoutParams(llp);
    tv.setTextColor(Color.WHITE);
    tv.setText(header);
    tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

    // Creating a new TextView
    final TextView tv1 = new TextView(this);
    llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    // llp.weight = (float) 0.30;
    tv1.setLayoutParams(llp);
    tv1.setTextColor(Color.WHITE);
    tv1.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
    tv1.setText(value);

    linearLayout.addView(tv);
    linearLayout.addView(tv1);
    linearLayout.setVisibility(View.VISIBLE);
    if (type.equalsIgnoreCase("Phone")) {

      linearLayout.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          System.out.println("Clicked");
          startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + value)));
        }

      });

      linearLayout.setOnLongClickListener(new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + value)));
          return false;
        }
      });
    }
    mainLl.addView(linearLayout);
  }

  private String getEmailType(String emailTypeStr) {
    System.out.println("emailTypeStr..." + emailTypeStr);
    int type = Integer.parseInt(emailTypeStr);
    if (type == 1) {
      return "Home";
    } else if (type == 2) {
      return "Work";
    } else if (type == 3) {
      return "Other";
    } else {
      return "Mobile";
    }
  }

  private void retrieveContactNumber() {

    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
        new String[] { contactId }, null);
    while (phones.moveToNext()) {
      // String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
      String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
      String phoneNumberType = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

      switch (Integer.parseInt(phoneNumberType)) {
      case Phone.TYPE_HOME:
        createNewRow("Phone (Home) :", phoneNumber, "Phone");
        break;
      case Phone.TYPE_MOBILE:
        createNewRow("Phone (Mobile) :", phoneNumber, "Phone");
        break;
      case Phone.TYPE_WORK:
        createNewRow("Phone (Work) :", phoneNumber, "Phone");
        break;
      default:
        createNewRow("Phone (Other) :", phoneNumber, "Phone");

      }

    }
    phones.close();
  }

}
