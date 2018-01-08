package com.wf.gu.udpchat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import umt.Callback;
import umt.SocketWrapper;


public class Register extends Fragment {

    public static ProgressBar spinner;
    LinearLayout l;


    public String getParameter(String str,String parameterName){
        str = str.substring(str.indexOf(parameterName+"=") + parameterName.length()+1);
        return str.substring(0, str.indexOf(";"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.

        final View rootView = inflater.inflate(
                R.layout.register_fragment, container, false);
        final AutoCompleteTextView autocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete);

        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
            }
        });

        Button submit = (Button) rootView.findViewById(R.id.button);
        //on click of register button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if not connected to a network  make toast
                if (!isNetworkAvailable()) {
                    Toast.makeText(getActivity(), "Unable To Connect", Toast.LENGTH_LONG).show();
                    return;
                }
                EditText full_name = (EditText) rootView.findViewById(R.id.editText3);

                if (full_name.getText() == null || full_name.getText().length() < 3) {
                    full_name.setError("User name please !!");
                    return;
                }

                if (autocompleteView.getText() == null || autocompleteView.getText().length() < 3) {
                    autocompleteView.setError("Present City !!");
                    return;
                }

                l = (LinearLayout) rootView.findViewById(R.id.regview);
                l.setVisibility(LinearLayout.GONE);

                spinner = (ProgressBar) rootView.findViewById(R.id.progress);
                spinner.setVisibility(View.VISIBLE);

                Static.user_name = full_name.getText().toString();
                Static.user_place = autocompleteView.getText().toString();

                String msg = "type=register;name=" + full_name.getText().toString() + ";" +
                        "place=" + Static.user_place + ";";

                Static.user_place = (Static.user_place).replaceAll(" ", "_");
                Static.user_place = (Static.user_place).replaceAll(",", "");

                //start a async task to send details to server
                Task t = new Task(msg);
                t.execute();

            }
        });

        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class Task extends AsyncTask<String, Integer, String> {

        String message = null;

        Task(String msg) {
            message = msg;
        }


        @Override
        protected String doInBackground(String... strings) {
            return SocketWrapper.send(message,true);
        }

        @Override
        protected void onPostExecute(String result){
            if(getParameter(result,"type").equals("user_id")){
                Static.user_id = Integer.parseInt(getParameter(result,"id"));
                Register.spinner.setVisibility(View.GONE);
                Intent inten = new Intent(MainActivity.cx, Main2Activity.class);
                inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                inten.putExtra("EXTRA_SESSION_ID", "NEW_USER");
                getActivity().startActivity(inten);
            }

        }

    }

}