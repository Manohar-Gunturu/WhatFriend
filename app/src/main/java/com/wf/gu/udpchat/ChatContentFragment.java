package com.wf.gu.udpchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import umt.Callback;
import umt.SocketWrapper;

/**
 * Provides UI for the view with List.
 */
public class ChatContentFragment extends Fragment {

    public static final ArrayList<String> names = new ArrayList<>();
    public static final ArrayList<String> messages = new ArrayList<>();
    public static final ArrayList<String> date = new ArrayList<>();
    public static ArrayList<Integer> ids = new ArrayList<>();
    DBHelper dbHelper = null;
    ConnectivityManager cm;
    ContentAdapter adapter;
    RecyclerView recyclerView;


    public String getParameter(String str,String parameterName){
        str = str.substring(str.indexOf(parameterName) + parameterName.length()+1);
        return str.substring(0, str.indexOf(";"));
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        adapter = new ContentAdapter(recyclerView.getContext());
        dbHelper = new DBHelper(getActivity().getApplication());
        names.clear();
        ids.clear();
        messages.clear();
        date.clear();
        recyclerView.setAdapter(adapter);
        Static.curent_view = "CHAT";

        SocketWrapper.attachListener(new Callback() {
            @Override
            public void onMessage(String message) {
                if(message.isEmpty() || !getParameter(message, "type").equals("message")){
                    return;
                }
                int id = Integer.parseInt(getParameter(message,"fid"));
                getActivity().runOnUiThread(()-> {
                if (ids.contains(id)) {
                    View v = recyclerView.getLayoutManager().findViewByPosition(ids.indexOf(id));
                    TextView t = (TextView) v.findViewById(R.id.list_desc);
                    t.setText(getParameter(message,"value"));
                    TextView time = (TextView) v.findViewById(R.id.time_stamp);
                    time.setText("NEW");
                } else {
                    ids.add(0, id);
                    names.add(0,  getParameter(message,"uname") );
                    messages.add(0, getParameter(message,"value"));
                    date.add(0, getParameter(message,"date"));

                    adapter.notifyDataSetChanged();
                }
                });
            }
        });


        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    @Override
    public void onStart() {
        Static.curent_view = "CHAT";
        super.onStart();
    }

    @Override
    public void onResume() {
        Static.curent_view = "CHAT";
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;
        public TextView time;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.chat_item, parent, false));
            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            time = (TextView) itemView.findViewById(R.id.time_stamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Messages.class);
                    intent.putExtra("USER_NAME", names.get(getAdapterPosition()));
                    intent.putExtra("USER_ID", ids.get(getAdapterPosition()));
                    intent.putExtra("USER_PLACE", "Not Loaded");
                    intent.putExtra("USER_STATUS", "Hey ");
                    intent.putExtra("IS_CONTACT", false);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        Context cs;

        public ContentAdapter(Context context) {
            cs = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (names.get(position) != null) {
                holder.name.setText(names.get(position));
                holder.description.setText(messages.get(position));
                holder.time.setText(date.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return names.size();
        }
    }

    class MyTask3 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ids.clear();
            names.clear();
            messages.clear();
            date.clear();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT distinct UID,UNAME,MESSAGE,HIMAGE,TIME, MAX(ID),IS_NEW FROM CHAT GROUP BY UID ORDER BY ID DESC", null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getInt(cursor.getColumnIndex("UID")));
                    names.add(cursor.getString(cursor.getColumnIndex("UNAME")));
                    messages.add(cursor.getString(cursor.getColumnIndex("MESSAGE")));
                    if (cursor.getInt(cursor.getColumnIndex("IS_NEW")) == 1) {
                        date.add("NEW");
                    } else {
                        date.add(cursor.getString(cursor.getColumnIndex("TIME")));
                    }
                } while (cursor.moveToNext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            adapter.notifyDataSetChanged();
        }
    }

}

