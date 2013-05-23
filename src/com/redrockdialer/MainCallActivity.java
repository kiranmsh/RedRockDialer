package com.redrockdialer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainCallActivity extends Activity {
  private DecimalFormat dec = new DecimalFormat("00");
  private HashMap<String, ArrayList<CallBean>> callHistoryMap = new HashMap<String, ArrayList<CallBean>>();
  private HashMap<String, CallBean> callDetailsMap = new HashMap<String, CallBean>();
  // private static HashMap<String, CallBean> callRefinedMap = new HashMap<String, CallBean>();
  private ListView callListView = null;
  private Button button00, button01, button02, button03, button04, button05, button06, button07, button08, button09, buttonPound, buttonStar;
  private Context context;
  private TableLayout keypad;
  private TextView dialNumber;
  private ImageView buttonDel, callButton;
  private static boolean isKeyPadOn;
  private String[] searchChars = new String[] {};

  // private ArrayList<String> testList = new ArrayList<String>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    context = this;
    callListView = (ListView) findViewById(R.id.call_list_view);
    keypad = (TableLayout) findViewById(R.id.dial_pad_nums);
    dialNumber = (TextView) this.findViewById(R.id.Number);
    callButton = (ImageView) this.findViewById(R.id.CALLButton);
    buttonDel = (ImageView) this.findViewById(R.id.ButtonDel);
    searchChars = new String[] {};
    setDialPad();
    dialNumber.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (isKeyPadOn) {
          turnOffKeyPad();
        } else {
          turnOnKeyPad();
        }
      }
    });
    loadCallData(null);

  }

  private void loadCallData(String phoneNumber) {
    if (isNull(phoneNumber)) {
      phoneNumber = "";
    }
    ArrayList<CallBean> displayList = getCallLogByPhoneNumber(null);
    System.out.println("Call Log size.............." + displayList.size());
    ArrayList<CallBean> contactsList = getAllContacts();
    System.out.println("Contacts size.............." + contactsList.size());

    ArrayList<CallBean> list = getSortedUniqueList(contactsList, displayList);

    System.out.println("Loading new Data..............");
    CallListAdapter adapter = new CallListAdapter(this, list, true);
    callListView.setAdapter(adapter);

  }

  private ArrayList<CallBean> getSortedUniqueList(ArrayList<CallBean> contactsList, ArrayList<CallBean> displayList) {
    Map<String, CallBean> uniqueMap = new LinkedHashMap<String, CallBean>();
    Collections.sort(contactsList, new Comparator<CallBean>() {
      @Override
      public int compare(CallBean a, CallBean b) {
        if (!isNull(b.getName()) && !isNull(a.getName()))
          return a.getName().compareTo(b.getName());
        else
          return a.getNumber().compareTo(b.getNumber());
      }
    });
    Collections.sort(displayList, new Comparator<CallBean>() {
      @Override
      public int compare(CallBean a, CallBean b) {
        if (b.getDateforSort() != null && a.getDateforSort() != null)
          return b.getDateforSort().compareTo(a.getDateforSort());
        else
          return 0;
      }
    });
    if (displayList.size() > 0) {
      CallBean bean = new CallBean();
      bean.setHeader(true);
      bean.setHeaderText("Call Log");
      uniqueMap.put("header", bean);
    }
    for (CallBean bean : displayList) {
      String key = isNull(bean.getName()) ? "" : bean.getName() + bean.getNumber();
      if (!uniqueMap.containsKey(key))
        uniqueMap.put(key, bean);
    }
    if (contactsList.size() > 0) {
      CallBean bean = new CallBean();
      bean.setHeader(true);
      bean.setHeaderText("Contacts");
      uniqueMap.put("header1", bean);
    }
    for (CallBean bean : contactsList) {
      String key = isNull(bean.getName()) ? "" : bean.getName() + bean.getNumber();
      if (!uniqueMap.containsKey(key))
        uniqueMap.put(key, bean);
      else {
        bean.setCallDuration(uniqueMap.get(key).getCallDuration());
        bean.setCallType(uniqueMap.get(key).getCallType());
        bean.setDate(uniqueMap.get(key).getDate());
        bean.setDateforSort(uniqueMap.get(key).getDateforSort());
        uniqueMap.put(key, bean);
      }
    }
    displayList = new ArrayList<CallBean>(uniqueMap.values());
    for (CallBean bean : displayList) {
      String key = isNull(bean.getName()) ? "" : bean.getName() + bean.getNumber();
      callDetailsMap.put(key, bean);
    }
    return displayList;
  }

  private ArrayList<CallBean> getAllContacts() {
    ArrayList<CallBean> list = new ArrayList<CallBean>();

    Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    while (cursor.moveToNext()) {

      CallBean bean = new CallBean();
      String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

      String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
      if (!isNull(hasPhone) && hasPhone.equalsIgnoreCase("1")) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
            null, null);
        while (phones.moveToNext()) {
          String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          if (!isNull(phoneNumber))
            phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
          bean.setNumber(phoneNumber);
          bean.setName(name);
        }
        phones.close();
      }

      bean.setContactId(contactId);
      bean.setFromCallLog(false);
      list.add(bean);
    }
    return list;
  }

  private ArrayList<CallBean> getCallLogByPhoneNumber(String number) {
    ArrayList<CallBean> callList = new ArrayList<CallBean>();
    Map<String, CallBean> callMap = new HashMap<String, CallBean>();
    System.out.println("Number in query : " + number);
    final String[] projection = null;
    String selection = null;
    String[] selectionArgs = null;
    if (!isNull(number)) {
      selection = android.provider.CallLog.Calls.NUMBER + " = ?";
      selectionArgs = new String[1];
      selectionArgs[0] = "%" + number + "%";
    }
    final String sortOrder = android.provider.CallLog.Calls.DATE + " ASC";
    Cursor cursor = null;
    try {
      cursor = getContentResolver().query(Uri.parse("content://call_log/calls"), projection, selection, selectionArgs, sortOrder);
      while (cursor.moveToNext()) {

        int contactId = cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls._ID));
        long dateTimeMillis = cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
        long duration = cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION));
        int callType = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
        String name = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
        String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));

        SimpleDateFormat datePattern = new SimpleDateFormat("yy-MM-dd");
        datePattern.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar callDate = Calendar.getInstance();
        callDate.setTime(new Date(dateTimeMillis));

        Calendar today = Calendar.getInstance(); // today

        String date_str = datePattern.format(callDate.getTime());
        if (callDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && callDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
          if (callDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            date_str = "Today";
          } else if (today.get(Calendar.DAY_OF_MONTH) - callDate.get(Calendar.DAY_OF_MONTH) == 1) {
            date_str = "Yesterday";
          }
        }

        date_str += "," + getTime(callDate);
        if (!isNull(callNumber)) {
          callNumber = callNumber.replaceAll("[^\\d]", "");
        }
        CallBean bean = new CallBean();
        bean.setName(name);

        int type = cursor.getInt(callType);

        switch (type) {
        case android.provider.CallLog.Calls.INCOMING_TYPE:
          bean.setCallType("IC");
          break;
        case android.provider.CallLog.Calls.MISSED_TYPE:
          bean.setCallType("MC");
          break;
        case android.provider.CallLog.Calls.OUTGOING_TYPE:
          bean.setCallType("OC");
          break;
        default:
          bean.setCallType("");
          break;
        }

        bean.setCallDuration(getCallDuration(duration));
        bean.setDate(date_str);
        bean.setNumber(callNumber);
        bean.setContactId(contactId + "");
        bean.setDateforSort(callDate.getTime());
        bean.setFromCallLog(true);

        String key = callNumber;
        callMap.put(key, bean);
        System.out.println(name + ">>" + callNumber + " >> " + bean.getCallType());
        ArrayList<CallBean> list = callHistoryMap.get(key);
        if (list == null) {
          list = new ArrayList<CallBean>();
        }
        list.add(bean);
        callHistoryMap.put(key, list);

      }
    } catch (Exception ex) {

      System.out.println("ERROR: " + ex.toString());
    } finally {
      cursor.close();
    }

    callList = new ArrayList<CallBean>(callMap.values());
    return callList;
  }

  private String getTime(Calendar calendar) {
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int min = calendar.get(Calendar.MINUTE);
    String ampm = "AM";
    if (hour > 12) {
      hour -= 12;
      ampm = "PM";
    }
    return dec.format(hour) + ":" + dec.format(min) + ":" + ampm;
  }

  private String getCallDuration(long totalSecs) {
    // int hours = (int) (totalSecs / 3600);
    int minutes = (int) ((totalSecs % 3600) / 60);
    int seconds = (int) (totalSecs % 60);
    return dec.format(minutes) + ":" + dec.format(seconds);
  }

  public void onClickShowContactDetails(String number) {
    System.out.println("number : " + number);
    Intent intent = new Intent(this, ContactDetailsActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString("phoneNumber", number);
    intent.putExtras(bundle);
    startActivity(intent);
  }

  public void onClickShowCallLog(String number, String type) {
    turnOffKeyPad();
    String key = number;
    if (callHistoryMap.containsKey(key)) {
      Bundle bundle = new Bundle();
      Intent intent = new Intent(this, DetailedCallLog.class);
      bundle.putSerializable("detailedCallList", callHistoryMap.get(key));
      intent.putExtras(bundle);
      startActivity(intent);

    }
  }

  public void onClickMakeCall(View view) {
    Intent intent = new Intent(this, DialPad.class);
    startActivity(intent);
  }

  public boolean isNull(String s) {
    if ((s == null) || s.equalsIgnoreCase("null") || s.trim().length() == 0)
      return true;
    else
      return false;
  }

  private void setDialPad() {
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

    buttonPound = (Button) this.findViewById(R.id.ButtonPound);
    buttonStar = (Button) this.findViewById(R.id.ButtonStar);

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
    callButton.setOnClickListener(tapNumber);
    turnOffKeyPad();
  }

  OnClickListener tapNumber = new OnClickListener() {

    @Override
    public void onClick(View arg0) {
      // System.out.println(arg0.getId());
      if (arg0.getId() == button00.getId()) {
        addText('0');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button01.getId()) {
        addText('1');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button02.getId()) {
        addText('2');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button03.getId()) {
        addText('3');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button04.getId()) {
        addText('4');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button05.getId()) {
        addText('5');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button06.getId()) {
        addText('6');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button07.getId()) {
        addText('7');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button08.getId()) {
        addText('8');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == button09.getId()) {
        addText('9');
        // refineData(getNumberForDial(), false);
      } else if (arg0.getId() == buttonDel.getId()) {
        if (!isKeyPadOn) {
          turnOnKeyPad();
        } else {
          // if (!isNull(getNumberForDial()) && getNumberForDial().length() > 1)
          // refineData(getNumberForDial(), true);
          delText();
        }
      } else if (arg0.getId() == buttonPound.getId()) {
        addText('#');
      } else if (arg0.getId() == buttonStar.getId()) {
        addText('*');
      } else if (arg0.getId() == callButton.getId()) {
        if (!isKeyPadOn) {
          turnOnKeyPad();
        } else {
          String num = getNumberForDial();
          if (!isNull(num)) {
            // Toast.makeText(getApplicationContext(), "Calling......" + num, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num)));
          } else {
            Toast.makeText(getApplicationContext(), "Invalid Phone number....", Toast.LENGTH_SHORT).show();
          }

        }
      }
    }

  };

  private void turnOnKeyPad() {
    keypad.setVisibility(View.VISIBLE);
    RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) callListView.getLayoutParams();
    params.height = 350;
    callListView.setLayoutParams(params);
    isKeyPadOn = true;
  }

  protected void refineData(String numberForDial, boolean isBackSpace) {

    LinkedHashMap<String, CallBean> newCallRefinedMap = new LinkedHashMap<String, CallBean>();
    List<CallBean> displayList;
    if (callDetailsMap.size() > 0 && !isNull(numberForDial)) {

      updateSearchPattern(numberForDial, isBackSpace);
      HashMap<String, CallBean> searchMap = new HashMap<String, CallBean>(callDetailsMap);

      for (String key : callDetailsMap.keySet()) {
        for (String c : searchChars) {
          if (Pattern.compile(Pattern.quote(c), Pattern.CASE_INSENSITIVE).matcher(key).find()) {
            String key1 = isNull(searchMap.get(key).getName()) ? "" : searchMap.get(key).getName() + searchMap.get(key).getNumber();
            newCallRefinedMap.put(key1, searchMap.get(key));
          }
        }
      }

      displayList = new ArrayList<CallBean>(newCallRefinedMap.values());

    } else {
      searchChars = new String[] {};
      displayList = new ArrayList<CallBean>(callDetailsMap.values());
    }
    CallListAdapter adapter = new CallListAdapter(this, displayList, true);
    callListView.setAdapter(adapter);
  }

  private void updateSearchPattern(String numberForDial, boolean isBackSpace) {

    String searchChar = getSubSearchString(numberForDial.charAt(numberForDial.length() - 1));
    String[] subChars = searchChar.split(",");
    String newSearchCharsStr = "";
    if (searchChars.length > 0) {
      for (String s1 : searchChars) {
        for (String s2 : subChars) {
          String newCharPattern = "";
          if (!isBackSpace) {
            newCharPattern = s1 + s2;
          } else {

            int index = s1.lastIndexOf(s2);
            if (index != -1) {
              newCharPattern = s1.substring(0, index);
            }
          }
          if (!newSearchCharsStr.contains(newCharPattern))
            newSearchCharsStr += "," + newCharPattern;
        }
      }
      if (newSearchCharsStr.length() > 2 && newSearchCharsStr.contains(","))
        searchChars = newSearchCharsStr.substring(1).split(",");
    } else {
      searchChars = subChars;
    }
    System.out.println("newSearchCharsStr " + newSearchCharsStr);
  }

  private String getSubSearchString(char num) {
    switch (num) {
    case '0':
      return "0,";
    case '1':
      return "1,";
    case '2':
      return "2,A,B,C";
    case '3':
      return "3,D,E,F";
    case '4':
      return "4,G,H,I";
    case '5':
      return "5,J,K,L";
    case '6':
      return "6,M,N,O,";
    case '7':
      return "7,P,Q,R,S";
    case '8':
      return "8,T,U,V";
    case '9':
      return "9,W,X,Y,Z";
    default:
      return "";
    }

  }

  private void turnOffKeyPad() {
    keypad.setVisibility(View.GONE);
    RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) callListView.getLayoutParams();
    params.height = 650;
    callListView.setLayoutParams(params);
    isKeyPadOn = false;
  }

  private void delText() {
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

  private void addText(char newChar) {
    CharSequence start = dialNumber.getText();
    String ret = start.toString();
    ret += newChar;
    dialNumber.setText(ret, BufferType.EDITABLE);
  }

  private String getNumberForDial() {
    CharSequence num = dialNumber.getText();
    if (num != "" && num != "Dial:") {
      return String.valueOf(num);
    } else {
      return null;
    }
  }

  OnLongClickListener longClick0 = new OnLongClickListener() {

    @Override
    public boolean onLongClick(View arg0) {
      addText('+');
      return true;
    }

  };
}
