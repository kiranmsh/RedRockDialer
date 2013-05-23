package com.redrockdialer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class DetailedCallLog extends Activity {
  private ListView callListView = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.detailed_call_log);
    callListView = (ListView) findViewById(R.id.detail_call_list_view);

    @SuppressWarnings("unchecked")
    ArrayList<CallBean> list = (ArrayList<CallBean>) getIntent().getExtras().getSerializable("detailedCallList");
    Collections.sort(list, new Comparator<CallBean>() {
      @Override
      public int compare(CallBean a, CallBean b) {
        return b.getDateforSort().compareTo(a.getDateforSort());
      }
    });
    TextView header = (TextView) findViewById(R.id.call_list_view_header);
    header.setVisibility(View.VISIBLE);
    header.setText(list.get(0).getNumber());
    if (list.get(0).getName() != null && list.get(0).getName().length() > 0) {
      header.setText(list.get(0).getName());
    }
    CallListAdapter adapter = new CallListAdapter(this, list, false);
    callListView.setAdapter(adapter);
  }

  public void onClickShowContactDetails(String number) {
    Intent intent = new Intent(this, ContactDetailsActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString("phoneNumber", number);
    intent.putExtras(bundle);
    startActivity(intent);
  }
}
