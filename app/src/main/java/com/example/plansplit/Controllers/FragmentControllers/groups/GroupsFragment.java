package com.example.plansplit.Controllers.FragmentControllers.groups;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.GroupAdapter;
import com.example.plansplit.Controllers.FragmentControllers.addgroups.AddGroupsFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {
    private static final String TAG = "GroupsFragment";
    //private static final Database database = Database.getInstance();
    //private static final Database database;
    //private static final DatabaseReference group_reference  =database.getReference("groups");
    private ArrayList<Groups> groupsArrayList;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private FirebaseDatabase database;
    private DatabaseReference group_ref;
    private GroupAdapter.RecyclerViewClickListener mListener;
    private ImageView add_expense;
    private Button add_new_group;
    private String person_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groups, container, false);

        database = FirebaseDatabase.getInstance();
        group_ref = database.getReference("groups");
        final HomeActivity home = (HomeActivity) getContext();
        person_id = home.getPersonId();


        recyclerView = root.findViewById(R.id.recyclerGroups);
        recyclerView.setHasFixedSize(true);
        setOnClickListener();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        groupsArrayList = new ArrayList<>();

        allGroups();


        groupAdapter = new GroupAdapter(this.getContext(), groupsArrayList, mListener);
        recyclerView.setAdapter(groupAdapter);

        Log.d(TAG, "BURADA");

        add_expense = root.findViewById(R.id.add_expense);
        add_new_group = root.findViewById(R.id.add_new_group);
        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupExpenseFragment expenseFragment = new GroupExpenseFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, expenseFragment);
                fragmentTransaction.commit();
            }
        });

        add_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, addGroupsFragment);
                fragmentTransaction.commit();
            }
        });

        return root;

    }

    private void setOnClickListener() {
        mListener = new GroupAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getParentFragment().getContext(), MyGroupActivity.class);
                intent.putExtra("group_title", groupsArrayList.get(position).getGroup_name());
                intent.putExtra("group_image", groupsArrayList.get(position).getGroup_type());
                startActivity(intent);
            }
        };
    }

    public void allGroups() {
        group_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsArrayList.clear();

                for (final DataSnapshot d : snapshot.getChildren()) {
                    System.out.println(person_id);
                    DataSnapshot personKeysnp = d.child("group_members").getChildren().iterator().next();
                    String personKey = (String) personKeysnp.getValue();
                    System.out.println(personKey);

                    DataSnapshot denem = d.child("group_members");

                    for(DataSnapshot d2 : denem.getChildren()){
                        if(d2.getValue().equals(person_id)){
                            Groups group = d.getValue(Groups.class);
                            groupsArrayList.add(group);
                        }
                    }

                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
