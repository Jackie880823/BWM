package com.madx.bwm.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 15/4/9.
 */
public class ContactUtil {


    private static ContentResolver reslover;
    private static ContentResolver getReslover(Context context){
        if(reslover==null){
            reslover = context.getContentResolver();
        }
        return reslover;
    }

    public static Cursor getContacts(Context context,String[] projection,
                                     String selection, String[] selectionArgs, String sortOrder){
        return getReslover(context).query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    public static List<String> getContactPhones(Context context,int contactId){
        Cursor phoneCursor = getReslover(context).query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null,
                null);

        List<String> phoneNumbers = new ArrayList<>();
        if(phoneCursor!=null) {
            while (phoneCursor.moveToNext()) {
                phoneNumbers.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
        }
        return phoneNumbers;
    }

    public static List<String> getContactEmails(Context context,int contactId){
        Cursor emailCursor = getReslover(context).query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null,
                null);

        List<String> emails = new ArrayList<>();
        if(emailCursor!=null) {
            while (emailCursor.moveToNext()) {
                emails.add(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
            }
        }
        return emails;
    }
}
