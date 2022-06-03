package com.example.user.contactlist.model;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContactRepository {

    private Context context;
    private List<Contact> contacts;

    private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

    @SuppressLint("InlinedApi")
    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    public ContactRepository(Context context) {
        this.context = context;
        contacts = new ArrayList<>();
    }




    public List<Contact> fetchContacts() {
        //Contact contact;
        //hold a list of Contacts without duplicates
        Map<String, Contact> cleanList = new LinkedHashMap<String, Contact>();

        /*Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor.moveToNext()) {
                contact = new Contact();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.));
                String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Log.e("contact", "getAllContacts: " + name + " " + phoneNo + " " + photoUri + " " + email);
                contact.setName(name);
                contact.setPhoneNumber(phoneNo);
                contact.setPhotoUri(photoUri);
                contact.setEmail(email);
                //contacts.add(contact);
                cleanList.put(contact.getPhoneNumber(), contact);
            }
        }
        if (cursor != null) {
            cursor.close();
        }*/

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                // get the contact's information
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                //
                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                // get the user's email address
                String email = null;
                String photoUri = null;
                Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    photoUri = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    ce.close();
                }

                // get the user's phone number
                String phone = null;
                if (hasPhone > 0) {
                    Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cp.close();
                    }
                }

                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhoneNumber(phone);
                contact.setPhotoUri(photoUri);
                contact.setEmail(email);
                contacts.add(contact);
                cleanList.put(contact.getPhoneNumber(), contact);

            } while (cursor.moveToNext());

            // clean up cursor
            cursor.close();
        }

        return new ArrayList<Contact>(cleanList.values());
    }

    //Using LinkedHashMap to eliminate duplicate Contacts
    private List<Contact> clearListFromDuplicatePhoneNumber(List<Contact> list1) {
        Map<String, Contact> cleanMap = new LinkedHashMap<String, Contact>();
        for (int i = 0; i < list1.size(); i++) {
            cleanMap.put(list1.get(i).getPhoneNumber(), list1.get(i));
        }
        return new ArrayList<Contact>(cleanMap.values());
    }
}
