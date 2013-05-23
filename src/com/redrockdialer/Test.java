//package com.redrockdialer;
//
//import java.io.InputStream;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//
//import android.content.ContentUris;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.provider.Contacts;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.QuickContactBadge;
//import android.widget.TextView;
//
//import com.redrockdialer.CallListAdapter.ViewHolder;
//
//public class MyArrayListAdapter extends ArrayAdapter<ContactsBook> implements OnItemClickListener{
//  private ArrayList<ContactsBook> mContacts;
//  private Context mContext;
//
//
//  public MyArrayListAdapter(Context context, int textViewResourceId, ArrayList<ContactsBook> contacts) {
//      super(context, textViewResourceId, contacts);
//      mContacts= contacts;
//      mContext=context;
//  }
//  @Override
//  public View getView(int position, View converview, ViewGroup parent){
//      View view=converview;
//      ViewHolder viewHolder=new ViewHolder();
//      if(view==null){
////          LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//          view=inflater.inflate(R.layout.phone_row, null);
//          viewHolder.tvName=(TextView)view.findViewById(R.id.tvContact);
//          viewHolder.tvPhoneNo=(TextView)view.findViewById(R.id.tvPhoneNo);
//          viewHolder.qcBadge=(QuickContactBadge)view.findViewById(R.id.qContact);
//          view.setTag(viewHolder);
//      }
//      else
//          viewHolder=(ViewHolder) view.getTag();
//      ContactsBook cb=mContacts.get(position);
//      if(cb!=null){
//          Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,cb.getContactIndex());
//          BitmapDownloaderTask bdTask=new BitmapDownloaderTask(viewHolder.qcBadge);
//          bdTask.execute(contactPhotoUri.toString());
//          viewHolder.qcBadge.assignContactUri(contactPhotoUri);
//          viewHolder.qcBadge.setImageBitmap(framePhoto(BitmapFactory.decodeResource(getResources(), R.drawable.ic_contact_list_picture)));
//          viewHolder.tvName.setText(getContactDisplayName(cb.getContactIndex()));
//          viewHolder.tvPhoneNo.setText(getContactPhoneNo(cb.getContactIndex()));
//      }
//      return view;
//
//  }
//  class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
//      private String url;
//      private final WeakReference<ImageView> imageViewReference;
//
//
//      public BitmapDownloaderTask(ImageView imageView) {
//          imageViewReference = new WeakReference<ImageView>(imageView);
//      }
//
//      /**
//       * Actual download method.
//       */
//      @Override
//      protected Bitmap doInBackground(String... params) {
//          url = params[0];
//          Uri conUri=Uri.parse(url);
//          InputStream photoInputStream=Contacts.openContactPhotoInputStream(mContext.getContentResolver(), conUri);
//          if(photoInputStream==null)
//              return framePhoto(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_contact_list_picture));
//          Bitmap photo=framePhoto(getPhoto(mContext.getContentResolver(), conUri));
//
//          return photo;
//      }
//  /**
//       * Once the image is downloaded, associates it to the imageView
//       */
//      @Override
//      protected void onPostExecute(Bitmap bitmap) {
//          if (isCancelled()) {
//              bitmap = null;
//          }
//          if (imageViewReference != null) {
//              ImageView imageView = imageViewReference.get();
//              imageView.setImageBitmap(bitmap);
//          }
//      }
//  }
//  @Override
//  public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//    // TODO Auto-generated method stub
//    
//  }