package com.marvik.apps.gdgmmustsmsdemo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by victor_mwenda on 11/15/2014. 5:30pm - 7:50pm
 * Phone: 0718034449
 * Email: vmwenda.vm@gmail.com
 * 	other: victor@merusongs.com
 * Website: http://www.merusongs.com
 */
public class SendMessage extends Activity implements View.OnClickListener{

    SmsManager smsManager;
    AutoCompleteTextView atvTo;
    EditText etMessage;
    Button btSendMessage,btSaveMessage;
    ImageView ivSelectContact;

    ArrayList<String>sPhoneNumbers;

    public static final String INTENT_SMS_SENT;
    public static final String INTENT_SMS_DELIVERED;
    public static final String TO;
    public static final String BODY;
    public static final String DELIVERED;

    static{
        INTENT_SMS_SENT="com.marvik.apps.gdgmmustsmsdemo.INTENT_SMS_SENT";
        INTENT_SMS_DELIVERED="com.marvik.apps.gdgmmustsmsdemo.INTENT_SMS_DELIVERED";
        TO="to";
        BODY="body";
        DELIVERED="delivered";
    }

    

    public SendMessage(){
      
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);


        init();
        initOurViewsById();
    }
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//unregisterReceiver(new SentMessageReceiver());
	}
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver(new SentMessageReceiver(), new IntentFilter(SendMessage.INTENT_SMS_DELIVERED));
        registerReceiver(new SentMessageReceiver(), new IntentFilter(SendMessage.INTENT_SMS_SENT));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	getMenuInflater().inflate(R.menu.menu_send_message, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.item_view_saved_messages){
			startActivity(new Intent(getApplicationContext(),SavedMessages.class));
		}
		return super.onOptionsItemSelected(item);
	}
	private void init() {
        // TODO Auto-generated method stub
        smsManager = SmsManager.getDefault();
        sPhoneNumbers = new ArrayList<String>();
        readPhoneContacts();
    }

    private void initOurViewsById(){
        TextView tvActionbarTitle = (TextView)findViewById(R.id.action_bar_textView_actionBar_text);
        tvActionbarTitle.setText(getTitle());

        atvTo =(AutoCompleteTextView)findViewById(R.id.send_message_autocompleteTextView_to);
        atvTo.setAdapter(new ArrayAdapter<String>(SendMessage.this,R.layout.contact_list,R.id.contact_list_textView_contact_number,sPhoneNumbers));

        etMessage =(EditText)findViewById(R.id.send_message_editText_message);

        btSendMessage = (Button)findViewById(R.id.send_message_button_send);
        btSendMessage.setOnClickListener(this);

        btSaveMessage = (Button)findViewById(R.id.send_message_button_save);
        btSaveMessage.setOnClickListener(this);

        ivSelectContact = (ImageView)findViewById(R.id.send_message_imageView_contact);
        ivSelectContact.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.send_message_button_send){
            validateFields(R.id.send_message_button_send);

        }
        if(view.getId()==R.id.send_message_button_save){
            validateFields(R.id.send_message_button_save);

        }
        if(view.getId()==R.id.send_message_imageView_contact){
            selectContact();
        }
    }

    private void validateFields(int buttonID){

        String to = atvTo.getText().toString();
        String body = etMessage.getText().toString();

        if(!to.equals("")&&!body.equals("")){
           
        	if(buttonID==R.id.send_message_button_send){
                sendMessage(to,body);
            }
            
        	if(buttonID==R.id.send_message_button_save){
                saveMessage(to,body,1);
            }

        }else{
            if(!to.equals("")){
                if(body.equals("")){
                    requestSendBlankMessage(to,body);
                }
            }

            if (to.equals("")) {
                atvTo.setHintTextColor(Color.RED);
                makeToast("Please provide a recipient");
            }

            if (body.equals("")) {
                etMessage.setHintTextColor(Color.RED);
            }
        }
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void requestSendBlankMessage(final String to, final String body){
        AlertDialog.Builder requestAlert = new AlertDialog.Builder(SendMessage.this,AlertDialog.THEME_HOLO_LIGHT);
        requestAlert.setTitle("Send message");
        requestAlert.setMessage("Do you want to send a blank message?");
        requestAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMessage(to, body);
            }

        });
        requestAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeToast("Please compose your message");
            }

        });

        requestAlert.setIcon(android.R.drawable.ic_menu_help);
        requestAlert.create();
        requestAlert.show();
    }
    public boolean sendMessage(String to,String body ){

        Bundle smsSentBundle = new Bundle();
        smsSentBundle.putString(SendMessage.TO, to);
        smsSentBundle.putString(SendMessage.BODY, body);
        smsSentBundle.putBoolean(SendMessage.DELIVERED,false);

        Bundle smsDeliveredBundle = new Bundle(smsSentBundle);
        smsDeliveredBundle.putBoolean(SendMessage.DELIVERED,true);

        PendingIntent sentIntent = PendingIntent.getBroadcast(SendMessage.this, 1, new Intent(SendMessage.INTENT_SMS_SENT).putExtras(smsSentBundle), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(SendMessage.this, 2, new Intent(SendMessage.INTENT_SMS_DELIVERED).putExtras(smsDeliveredBundle), PendingIntent.FLAG_UPDATE_CURRENT);

        int smsLength = etMessage.getText().toString().length();

        if(smsLength>160){
            ArrayList<String>parts = smsManager.divideMessage(body);
            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents= new ArrayList<PendingIntent>();

            for(int i =0;i<parts.size();i++){
                sentIntents.add(sentIntent);
                deliveryIntents.add(deliveryIntent);
            }
            smsManager.sendMultipartTextMessage(to, null, parts, sentIntents, deliveryIntents);
        }

        if(smsLength<=160){
            smsManager.sendTextMessage(to, null, body, sentIntent, deliveryIntent);
        }

        return true;
    }
    private void saveMessage(String to, String body,int status){

		ContentValues values = new ContentValues();
		values.put(MessageProvider.ADDRESS, to);
		values.put(MessageProvider.BODY, body);
		values.put(MessageProvider.STATUS, status);
		values.put(MessageProvider.SNIPPET, getMessageSnippet(body));
		getContentResolver().insert(MessageProvider.GDG_MMUST_SAVED_SMS_URI,values);

		Log.i("Saving Message ", "Status : "+status);
    }

    private void writeMessage(String to, String body,boolean delivered){
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.BODY,body);
        values.put(Telephony.Sms.ADDRESS,to);

        if(delivered){
            values.put(Telephony.Sms.STATUS, "1");
            getContentResolver().update(Uri.parse("content://sms/sent"), values, Telephony.Sms.ADDRESS +"='"+to+"' AND "+ Telephony.Sms.BODY+"='"+body+"'",null);
            makeToast("Message delivered to "+to);
        }else{
            getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        }
    }
    private String getMessageSnippet(String longMessage){

        return longMessage.length()<45? longMessage:longMessage.substring(0,45);
    }
    private void makeToast(String text){
        Toast.makeText(SendMessage.this, text, Toast.LENGTH_SHORT).show();
    }
    private void resetFields(){
        atvTo.setText("");
        etMessage.setText("");
    }
    private void selectContact(){
        Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 3);
    }
    private void readPhoneContacts(){
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String []{ContactsContract.CommonDataKinds.Phone.NUMBER},null,null,null);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            sPhoneNumbers.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                String to = new String();
                Cursor cursor = getContentResolver().query(data.getData(), new String []{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){

                    to = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }

                atvTo.setText(to);
            }
        }

    }

    private class SentMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            /**
             * String to = intent.getExtras().getString(SendMessage.TO); String
             * body = intent.getExtras().getString(SendMessage.BODY); boolean
             * delivered = intent.getExtras().getBoolean(SendMessage.DELIVERED);
             * writeMessage(to, body, delivered);
             *
             * */
            if(intent.getAction().equals(SendMessage.INTENT_SMS_SENT)){

                String to = intent.getExtras().getString(SendMessage.TO);
                String body = intent.getExtras().getString(SendMessage.BODY);
                writeMessage(to, body, false);

                switch(getResultCode()){
                    case RESULT_OK:
                        makeToast("Message sent to "+to);
                        resetFields();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        makeToast("Generic failure : Message not sent to "+to);
                        saveMessage(to,body,2);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        makeToast("No service : Message not sent to "+to);
                        saveMessage(to,body,3);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        makeToast("No PDU's : Message not sent to "+to);
                        saveMessage(to,body,4);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        makeToast("Radio off : Message not sent to "+to);
                        saveMessage(to,body,5);
                        break;
                }
            }
            if(intent.getAction().equals(SendMessage.INTENT_SMS_DELIVERED)){
                String to = intent.getExtras().getString(SendMessage.TO);
                String body = intent.getExtras().getString(SendMessage.BODY);
                writeMessage(to, body, true);
            }

        }

    }
}
