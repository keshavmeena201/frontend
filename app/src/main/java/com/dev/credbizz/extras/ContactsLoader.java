package com.dev.credbizz.extras;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.OperationCanceledException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.dev.credbizz.dbHelper.LoadTables;
import com.dev.credbizz.models.ContactModel;

import java.util.ArrayList;

public class ContactsLoader extends AsyncTaskLoader<ArrayList<ContactModel>> {

    private volatile Thread thread;

    // ARRAY LIST
    ArrayList<ContactModel> contactModels = new ArrayList<>();

    // CONTEXT
    Context context;

    public ContactsLoader(@NonNull Context cxt) {
        super(cxt);
        this.context = cxt;
    }

    @Nullable
    @Override
    public ArrayList<ContactModel> loadInBackground() {
        try {
            thread = Thread.currentThread();
            return onLoadInBackground();
        } finally {
            Thread.interrupted();
            thread = null;
        }
    }

    @Nullable
    @Override
    protected ArrayList<ContactModel> onLoadInBackground() {
        LoadTables lt = new LoadTables(context);
        contactModels = new ArrayList<>();
        ContactModel contactModel;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0){
                        try {
                            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String isPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                            String newName = name.substring(0, 1).toUpperCase() + name.substring(1);
                            String alphaName =  name.substring(0, 1).toUpperCase();



                            contactModel = new ContactModel();
                            contactModel.setContactName(newName);
                            contactModel.setContactId(id);
                            contactModel.setContactNameAlpha(alphaName);

                            if (cursor.getCount() % 2 == 0){
                                contactModel.setCreditBuzzUser(true);
                                contactModel.setContactCreditScore("450");
                            } else {
                                contactModel.setCreditBuzzUser(false);
                                contactModel.setContactCreditScore("");
                            }
                            contactModel.setContactSelected(false);
                            contactModel.setTransactionType(0);
                            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                            if (phoneCursor != null && phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                contactModel.setContactNumber(phoneNumber.trim());
                            }

                            if (phoneCursor != null) {
                                phoneCursor.close();
                            }

                            contactModels.add(contactModel);
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }

            }
        }
        return contactModels;
    }

    @Override
    public void deliverResult(@Nullable ArrayList<ContactModel> data) {
        contactModels = data;
        //if (isStarted()) {
            super.deliverResult(data);
        //}
    }

    @Override
    protected void onStartLoading() {
        //if (takeContentChanged() || contactModels == null) {
            forceLoad();
       // } else {
          //  deliverResult(contactModels);
      //  }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
