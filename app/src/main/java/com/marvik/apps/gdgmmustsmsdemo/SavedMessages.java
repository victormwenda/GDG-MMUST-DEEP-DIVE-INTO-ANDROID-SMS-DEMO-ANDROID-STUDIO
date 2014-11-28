package com.marvik.apps.gdgmmustsmsdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by victor_mwenda on 11/16/2014. 5:50pm - 8:00pm
 * Phone: 0718034449
 * Email: vmwenda.vm@gmail.com
 * 	other: victor@merusongs.com
 * Website: http://www.merusongs.com
 */
public class SavedMessages extends Activity implements AdapterView.OnItemLongClickListener{

    List<SavedMessagesDetails> lSavedMessages;

    ListView lvSavedMessages;
    int selectedMessage = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_messages);

        init();
        findViewsById();
	}
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        selectedMessage = position;
        final Dialog dialog = new Dialog(SavedMessages.this);
        dialog.setTitle("Choose an option");
        
        ListView dialogList = new ListView(SavedMessages.this);
        dialogList.setAdapter(new ArrayAdapter<String>(SavedMessages.this,R.layout.dialog_list,R.id.dialog_list_textView_options,new String []{"Send Message"," Delete Message"}));
        
        dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    sendMessage(selectedMessage);
                }
                if(position==1){
                    deleteMessage(selectedMessage,false);
                }
                dialog.dismiss();
            }
        });
        
        dialog.setContentView(dialogList);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return false;
    }
    private void init(){
    	
    	TextView tvActionbarTitle = (TextView)findViewById(R.id.action_bar_textView_actionBar_text);
        tvActionbarTitle.setText(getTitle());
        
        lSavedMessages = new ArrayList<SavedMessagesDetails>();
       
    }

    private void findViewsById(){

        lvSavedMessages = (ListView)findViewById(R.id.saved_messages_listView_savedMessages);
        lvSavedMessages.setOnItemLongClickListener(this);
        setMessagesAdapter();
    }
    private void readSavedMessaged(){
        Cursor cursor = getContentResolver().query(MessageProvider.GDG_MMUST_SAVED_SMS_URI,null,null,null,null);

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(MessageProvider.ID));
            String to = cursor.getString(cursor.getColumnIndex(MessageProvider.ADDRESS));
            String body = cursor.getString(cursor.getColumnIndex(MessageProvider.BODY));
            String snippet = cursor.getString(cursor.getColumnIndex(MessageProvider.SNIPPET));
            String status = cursor.getString(cursor.getColumnIndex(MessageProvider.STATUS));

            lSavedMessages.add(new SavedMessagesDetails(id, to, body, snippet, status));
        }
    }
    private void setMessagesAdapter(){
    	
    	lSavedMessages = new ArrayList<SavedMessagesDetails>();
    	readSavedMessaged();
    	lvSavedMessages.setAdapter(new SavedMessagesAdapter());
    }
    private void sendMessage(int selectedMessage){
        SavedMessagesDetails savedMessagesDetails = lSavedMessages.get(selectedMessage);
        SendMessage sendMessage = new SendMessage();
        sendMessage.sendMessage(savedMessagesDetails.getTo(),savedMessagesDetails.getBody());
        deleteMessage(selectedMessage,true);
    }

    private void deleteMessage(int selectedMessage, boolean sent){
        SavedMessagesDetails savedMessagesDetails = lSavedMessages.get(selectedMessage);
        int deleted = getContentResolver().delete(MessageProvider.GDG_MMUST_SAVED_SMS_URI,MessageProvider.ID+"="+savedMessagesDetails.getId(),null);
           if(sent==false){
			if (deleted != 0) {
				makeToast("Message deleted");
				setMessagesAdapter();
			} else
				makeToast("Message could not deleted");
           }
           
    }

    private void makeToast(String text){
        Toast.makeText(SavedMessages.this,text,Toast.LENGTH_SHORT).show();
    }
    public Bitmap getImageBitmap(String to) {
		// TODO Auto-generated method stub
		return BitmapFactory.decodeResource(getResources(), R.drawable.contact);
	}
    class SavedMessagesAdapter extends ArrayAdapter<SavedMessagesDetails>{
         SavedMessagesAdapter(){
            super(SavedMessages.this,R.layout.saved_messages_ui,lSavedMessages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View messageView = convertView;
            if(messageView==null){
                messageView = getLayoutInflater().inflate(R.layout.saved_messages_ui,parent,false);
            }

            SavedMessagesDetails savedMessagesDetails = lSavedMessages.get(position);
            
            ImageView ivContact = (ImageView)messageView.findViewById(R.id.saved_messages_imageView_contact);
            TextView tvSnippet = (TextView)messageView.findViewById(R.id.saved_messages_textView_snippet);
            TextView tvTo = (TextView)messageView.findViewById(R.id.saved_messages_textView_to);
            
            ivContact.setImageBitmap(getImageBitmap(savedMessagesDetails.getTo()));
            tvSnippet.setText(savedMessagesDetails.getSnippet());
            tvTo.setText(savedMessagesDetails.getTo());
            return messageView;
        }
    }
    public class SavedMessagesDetails{
        private int id ;
        private String to ;
        private String body ;
        private String snippet ;
        private String status ;

        public SavedMessagesDetails(int id,String to,String body, String snippet,String status) {
            this.id =id;
            this.to = to;
            this.body= body;
            this.snippet = snippet;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
	
}
