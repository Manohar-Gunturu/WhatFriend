/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wf.gu.udpchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import umt.Callback;
import umt.SocketWrapper;

/**
 * Provides UI for the view with List.
 */
public class ListContentFragment extends Fragment {

    public static final ArrayList<String> names = new ArrayList<>();
    public static final ArrayList<String> places = new ArrayList<>();
    public static final ArrayList<String> status = new ArrayList<>();
    public static ArrayList<Integer> ids = new ArrayList<>();
    public static ContentAdapter adapter;
    DBHelper dbHelper = null;
    ConnectivityManager cm;


    public String getParameter(String str,String parameterName){
        str = str.substring(str.indexOf(parameterName) + parameterName.length()+1);
        return str.substring(0, str.indexOf(";"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        Static.curent_view = "ONLINE";
        adapter = new ContentAdapter(recyclerView.getContext());
        dbHelper = new DBHelper(getActivity().getApplication());
        recyclerView.setAdapter(adapter);


        SocketWrapper.attachListener(new Callback() {
            @Override
            public void onMessage(String message) {
                    if(!message.isEmpty() && getParameter(message,"type").equals("users")) {
                        final String m = message.replace("type=users;","");
                        getActivity().runOnUiThread(()->{
                            String[] users = m.split("#\\$");
                            for(String s : users){
                                if(s.isEmpty())
                                    continue;
                                names.add(s.split(":")[0]);
                                places.add(s.split(":")[1]);
                                ids.add(Integer.parseInt(s.split(":")[2]));
                                status.add("Hey there! I am using WhatFriend");
                            }
                            adapter.notifyDataSetChanged();
                        });
                    }
            }
        });

        new Thread(new Task()).start();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    @Override
    public void onStart() {

        Static.curent_view = "ONLINE";
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Messages.class);
                    intent.putExtra("USER_NAME", names.get(getAdapterPosition()));
                    intent.putExtra("USER_ID", ids.get(getAdapterPosition()));
                    intent.putExtra("USER_PLACE", places.get(getAdapterPosition()));
                    intent.putExtra("USER_STATUS", status.get(getAdapterPosition()));
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
                holder.description.setText(places.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return names.size();
        }
    }




    class Task implements Runnable{
        @Override
        public void run() {
            SocketWrapper.send("type=get_users;id="+Static.user_id+";");
        }
    }

}
