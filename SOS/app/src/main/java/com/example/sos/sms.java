package com.example.sos;

import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class sms {
    public void init(String strMessage, List<DataContact> number, MainActivity context){
        try {
            for (int i=0;i < number.size(); i++){
                String strNumber = number.get(i).getPhoneNumber().trim();
                SmsManager mySms = SmsManager.getDefault();
                mySms.sendTextMessage(strNumber,null,strMessage,null,null);
            }
        }catch (Exception e){
            Log.e("in sms: ",""+e.toString());
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}