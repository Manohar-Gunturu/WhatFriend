package com.wf.gu.udpchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import umt.Callback;
import umt.SocketWrapper;


public class Messages extends AppCompatActivity {


    String USER_NAME, USER_PLACE, USER_STATUS, USER_IMAGE;
    int USER_ID;
    DBHelper dbHelper = null;
    SQLiteDatabase db = null;
    TextView las;
    boolean is_c = false;
    EditText ms = null;
    LinearLayoutManager mLayoutManager;
    int in = 8, totalItemCount, lastVisibleItem;
    ConnectivityManager cm;
    String date = "1/14", tmp;
    String pf_view = "MS";
    boolean loading = false;
    Callback callback = null;
    private ArrayList<String> messages = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<Boolean> b = new ArrayList<>();
    private ArrayList<Boolean> c = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;



    public String getParameter(String str,String parameterName){
        str = str.substring(str.indexOf(parameterName) + parameterName.length()+1);
        return str.substring(0, str.indexOf(";"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        pf_view = Static.curent_view;
        Static.curent_view = "MS_VIEW";
        las = (TextView) findViewById(R.id.last_seen);
        if (extras != null) {
            USER_NAME = extras.getString("USER_NAME");
            USER_PLACE = extras.getString("USER_PLACE");
            USER_STATUS = extras.getString("USER_STATUS");
            USER_IMAGE = extras.getString("USER_IMAGE");
            USER_ID = extras.getInt("USER_ID");
            is_c = extras.getBoolean("IS_CONTACT");
            Ozo.Ozoset(USER_NAME, USER_PLACE, USER_IMAGE, USER_STATUS, USER_ID, is_c);

        } else {
            USER_NAME = Ozo.user_name;
            USER_ID = Ozo.user_id;
            USER_PLACE = Ozo.user_place;
            USER_STATUS = Ozo.user_status;
            USER_IMAGE = Ozo.user_image;
            is_c = Ozo.c;
        }


        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Static.curr_view_users = USER_ID;
        dbHelper = new DBHelper(this.getApplication());
        db = dbHelper.getReadableDatabase();

        callback = new Callback() {
            @Override
            public void onMessage(String message) {
                if (!message.isEmpty() && getParameter(message, "type").equals("message")) {
                    if(getParameter(message, "fid").equals(USER_ID+"")) {
                        runOnUiThread(() -> {
                            messages.add(getParameter(message, "value"));
                            dates.add(getParameter(message, "date"));
                            b.add(true);
                            tmp = (getParameter(message, "date")).split(" ")[0];
                            date = dates.get(dates.size()-1).split(" ")[0];
                            if(!date.equals(tmp)){
                                c.add(true);
                            }else{
                                c.add(false);
                            }
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.smoothScrollToPosition(b.size());
                        });
                    }
                }
            }
        };

        SocketWrapper.attachListener(callback);

        genView();

    }

    @Override
    public void onResume() {
        Static.curent_view = "MS_VIEW";
        super.onResume();
    }

    public void genView() {
        TextView un = (TextView) findViewById(R.id.user_nm);
        un.setText(USER_NAME);
        ImageView im = (ImageView) findViewById(R.id.uim);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_m);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new ScrollListener());
        mAdapter = new MessageAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ms = (EditText) findViewById(R.id.message_c);
        ImageButton sendms = (ImageButton) findViewById(R.id.send_ms);


        final int[] j = {0};

        sendms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ms.getText().toString().length() < 1) {
                    return;
                }
                messages.add(ms.getText().toString());
                Date date1 = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mm a");
                String formattedDate = sdf.format(date1);
                dates.add(formattedDate);
                b.add(false);
                tmp = (formattedDate).split(" ")[0];
                date = dates.get(dates.size() - 1).split(" ")[0];
                if (!date.equals(tmp)) {
                    c.add(true);
                } else {
                    c.add(false);
                }

                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(b.size());



                if(isNetworkAvailable()){
                    j[0] = 1;

                    new Thread(() -> {
                        String mes = "type=message;tid="+USER_ID+";fid=" +Static.user_id+ ";value="+ms.getText().toString()+";date="+formattedDate+";uname="+Static.user_name+";";
                        SocketWrapper.send(mes);
                    }).start();

                    db.execSQL("INSERT INTO CHAT(UID,MESSAGE,TIME,UNAME,HIMAGE,IS_NEW,IS_WHOM,IS_SENT) VALUES(" + USER_ID + ",'" + ms.getText().toString() + "','" + formattedDate + "','" + USER_NAME + "','" + USER_IMAGE + "',0,0," + j[0] + ") ");
                    ms.setText("");

                }else{
                    Toast.makeText(getApplicationContext(), "Unable to Connect to Server, May be the server is down", Toast.LENGTH_LONG).show();
                    j[0] = 0;
                }
            }
        });


        new HeavyTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new DeleteTask().execute();
            //delete chat
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onStart() {
        Static.curent_view = "MS_VIEW";
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        Static.curent_view = pf_view;
        Static.curr_view_users = 0;
        SocketWrapper.removelistner(callback);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Static.curr_view_users = 0;
        super.onDestroy();
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            // your code there
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy < 0) {
                lastVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (!loading && lastVisibleItem < 4) {

                    new HeavyTask1().execute(in = in + 8);
                }

            }
        }

    }



    private class DeleteTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... params) {
            db.execSQL("DELETE FROM CHAT WHERE UID=" + USER_ID);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer i) {
            messages.clear();
            b.clear();
            c.clear();
            dates.clear();
            mAdapter.notifyDataSetChanged();

        }


    }

    private class HeavyTask extends AsyncTask<Void, Void, Integer> {

        String t = "";
        boolean is_ck = false;

        @Override
        protected Integer doInBackground(Void... params) {
            loading = true;
            is_ck = false;
            Cursor cursor = db.rawQuery("SELECT MESSAGE,HIMAGE,TIME,IS_WHOM FROM CHAT WHERE UID=" + USER_ID + " ORDER BY ID DESC LIMIT 15", null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    messages.add(0, cursor.getString(cursor.getColumnIndex("MESSAGE")));
                    if (cursor.getInt(cursor.getColumnIndex("IS_WHOM")) == 0) {
                        b.add(0, false);
                    } else {
                        b.add(0, true);
                    }
                    t = ((cursor.getString(cursor.getColumnIndex("TIME"))));
                    tmp = t.split(" ")[0];
                    if (!date.equals(tmp) && !date.equals("1/14")) {
                        c.add(0, true);
                        is_ck = true;
                    } else {
                        c.add(0, false);
                    }
                    date = tmp;
                    dates.add(0, t);
                } while (cursor.moveToNext());
            } else {
                loading = true;
            }
            //if (db != null)
            //db.close();
            if (c.size() > 0) {
                return 1;
            } else {
                return 0;
            }

        }


        @Override
        protected void onPostExecute(Integer i) {
            loading = false;
            if (i != 0) {
                if (!is_ck) {
                    c.set(0, true);
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(b.size());
            }

        }
    }


    private class HeavyTask1 extends AsyncTask<Integer, Void, Integer> {

        String t = "";
        boolean v = false;
        int y = 0;
        boolean is_ck = false;

        @Override
        protected Integer doInBackground(Integer... params) {
            loading = true;
            v = false;
            y = 0;
            is_ck = false;
            Cursor cursor = db.rawQuery("SELECT MESSAGE,HIMAGE,TIME,IS_WHOM FROM CHAT WHERE UID=" + USER_ID + " ORDER BY ID DESC LIMIT " + params[0] + ",8", null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    v = true;
                    messages.add(0, cursor.getString(cursor.getColumnIndex("MESSAGE")));
                    if (cursor.getInt(cursor.getColumnIndex("IS_WHOM")) == 0) {
                        b.add(0, false);
                        is_ck = true;
                    } else {
                        b.add(0, true);
                    }
                    y = y + 1;
                    t = ((cursor.getString(cursor.getColumnIndex("TIME"))));
                    tmp = t.split(" ")[0];
                    if (!date.equals(tmp)) {
                        c.add(0, true);
                    } else {
                        c.add(0, false);
                    }
                    date = tmp;
                    dates.add(0, t);
                } while (cursor.moveToNext());
            } else {
                loading = true;
            }
            //if (db != null)
            //db.close();

            return 1;
        }


        @Override
        protected void onPostExecute(Integer i) {

            if (!is_ck) {
                c.set(0, true);
            }
            if (v) {
                loading = false;
                mAdapter.notifyItemRangeInserted(0, y);
            }

        }
    }

    class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public MessageAdapter() {

        }


        @Override
        public int getItemViewType(int position) {

            if (dates.size() > 0) {

                if (c.get(position)) {
                    if (b.get(position)) {
                        return 2;
                    } else {
                        return 3;
                    }
                }

                if (b.get(position)) {
                    return 1;
                } else {
                    return 0;
                }
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new ViewHolder1(LayoutInflater.from(parent.getContext()), parent);

            } else if (viewType == 2) {
                return new ViewHolder2(LayoutInflater.from(parent.getContext()), parent);

            } else if (viewType == 3) {
                return new ViewHolder3(LayoutInflater.from(parent.getContext()), parent);

            } else {
                return new ViewHolder4(LayoutInflater.from(parent.getContext()), parent);

            }

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ViewHolder1 v = (ViewHolder1) holder;
                v.tx.setText(messages.get(position));
                v.dt.setText(dates.get(position).substring(dates.get(position).indexOf(" ") + 1, (dates.get(position)).length()));
            } else if (holder.getItemViewType() == 2) {
                ViewHolder2 v = (ViewHolder2) holder;
                v.tx.setText(messages.get(position));
                v.dt.setText(dates.get(position).substring(dates.get(position).indexOf(" ") + 1, (dates.get(position)).length()));
                v.getdate().setText(dates.get(position));
            } else if (holder.getItemViewType() == 3) {
                ViewHolder3 v = (ViewHolder3) holder;
                v.tx.setText(messages.get(position));
                v.dt.setText(dates.get(position).substring(dates.get(position).indexOf(" ") + 1, (dates.get(position)).length()));
                v.getdate().setText(dates.get(position));
            } else if (holder.getItemViewType() == 1) {
                ViewHolder4 v = (ViewHolder4) holder;
                v.tx.setText(messages.get(position));
                v.dt.setText(dates.get(position).substring(dates.get(position).indexOf(" ") + 1, (dates.get(position)).length()));
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tx;
        TextView dt;

        public ViewHolder1(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.right_msg_item, parent, false));
            tx = (TextView) itemView.findViewById(R.id.msg_ri);
            dt = (TextView) itemView.findViewById(R.id.textView);
        }


    }

    public class ViewHolder4 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tx;
        TextView dt;

        public ViewHolder4(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.left_msg_item, parent, false));
            tx = (TextView) itemView.findViewById(R.id.msg_ri);
            dt = (TextView) itemView.findViewById(R.id.textView);
        }


    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tx;
        TextView dt;

        public ViewHolder2(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.left_date, parent, false));
            tx = (TextView) itemView.findViewById(R.id.msg_ri);
            dt = (TextView) itemView.findViewById(R.id.textView);
        }

        public TextView getdate() {
            return (TextView) itemView.findViewById(R.id.textView2);
        }
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tx;
        TextView dt;

        public ViewHolder3(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.right_date, parent, false));
            tx = (TextView) itemView.findViewById(R.id.msg_ri);
            dt = (TextView) itemView.findViewById(R.id.textView);
        }

        public TextView getdate() {
            return (TextView) itemView.findViewById(R.id.textView2);
        }
    }

}
