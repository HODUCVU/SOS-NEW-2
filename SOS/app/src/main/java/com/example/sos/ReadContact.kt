package com.example.sos

import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

class ReadContact(private val context : MainActivity) {
    private lateinit var db : MyDatabase
    private val listTemp = mutableListOf<DataContact>()
    init {
        init()
    }
    private fun init() {
        db = MyDatabase(context.applicationContext)
    }

    fun readContact() {
        try {
            if (
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context,
                    Array(1) { Manifest.permission.READ_CONTACTS },
                    context.request
                )
            }
            else {
                queryListContact()
            }
        }
        catch (e : Exception) {
            Log.e("Error in one: ",e.toString())
            Toast.makeText(context,e.message, Toast.LENGTH_LONG).show()
        }
    }
    private fun saveListContact() {
        /**
         * nếu mà trong database có dữ liệu rồi thì check để không bị lỗi lặp dữ liệu
         */
        listContact.clear()
        listContact.addAll(db.query(listContact))
        for(i in 0 until listTemp.size){
            if(!listContact.contains(listTemp[i]))
               db.insert(listTemp[i])
        }
        listContact.clear()
        listContact.addAll(db.query(listContact))
    }
    fun deleteContact(contact : DataContact) {
        db.delete(contact)
        listContact.clear()
        listContact.addAll(db.query(listContact))
    }
    fun deleteAll() {
        db.deleteAll()
        listContact.clear()
    }
    fun queryListContact() {
        try {
            val contacts = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
//            listTemp.clear()
            listContact.clear()
            while (contacts!!.moveToNext()) {
                val name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val numberPhone =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                listContact.add(DataContact(name,numberPhone))
            }
            contacts.close()
        }
        catch (e : Exception){
            Log.e("Error in three: ",e.toString())
            Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
        }
    }
}