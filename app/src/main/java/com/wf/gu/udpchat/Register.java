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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static ProgressBar spinner;
    LinearLayout l;

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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(getActivity(), "Unable To Connect", Toast.LENGTH_LONG).show();
                    return;
                }
                EditText full_name = (EditText) rootView.findViewById(R.id.editText3);
                EditText email = (EditText) rootView.findViewById(R.id.editText);
                EditText pass = (EditText) rootView.findViewById(R.id.editText2);
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
                Static.user_image = "user_files$download.svg";

                String msg = "type=register;name="+full_name.getText().toString()+";"+
                        "place="+Static.user_place+";";

                Static.user_place = (Static.user_place).replaceAll(" ", "_");
                Static.user_place = (Static.user_place).replaceAll(",", "");

                Task t = new Task(msg, new Callback() {
                    @Override
                    public void onMessage(String message) {

                    }
                });

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

    public boolean isValidEmail(String email) {

        String EMAIL_REGEX = "^(.+)@(.+)$";
        return email.matches(EMAIL_REGEX);
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }


    public class Task extends AsyncTask<String, Integer, String> {


        String message = null;
        Callback c;

        Task(String msg, Callback nc) {
            c = nc;
            message = msg;
        }

        @Override
        protected String doInBackground(String... strings) {
            String s = SocketWrapper.send(message, c,true);
            return s;
        }


        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                Register.spinner.setVisibility(View.GONE);
                Intent inten = new Intent(MainActivity.cx, Main2Activity.class);
                inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                inten.putExtra("EXTRA_SESSION_ID", "NEW_USER");
                getActivity().startActivity(inten);
            }
        }

        }


}