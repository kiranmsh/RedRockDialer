package com.redrockdialer;
//test my kiran
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class DialPad extends Activity {

  Button button00, button01, button02, button03, button04, button05, button06, button07, button08, button09, buttonDel, buttonPound, buttonStar;
  TextView dialNumber;
  // Spinner phoneSelect;
  Button CallButton;
  String selectedPhone = "";
  String[] PHONES;
  ImageButton ContactLookup;

  // ArrayAdapter<String> adapter;
  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    this.setContentView(R.layout.dial_pad);

    // selectedPhone = "";

    ContactLookup = (ImageButton) this.findViewById(R.id.Contacts);

    button01 = (Button) this.findViewById(R.id.Button01);
    button01.setText(Html.fromHtml("1&nbsp;&nbsp;&nbsp;<small><small><small>&nbsp;&nbsp;&nbsp;</small></small></small>"));

    button02 = (Button) this.findViewById(R.id.Button02);
    button02.setText(Html.fromHtml("2&nbsp;<small><small><small>ABC</small></small></small>"));
    button03 = (Button) this.findViewById(R.id.Button03);
    button03.setText(Html.fromHtml("3&nbsp;<small><small><small>DEF</small></small></small>"));

    button04 = (Button) this.findViewById(R.id.Button04);
    button04.setText(Html.fromHtml("4&nbsp;<small><small><small>GHI</small></small></small>"));

    button05 = (Button) this.findViewById(R.id.Button05);
    button05.setText(Html.fromHtml("5&nbsp;<small><small><small>JKL</small></small></small>"));

    button06 = (Button) this.findViewById(R.id.Button06);
    button06.setText(Html.fromHtml("6&nbsp;<small><small><small>MNO</small></small></small>"));

    button07 = (Button) this.findViewById(R.id.Button07);
    button07.setText(Html.fromHtml("7&nbsp;<small><small><small>PQRS</small></small></small>"));

    button08 = (Button) this.findViewById(R.id.Button08);
    button08.setText(Html.fromHtml("8&nbsp;<small><small><small>TUV</small></small></small>"));

    button09 = (Button) this.findViewById(R.id.Button09);
    button09.setText(Html.fromHtml("9&nbsp;<small><small><small>WXYZ</small></small></small>"));

    button00 = (Button) this.findViewById(R.id.Button00);
    button00.setText(Html.fromHtml("0&nbsp;<small><small><small>+</small></small></small>"));

    buttonDel = (Button) this.findViewById(R.id.ButtonDel);
    buttonPound = (Button) this.findViewById(R.id.ButtonPound);
    buttonStar = (Button) this.findViewById(R.id.ButtonStar);

    CallButton = (Button) this.findViewById(R.id.CALLButton);

    // phoneSelect = (Spinner)this.findViewById(R.id.phoneSelectSpinner);

    dialNumber = (TextView) this.findViewById(R.id.Number);
    // dialNumber.setInputType(InputType.TYPE_CLASS_PHONE);
    dialNumber.setText("", BufferType.EDITABLE);

    // adapter = ArrayAdapter.createFromResource(this, R.array.phonearray,android.R.layout.simple_spinner_item);
    // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // adapter.clear();
    // phoneSelect.setAdapter(adapter);
    // getPhones();

    // phoneSelect.setOnItemSelectedListener(select);

    button00.setOnLongClickListener(longClick0);
    button01.setOnClickListener(tapNumber);
    button02.setOnClickListener(tapNumber);
    button03.setOnClickListener(tapNumber);
    button04.setOnClickListener(tapNumber);
    button05.setOnClickListener(tapNumber);
    button06.setOnClickListener(tapNumber);
    button07.setOnClickListener(tapNumber);
    button08.setOnClickListener(tapNumber);
    button09.setOnClickListener(tapNumber);
    button00.setOnClickListener(tapNumber);
    buttonDel.setOnClickListener(tapNumber);
    buttonPound.setOnClickListener(tapNumber);
    buttonStar.setOnClickListener(tapNumber);
    CallButton.setOnClickListener(tapNumber);

    ContactLookup.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        getNumber();
      }

    });
  }

  OnLongClickListener longClick0 = new OnLongClickListener() {

    @Override
    public boolean onLongClick(View arg0) {
      addText('+');
      return true;
    }

  };

  OnItemSelectedListener select = new OnItemSelectedListener() {

    @SuppressWarnings("unchecked")
    @Override
    public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {
      System.out.println(arg0);
      System.out.println("Phone index " + arg2);
      // System.out.println()
      selectedPhone = PHONES[arg2];
      System.out.println(selectedPhone);
    }

    @Override
    public void onNothingSelected(AdapterView arg0) {

    }

  };

  OnClickListener tapNumber = new OnClickListener() {

    @Override
    public void onClick(View arg0) {
      if (arg0.getId() == CallButton.getId()) {
        Log.i("RRCALL", "CALL SELECTED");
        if (selectedPhone.equals(""))
          Log.i("RRCALL", "blank selectedPhone");
      }
      // System.out.println(arg0.getId());
      if (arg0.getId() == button00.getId()) {
        addText('0');
      } else if (arg0.getId() == button01.getId()) {
        addText('1');
      } else if (arg0.getId() == button02.getId()) {
        addText('2');
      } else if (arg0.getId() == button03.getId()) {
        addText('3');
      } else if (arg0.getId() == button04.getId()) {
        addText('4');
      } else if (arg0.getId() == button05.getId()) {
        addText('5');
      } else if (arg0.getId() == button06.getId()) {
        addText('6');
      } else if (arg0.getId() == button07.getId()) {
        addText('7');
      } else if (arg0.getId() == button08.getId()) {
        addText('8');
      } else if (arg0.getId() == button09.getId()) {
        addText('9');
      } else if (arg0.getId() == buttonDel.getId()) {
        delText();
      } else if (arg0.getId() == buttonPound.getId()) {
        addText('#');
      } else if (arg0.getId() == buttonStar.getId()) {
        addText('*');
      } else if (arg0.getId() == CallButton.getId()) {

        String num = getNumberForDial();
        if (!isNull(num)) {
          Toast.makeText(getApplicationContext(), "Calling......" + num, Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(getApplicationContext(), "Invalid Phone number....", Toast.LENGTH_SHORT).show();
        }

      }
    }

  };

  void delText() {
    CharSequence start = dialNumber.getText();
    String ret = start.toString();
    int len = ret.length();
    if (len > 0) {
      ret = ret.substring(0, len - 1);
    }
    // ret.f
    dialNumber.setText(ret, BufferType.EDITABLE);
    // dialNumber
    dialNumber.setInputType(InputType.TYPE_CLASS_PHONE);
  }

  void addText(char newChar) {
    CharSequence start = dialNumber.getText();
    String ret = start.toString();
    ret += newChar;
    dialNumber.setText(ret, BufferType.EDITABLE);
    // dialNumber.setInputType(InputType.TYPE_CLASS_PHONE);
  }

  void getPhones() {
    // phoneSelect.removeAllViewsInLayout();
    // if(!adapter.isEmpty())
    // adapter.clear();

    SQLiteDatabase sql = openOrCreateDatabase("db", 0, null);

    PHONES = getPhoneArray("SELECT * FROM PHONE;", sql);

    sql.close();
    System.out.println("Phone Numbers with current search : ");
    for (String s : PHONES) {
      System.out.println(s);
    }
    System.out.println("Over...");

    // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,PHONES);

    // phoneSelect.setAdapter(adapter);
    /*
     * if(PHONES!=null)if(PHONES.length>0){
     * 
     * for(int i=0;i<PHONES.length;i++){ System.out.println(PHONES[i]); adapter.add((CharSequence)PHONES[i]); } }
     */

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 0 || resultCode == 0) {
      // String res = data.getStringExtra(name) data.getExtras();
    }
  }

  public String getNumber() {
    // lookup contact number
    startActivityForResult(new Intent(Intent.ACTION_PICK, People.CONTENT_URI), 0);

    Intent intent = new Intent(Intent.ACTION_SEARCH, People.CONTENT_URI);
    // Contacts.
    // Uri lookupUri = Uri.withAppendedPath(Contacts.CONTENT_URI, Contacts.Phones.NUMBER);
    // Cursor c = getContentResolver().query(lookupUri, new String[]{Contacts.DISPLAY_NAME}, ...);
    try {
      // c.moveToFirst();
      // String displayName = c.getString(0);
    } finally {
      // c.close();
    }
    return selectedPhone;
  }

  private String[] getPhoneArray(String query, SQLiteDatabase sql) {
    String[] ret = new String[0];
    try {
      Cursor c = sql.rawQuery(query, null);
      List<String> list = new ArrayList<String>();
      if (c != null) {
        while (c.moveToNext()) {
          list.add(c.getString(0));
          Log.i("RRCALL", c.getString(0));

        }
        if (list.size() > 0) {
          ret = new String[list.size() + 1];
          ret[0] = "";
          for (int i = 0; i < list.size(); i++) {
            ret[i + 1] = list.get(i);
          }
        }
        c.close();
      }

      // c.getString(0);

    } catch (Exception o) {

      o.printStackTrace();
    }
    return ret;
  }

  public String getNumberForDial() {
    CharSequence num = dialNumber.getText();
    if (num != "" && num != "Dial:") {
      return String.valueOf(num);
    } else {
      return null;
    }
  }

  public boolean isNull(String s) {
    if ((s == null) || s.equalsIgnoreCase("null") || s.trim().length() == 0)
      return true;
    else
      return false;
  }
}