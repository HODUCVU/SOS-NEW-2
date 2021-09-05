package com.example.sos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class MyDatabase(private val context : Context)
    : SQLiteOpenHelper(context,"CONTACT_LIST",null,1) {
    private val nameTable = "CONTACT"
    private val column0 = "name"
    private val column1 = "phoneNumber"
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $nameTable($column0 TEXT NOT NULL, $column1 TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
    fun insert(contact : DataContact){
        val db : SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(column0,contact.name)
        cv.put(column1,contact.phoneNumber)
        db.insert(nameTable,null,cv)
    }
    fun query(listContact : MutableList<DataContact>) : List<DataContact>{
        try{
            val db : SQLiteDatabase = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $nameTable",null)
            while(cursor.moveToNext()){
                val name = cursor.getString(0)
                val numberPhone = cursor.getString(1)
                listContact.add(DataContact(name,numberPhone))
            }
            cursor.close()
        }catch (e : Exception){
            Log.e("Query: ",e.toString())
            Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
        }
        return listContact
    }
    fun delete(contact : DataContact){
        val db : SQLiteDatabase = writableDatabase
        db.delete(nameTable,"$column1 = ?", arrayOf(contact.phoneNumber))
    }
    fun deleteAll(){
        val db : SQLiteDatabase = writableDatabase
        db.delete(nameTable,null,null)
    }
}
