package com.example.plansplit.Controllers.FragmentControllers.friends;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.FriendsAdapter;
import com.example.plansplit.Models.Database;
import com.example.plansplit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";
    private static final Database database = Database.getInstance();
    private RecyclerView m_RecyclerView;
    private FriendsAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;
    private String person_id = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_friends, container, false);
        Log.d(TAG, "BURADA");

        Button add_friend_button = root.findViewById(R.id.friend_add_button);
        final EditText add_friend_email_text = root.findViewById(R.id.friends_add_email);

        person_id = getArguments().get("person_id").toString();


        m_RecyclerView = root.findViewById(R.id.recycler_friends);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(getActivity());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        add_friend_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = add_friend_email_text.getText().toString();
                database.sendFriendRequest(person_id, email, new Database.DatabaseCallBack() {
                    @Override
                    public void onSuccess(String success) {
                        Log.i(TAG, success);
                        Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
                        m_Adapter.loadFriends();
                        add_friend_email_text.clearFocus();
                        add_friend_email_text.setText("");
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(add_friend_email_text.getWindowToken(), 0);
                    }

                    @Override
                    public void onError(String error_tag, String error) {
                        Log.e(TAG, error_tag + ": " + error);
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        add_friend_email_text.clearFocus();
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(add_friend_email_text.getWindowToken(), 0);
                    }
                });
                Log.i(TAG, "friend request email: " + email);
                Log.i(TAG, "users own id: " + person_id);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        m_Adapter = new FriendsAdapter(getContext(), person_id, m_RecyclerView);
    }
}