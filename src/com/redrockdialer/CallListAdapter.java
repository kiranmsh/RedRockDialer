package com.redrockdialer;

import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts.People;
import android.text.Html;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class CallListAdapter extends ArrayAdapter<CallBean> implements View.OnTouchListener {
  private Activity context;
  private List<CallBean> list;
  private boolean mainView;

  public CallListAdapter(Activity context, List<CallBean> list, boolean mainView) {
    super(context, R.layout.call_list, list);

    this.context = context;
    this.list = list;
    this.mainView = mainView;
  }

  static class ViewHolder {
    protected TextView date;
    protected QuickContactBadge qcBadge;
    protected TextView name;
    protected TextView duration;
    protected TextView duration1;
    protected TextView type;
    protected ImageView img;
    protected TextView header;
    protected TextView leftClolor;
    protected LinearLayout layout;
  }

  @SuppressLint("NewApi")
  public View getView(final int position, View convertView, ViewGroup parent) {
    View view = null;
    LayoutInflater inflator = context.getLayoutInflater();
    view = inflator.inflate(R.layout.call_list, null);

    final ViewHolder viewHolder = new ViewHolder();
    viewHolder.date = (TextView) view.findViewById(R.id.call_list_view_date);
    viewHolder.qcBadge = (QuickContactBadge) view.findViewById(R.id.call_list_view_qcBadge);
    viewHolder.name = (TextView) view.findViewById(R.id.call_list_view_name);
    viewHolder.type = (TextView) view.findViewById(R.id.call_list_view_type);
    viewHolder.duration = (TextView) view.findViewById(R.id.call_list_view_duration);
    viewHolder.duration1 = (TextView) view.findViewById(R.id.call_list_view_duration1);
    viewHolder.layout = (LinearLayout) view.findViewById(R.id.call_list_view_ll);
    viewHolder.img = (ImageView) view.findViewById(R.id.call_list_view_img);
    viewHolder.header = (TextView) view.findViewById(R.id.call_list_view_header1);
    viewHolder.leftClolor = (TextView) view.findViewById(R.id.call_list_view_color);

    if (!list.get(position).isHeader()) {

      view.setTag(viewHolder);

      String name = list.get(position).getName();
      String duration = list.get(position).getCallDuration();
      String date = list.get(position).getDate();
      final String number = list.get(position).getNumber();
      final String type = list.get(position).getCallType();
      if (mainView) {
        viewHolder.qcBadge.setVisibility(View.VISIBLE);
        Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, new Long(list.get(position).getContactId()));
        Bitmap bitmap = People.loadContactPhoto(context, uri, R.drawable.ic_contact_list_picture, null);
        viewHolder.qcBadge.setImageBitmap(bitmap);

        if (isNull(name)) {
          viewHolder.name.setText(number);
        } else {

          viewHolder.name.setText(Html.fromHtml(name + "<br/><small>M&nbsp;:&nbsp;" + number + "</small>"));
        }
        // viewHolder.duration.setText(list.get(position).getCallDuration());

        viewHolder.type.setText(type);
        if (!isNull(duration)) {
          if (!isNull(date) && date.indexOf(",") != -1) {
            String[] date1 = date.split(",");

            // Spanned durationVal = Html.fromHtml(date1[0] + "<br/><small>" + date1[1] + "<br/>" + duration + "</small>");
            viewHolder.duration.setText(date1[0]);
            viewHolder.duration1.setText(date1[1]);
            if (isNull(type))
              viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            else if (type.equalsIgnoreCase("OC"))
              viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_call_outgoing_holo_dark), null);
            else if (type.equalsIgnoreCase("IC"))
              viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_call_incoming_holo_dark), null);
            else if (type.equalsIgnoreCase("MC"))
              viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_call_missed_holo_dark), null);

          }
        }
      } else {
        viewHolder.qcBadge.setVisibility(View.GONE);
        if (!isNull(date) && date.indexOf(",") != -1) {
          String[] date1 = date.split(",");
          viewHolder.name.setText(Html.fromHtml("M&nbsp;:&nbsp;" + number + "<br/><small>" + date1[0] + "," + date1[1] + "</small>"));
        }
        if (isNull(type))
          viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        else if (type.equalsIgnoreCase("OC"))
          viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_call_outgoing_holo_dark), null);
        else if (type.equalsIgnoreCase("IC"))
          viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_call_incoming_holo_dark), null);
        else if (type.equalsIgnoreCase("MC"))
          viewHolder.duration.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_call_missed_holo_dark), null);
        viewHolder.duration.setText(duration);
      }
      viewHolder.name.setOnLongClickListener(new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
          if (context instanceof MainCallActivity)
            ((MainCallActivity) context).onClickShowContactDetails(number);
          else
            ((DetailedCallLog) context).onClickShowContactDetails(number);
          return false;
        }
      });
      viewHolder.name.setClickable(true);

      OnTouchListener swipeListener = getOnTouchListener(list.get(position));

      // viewHolder.name.setOnTouchListener(swipeListener);
      // viewHolder.duration.setOnTouchListener(swipeListener);
      // viewHolder.img.setOnTouchListener(swipeListener);

      if (mainView) {
        viewHolder.name.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            ((MainCallActivity) context).onClickShowCallLog(number, type);
          }
        });
        viewHolder.name.setOnDragListener(getSome());
      }
    } else {
      System.out.println("I am header");
      viewHolder.leftClolor.setVisibility(View.GONE);
      viewHolder.header.setVisibility(View.VISIBLE);
      viewHolder.header.setText(list.get(position).getHeaderText());
    }
    return view;
  }

  // public Bitmap getPhoto(ContentResolver contentResolver, Long contactId) {
  // Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
  // InputStream photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, contactPhotoUri);
  // Bitmap photo = BitmapFactory.decodeStream(photoDataStream);
  // return photo;
  // }

  private OnTouchListener getOnTouchListener(final CallBean callBean) {
    OnTouchListener onThumbTouch = new OnTouchListener() {
      float previouspoint = 0;
      float startPoint = 0;

      @Override
      public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
        case R.id.call_list_view_name: {
          switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN: {
            startPoint = event.getX();
          }
            break;
          case MotionEvent.ACTION_MOVE: {
          }
            break;
          case MotionEvent.ACTION_CANCEL: {

            previouspoint = event.getX();
            System.out.println("Dragged : " + (previouspoint - startPoint));
            if ((previouspoint - startPoint) > 200) {
              // call
              context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callBean.getNumber())));

            } else if ((previouspoint - startPoint) < -150) {

              // Send sms
              context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + callBean.getNumber())));
            }
          }
            break;
          }
          break;
        }
        }
        return true;
      }
    };
    return onThumbTouch;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    // TODO Auto-generated method stub
    return false;
  }

  // @Override
  // public boolean onTouch(View v, MotionEvent event) {
  // return false;
  // }

  // class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
  //
  // private static final int SWIPE_MIN_DISTANCE = 10;
  //
  // private static final int SWIPE_MAX_OFF_PATH = 10;
  //
  // private static final int SWIPE_THRESHOLD_VELOCITY = 10;
  //
  // @Override
  // public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
  //
  // float dX = e2.getX() - e1.getX();
  // float dY = e1.getY() - e2.getY();
  // System.out.println("velocityX : " + velocityX + " velocityY : " + velocityY + " dX : " + dX + " dY : " + dY);
  // if (Math.abs(dY) < SWIPE_MAX_OFF_PATH && Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dX) >=
  // SWIPE_MIN_DISTANCE) {
  // if (dX > 0) {
  // Toast.makeText(context, "Right Swipe", Toast.LENGTH_SHORT).show();
  // } else {
  // Toast.makeText(context, "Left Swipe", Toast.LENGTH_SHORT).show();
  // }
  // return true;
  // } else if (Math.abs(dX) < SWIPE_MAX_OFF_PATH && Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dY) >=
  // SWIPE_MIN_DISTANCE) {
  //
  // if (dY > 0) {
  // Toast.makeText(context, "Up Swipe", Toast.LENGTH_SHORT).show();
  // } else {
  // Toast.makeText(context, "Down Swipe", Toast.LENGTH_SHORT).show();
  // }
  // return true;
  // }
  // return false;
  //
  // }
  // }

  // public boolean dispatchTouchEvent(MotionEvent ev) {
  // context.dispatchTouchEvent(ev);
  // return gesturedetector.onTouchEvent(ev);
  //
  // }
  //
  // class MyDragListener implements OnDragListener {
  // // Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
  // // Drawable normalShape = getResources().getDrawable(R.drawable.shape);
  //
  // @SuppressLint("NewApi")
  // @Override
  // public boolean onDrag(View v, DragEvent event) {
  // int action = event.getAction();
  // Toast.makeText(context, "Toast test" + action, Toast.LENGTH_SHORT).show();
  //
  // switch (event.getAction()) {
  // case DragEvent.ACTION_DRAG_STARTED:
  // Toast.makeText(context, "ACTION_DRAG_STARTED", Toast.LENGTH_SHORT).show();
  // break;
  // case DragEvent.ACTION_DRAG_ENTERED:
  // Toast.makeText(context, "ACTION_DRAG_ENTERED", Toast.LENGTH_SHORT).show();
  // break;
  // case DragEvent.ACTION_DRAG_EXITED:
  // Toast.makeText(context, "ACTION_DRAG_EXITED", Toast.LENGTH_SHORT).show();
  // break;
  // case DragEvent.ACTION_DROP:
  // Toast.makeText(context, "ACTION_DROP", Toast.LENGTH_SHORT).show();
  // break;
  // case DragEvent.ACTION_DRAG_ENDED:
  // Toast.makeText(context, "ACTION_DRAG_ENDED", Toast.LENGTH_SHORT).show();
  // default:
  // break;
  // }
  // return true;
  // }
  // }
  public boolean isNull(String s) {
    if ((s == null) || s.equalsIgnoreCase("null") || s.trim().length() == 0)
      return true;
    else
      return false;
  }

  @SuppressLint("NewApi")
  public OnDragListener getSome() {
    return new View.OnDragListener() {
      private static final String DROPTAG = "DropTarget";
      private int dropCount = 0;
      private ObjectAnimator anim;

      public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        boolean result = true;
        switch (action) {
        case DragEvent.ACTION_DRAG_STARTED:
          System.out.println(">>>>>>>>>>>>>>>>@@@@@@@@@@@@@@@");
          break;
        case DragEvent.ACTION_DRAG_ENTERED:
          anim = ObjectAnimator.ofFloat((Object) v, "alpha", 1f, 0.5f);
          anim.setInterpolator(new CycleInterpolator(40));
          anim.setDuration(30 * 1000);
          anim.start();
          break;
        case DragEvent.ACTION_DRAG_EXITED:
          if (anim != null) {
            anim.end();
            anim = null;
          }
          break;
        case DragEvent.ACTION_DRAG_LOCATION:
          System.out.println("drag proceeding in dropTarget: " + event.getX() + ", " + event.getY());
          break;
        case DragEvent.ACTION_DROP:
          System.out.println("drag drop in dropTarget");

          if (anim != null) {
            anim.end();
            anim = null;
          }
          ClipData data = event.getClipData();
          System.out.println("Item data is " + data.getItemAt(0).getText());
          dropCount++;
          String message = dropCount + " drop";
          if (dropCount > 1)
            message += "s";
          break;
        case DragEvent.ACTION_DRAG_ENDED:
          System.out.println("drag ended in dropTarget");
          if (anim != null) {
            anim.end();
            anim = null;
          }
          break;
        default:
          System.out.println("other action in dropzone: " + action);
          result = false;
        }
        return result;
      }
    };
  }
}