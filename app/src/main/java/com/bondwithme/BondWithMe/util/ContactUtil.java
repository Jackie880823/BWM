package com.bondwithme.BondWithMe.util;

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

    public static List<String> getContactPhones(Context context,Cursor cursor){
        List<String> phoneNumbers = new ArrayList<>();

            int phoneColumn = cursor
                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int phoneNum = cursor.getInt(phoneColumn);
            if (phoneNum > 0) {
                // 获得联系人的ID号
                int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                String contactId = cursor.getString(idColumn);
                // 获得联系人电话的cursor
                Cursor phone = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                                + contactId, null, null);
                if (phone.moveToFirst()) {
                    for (; !phone.isAfterLast(); phone.moveToNext()) {
                        int index = phone
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int typeindex = phone
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int phone_type = phone.getInt(typeindex);
                        String phoneNumber = phone.getString(index).replaceAll("\\s*", "");
                        phoneNumbers.add(phoneNumber);
//                  switch (phone_type) {//此处请看下方注释
//                  case 2:
//                      result = phoneNumber;
//                      break;
//
//                  default:
//                      break;
//                  }
                    }
                    if (!phone.isClosed()) {
                        phone.close();
                    }
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
