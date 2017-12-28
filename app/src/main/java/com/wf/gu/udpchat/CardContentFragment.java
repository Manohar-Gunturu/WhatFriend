package com.wf.gu.udpchat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Provides UI for the view with Cards.
 */
public class CardContentFragment extends Fragment {


    private static final ArrayList<String> names = new ArrayList<>();
    private static final ArrayList<String> places = new ArrayList<>();
    private static final ArrayList<String> status = new ArrayList<>();
    private static final ArrayList<String> pictures = new ArrayList<>();
    static DBHelper dbHelper = null;
    private static boolean loading;
    private static ArrayList<Integer> ids = new ArrayList<>();
    private static int th = 0;
    LinearLayoutManager linearLayoutManager;
    ContentAdapter adapter;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new ScrollListener());

        if (names.size() == 0) {
            if (!loading) {
                names.add("Manohar");
                places.add("Nuthakki, Guntur, Andhra Pradesh");
                pictures.add("../users/images/1/profile.png");
                status.add("Jst Coding....");
                ids.add(1);
                loading = true;
                Log.e("URLLL", "http://" + Static.IP + ":8080/android/get.jsp?name=offline&offset=0&id=" + Static.user_id);
                if (isNetworkAvailable()) {
                    new AsyncTask1().execute("http://" + Static.IP + ":8080/android/get.jsp?name=offline&offset=0&id=" + Static.user_id);
                }
            }
        }
        return recyclerView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class AsyncTask2 extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            Static.postData(params[0]);
            return null;
        }
    }




   /* @Override
    public void onStart(){
        Log.d("curent_view","OSTARTA IN CARDSFRAGMENT");
        Static.curent_view = "USERS";
        super.onStart();
    }*/

/*

    @Override
    public void onResume(){
        Static.curent_view = "USERS";
        super.onResume();
    }

*/

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView picture;
        public TextView name;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent, int i) {
            super(inflater.inflate(i, parent, false));
            if (i == R.layout.item_card) {
                picture = (ImageView) itemView.findViewById(R.id.card_image);
                name = (TextView) itemView.findViewById(R.id.card_title);
                description = (TextView) itemView.findViewById(R.id.card_text);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("USER_ID", ids.get(getAdapterPosition()));
                        intent.putExtra("USER_NAME", names.get(getAdapterPosition()));
                        intent.putExtra("PLACE", places.get(getAdapterPosition()));
                        intent.putExtra("STATUS", status.get(getAdapterPosition()));
                        intent.putExtra("IMAGE", pictures.get(getAdapterPosition()));
                        context.startActivity(intent);
                    }
                });

                   /* // Adding Snackbar to Action Button inside card
                    final Button button = (Button) itemView.findViewById(R.id.action_button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                           new AsyncTask2().execute("http://192.169.215.112:8080/android/addc.jsp?id="+ids.get(getAdapterPosition())+"&myid="+Static.user_id);
                           saveContact(getAdapterPosition(),v.getContext());
                            button.setVisibility(View.GONE);
                            Snackbar.make(v, "Added",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });
*/
                ImageButton favoriteImageButton =
                        (ImageButton) itemView.findViewById(R.id.favorite_button);
                favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Messages.class);
                        intent.putExtra("USER_NAME", names.get(getAdapterPosition()));
                        intent.putExtra("USER_ID", ids.get(getAdapterPosition()));
                        intent.putExtra("USER_PLACE", places.get(getAdapterPosition()));
                        intent.putExtra("USER_STATUS", status.get(getAdapterPosition()));
                        intent.putExtra("USER_IMAGE", pictures.get(getAdapterPosition()));
                        intent.putExtra("IS_CONTACT", false);
                        v.getContext().startActivity(intent);
                    }
                });

                   /* ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
                    shareImageButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(v, "Share article",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });*/
            }

        }


    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        Context cx;


        public ContentAdapter(Context context) {
            cx = context;


        }


        public void updateView(ArrayList<String> name, ArrayList<String> place, ArrayList<String> pic, ArrayList<Integer> i, ArrayList<String> st) {
            names.addAll(name);
            places.addAll(place);
            pictures.addAll(pic);
            ids.addAll(i);
            status.addAll(st);
            notifyDataSetChanged();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, R.layout.item_card);
            } else {
                return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, R.layout.progress_item);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return names.get(position) != null ? 0 : 1;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder != null) {
                if (pictures.get(position).equals("user_files/download.svg")) {
                    Picasso.with(cx).load(R.drawable.d).into(holder.picture);
                    //holder.picture.setBackgroundResource(R.drawable.d);
                    Log.e("IMAGEEE", "YESS");
                } else {
                    Log.e("IMAGEEE", "YESS " + pictures.get(position).replace("..", ""));
                    Picasso.with(cx).load("http://" + Static.IP + ":8080/" + pictures.get(position).replace("..", "")).into(holder.picture);
                }
                holder.name.setText(names.get(position));
                holder.description.setText(places.get(position));
            }
        }


        @Override
        public int getItemCount() {
            return names.size();
        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            // your code there
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            Log.d("SCROLLINGGGGGGG", dx + " " + dy);
            if (dy > 0) {
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                Log.d("VALUESSS", loading + " " + totalItemCount + " " + lastVisibleItem + " " + visibleThreshold);
                if (!loading && (totalItemCount <= (lastVisibleItem + visibleThreshold)) && isNetworkAvailable()) {
                    Log.d("RECHHHHHHHHHHHHHHH", "endddddddddddddddd");
                    new AsyncTask1().execute("http://" + Static.IP + ":8080/android/get.jsp?name=offline&offset=" + (th = th + 15) + "&id=" + Static.user_id);

                }
            }
        }

    }

    public class AsyncTask1 extends AsyncTask<String, Integer, String> {
        private final ArrayList<String> names1 = new ArrayList<>();
        private final ArrayList<String> places1 = new ArrayList<>();
        private final ArrayList<String> pictures1 = new ArrayList<>();
        private final ArrayList<Integer> id = new ArrayList<>();
        private final ArrayList<String> st = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {
            loading = true;
            return Static.postData(params[0]);
        }

        protected void onPostExecute(String result) {
            Log.e("TAAAAAAAAAAA", result);
            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    names1.add(obj.getString("name"));
                    places1.add(obj.getString("location"));
                    pictures1.add(obj.getString("picture"));
                    id.add(obj.getInt("message"));
                    st.add(obj.getString("status"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.updateView(names1, places1, pictures1, id, st);
            loading = false;
        }
    }


}
