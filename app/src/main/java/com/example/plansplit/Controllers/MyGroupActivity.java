package com.example.plansplit.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plansplit.Controllers.Adapters.GroupAdapter;
import com.example.plansplit.Controllers.FragmentControllers.addgroups.AddGroupsFragment;
import com.example.plansplit.Controllers.FragmentControllers.groups.GroupsFragment;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyGroupActivity extends AppCompatActivity {
    private static final String TAG = "MyGroupActivity";
    Database database ;
    public String person_id = "";
    private Friend friend;
    private Groups group;
    public static BottomNavigationView navView;
    Intent intent;
    boolean ctrlType = false;             //eğer friend'den geliyorsa true, gruptan geliyorsa false
    private String group_type_option_home = "ev";
    private String group_type_option_work = "iş";
    private String group_type_option_trip = "seyahat";
    private String group_type_option_other = "diğer";
    int homePicture = R.drawable.ic_home_black_radius;
    int workPicture = R.drawable.ic_suitcase_radius;
    int tripPicture = R.drawable.ic_trip_radius;
    int otherPicture = R.drawable.ic_other;
    String control_list="control";
    String todolistfriend = null;
    ArrayList<Friend> groupMembersInfos = new ArrayList<>();
    Bundle extras;
    private ImageView add_expense_btn;
    NavController navController;
    private int naviIdPay;

    public void setGroup(Groups group) {
        this.group = group;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public String getType() {
        if (ctrlType) {
            return "friend";
        } else {
            return "group";
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(MyGroupActivity.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mygroup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.mygroup_group_options) {
                    AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_place_mygroup, addGroupsFragment); // Başka aktivitedeki fragment'a erişemiyorum
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    Toast.makeText(MyGroupActivity.this, "Grup Ayarları Seçildi", Toast.LENGTH_SHORT).show();
                }
                if (menuItem.getItemId() == R.id.mygroup_table_export)
                    Toast.makeText(MyGroupActivity.this, "Tablo Olarak Çıkar Seçildi", Toast.LENGTH_SHORT).show();
                if (menuItem.getItemId() == R.id.mygroup_quick_add)
                    Toast.makeText(MyGroupActivity.this, "Hızlı Ekle Seçildi", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }

    public void loadActivity(String key) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("navigation", key);
        startActivity(intent);

    }

    public Friend getFriend() {
        return friend;
    }

    public Groups getGroup() {
        return group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database=new Database(getBaseContext());
        super.onCreate(savedInstanceState);
        person_id=database.getUserId();
        setContentView(R.layout.activity_mygroup);
        intent = new Intent(MyGroupActivity.this, HomeActivity.class);
        TextView groupnameTv = findViewById(R.id.group_title_mygroupTv);
        final TextView list_titleTv = findViewById(R.id.list_buttonTv);
        final TextView events_titleTv = findViewById(R.id.events_buttonTv);
        final TextView group_op_titletV = findViewById(R.id.group_op_buttonTv);
        final TextView remove_txt = findViewById(R.id.remove_friend);
        LinearLayout l = findViewById(R.id.remove_friend_linear);
        ImageView groupPhotoIv = findViewById(R.id.group_pictureIv);
        ImageButton listBttn = findViewById(R.id.task_listButton);
        ImageButton eventsBttn = findViewById(R.id.eventsButton);
        ImageButton groupOpBttn = findViewById(R.id.groupOpButton);
        ImageButton backBttn = findViewById(R.id.mygroup_back_button);
        ImageButton removeFriendBttn = findViewById(R.id.removeFriendButton);
        ImageButton menu = findViewById(R.id.mygroup_menuline_button);
        add_expense_btn = findViewById(R.id.add_expense);



        navView = findViewById(R.id.nav_view2);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                String fragmentKey = null;

                switch (item.getItemId()) {
                    case R.id.navigation_personal:
                        fragmentKey = "personal";
                        break;
                    case R.id.navigation_friends:
                        fragmentKey = "friends";
                        break;
                    case R.id.navigation_groups:
                        fragmentKey = "groups";
                        break;
                    case R.id.navigation_notifications:
                        fragmentKey = "notifications";
                        break;
                }
                loadActivity(fragmentKey);
                return true;
            }


        });


       
        extras = getIntent().getExtras();
        navController = Navigation.findNavController(this, R.id.fragment_place_mygroup);

        String group_title = "Group title";


        if (extras != null && extras.keySet().contains("group")) {
            navView.getMenu().getItem(2).setChecked(true);
            if(extras.keySet().contains("group_back")){
                control_list="group_list";
            }
            Gson gson = new Gson();
            String json = extras.getString("group");
            group = gson.fromJson(json, Groups.class);
            group_title = group.getGroup_name();
            String resid = group.getGroup_type();
            if (resid.equals(group_type_option_home)) {
                groupPhotoIv.setImageResource(homePicture);
            } else if (resid.equals(group_type_option_work)) {
                groupPhotoIv.setImageResource(workPicture);
            } else if (resid.equals(group_type_option_trip)) {
                groupPhotoIv.setImageResource(tripPicture);
            } else if (resid.equals(group_type_option_other)) {
                groupPhotoIv.setImageResource(otherPicture);
            }
            groupOpBttn.setVisibility(View.VISIBLE);
            removeFriendBttn.setVisibility(View.INVISIBLE);
            l.setVisibility(View.INVISIBLE);
            menu.setVisibility(View.VISIBLE);
            ctrlType = false;
        } if (extras != null && extras.keySet().contains("friend")) {
            navView.getMenu().getItem(1).setChecked(true);
            if(extras.keySet().contains("friend_back")){
                control_list="friend_list";
                System.out.println("buraya kadar geldin lan");
            }
            Gson gson = new Gson();
            String json = extras.getString("friend");
            friend = gson.fromJson(json, Friend.class);
            todolistfriend = friend.getKey();
            System.out.println(todolistfriend);
            if (extras.keySet().contains("person_id")) {
                person_id = extras.getString("person_id");
            }
            // groupPhotoIv.setImageResource(friend.getPerson_image());
            Picasso.with(getApplicationContext()).load(friend.getPerson_image()).into(groupPhotoIv);
            group_title = friend.getName();
            System.out.println("friend" + friend.getName());
            groupOpBttn.setVisibility(View.INVISIBLE);
            removeFriendBttn.setVisibility(View.VISIBLE);
            l.setVisibility(View.VISIBLE);
            menu.setVisibility(View.INVISIBLE);
            ctrlType = true;
        }

        list_titleTv.setVisibility(View.GONE);
        events_titleTv.setVisibility(View.VISIBLE);
        groupnameTv.setText(group_title);
        group_op_titletV.setVisibility(View.GONE);
        remove_txt.setVisibility(View.GONE);

        if(extras != null && extras.keySet().contains("friend_to_list")){
            navView.getMenu().getItem(1).setChecked(true);
            control_list="friend_list";
            groupOpBttn.setVisibility(View.INVISIBLE);
            removeFriendBttn.setVisibility(View.VISIBLE);
            l.setVisibility(View.VISIBLE);
            menu.setVisibility(View.INVISIBLE);
            Bundle bundlelistfriends = new Bundle();
            Gson gson = new Gson();
            String json = extras.getString("friend_to_list");
            friend = gson.fromJson(json, Friend.class);
            setFriend(friend);
            ctrlType=true;
            Picasso.with(getApplicationContext()).load(friend.getPerson_image()).into(groupPhotoIv);
            group_title = friend.getName();
            groupnameTv.setText(group_title);
            System.out.println("arkadaş keyi: "+friend.getKey() +" personidsi "+database.getUserId());
            bundlelistfriends.putString("friend_key", friend.getKey());
            bundlelistfriends.putString("person_key", database.getUserId());
            navController.navigate(R.id.navi_todo_list, bundlelistfriends);
            add_expense_btn.setVisibility(View.GONE);
            list_titleTv.setVisibility(View.VISIBLE);
            events_titleTv.setVisibility(View.GONE);

        }

        if(extras != null && extras.keySet().contains("group_to_list")){
            navView.getMenu().getItem(2).setChecked(true);
            control_list="group_list";
            Bundle bundlelistgroup = new Bundle();
            Gson gson = new Gson();
            String json = extras.getString("group_to_list");
            group = gson.fromJson(json, Groups.class);
            setGroup(group);
            ctrlType=false;
            group_title = group.getGroup_name();
            groupnameTv.setText(group_title);
            String resid = group.getGroup_type();
            if (resid.equals(group_type_option_home)) {
                groupPhotoIv.setImageResource(homePicture);
            } else if (resid.equals(group_type_option_work)) {
                groupPhotoIv.setImageResource(workPicture);
            } else if (resid.equals(group_type_option_trip)) {
                groupPhotoIv.setImageResource(tripPicture);
            } else if (resid.equals(group_type_option_other)) {
                groupPhotoIv.setImageResource(otherPicture);
            }
            bundlelistgroup.putString("group_title", group.getGroupKey());
            navController.navigate(R.id.navi_todo_list, bundlelistgroup);
            add_expense_btn.setVisibility(View.GONE);
            list_titleTv.setVisibility(View.VISIBLE);
            events_titleTv.setVisibility(View.GONE);


        }

        listBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      if (!ctrlType){
                        group_op_titletV.setVisibility(View.GONE);
                }

                if (extras != null && extras.keySet().contains("group")) {
                    Bundle bundlelistgroup = new Bundle();
                    Gson gson = new Gson();
                    String json = extras.getString("group");
                    group = gson.fromJson(json, Groups.class);
                    bundlelistgroup.putString("group_title", group.getGroupKey());
                    navController.navigate(R.id.navi_todo_list, bundlelistgroup);
                    add_expense_btn.setVisibility(View.GONE);
                    list_titleTv.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);

                }
                if (extras != null && extras.keySet().contains("friend")) {

                    Bundle bundlelistfriends = new Bundle();
                    bundlelistfriends.putString("friend_key", todolistfriend);
                    bundlelistfriends.putString("person_key", person_id);
                    navController.navigate(R.id.navi_todo_list, bundlelistfriends);
                    add_expense_btn.setVisibility(View.GONE);
                    list_titleTv.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);
                }
               else{
                  if (control_list.equals("friend_list")) {
                      Bundle bundlelistfriends = new Bundle();
                      bundlelistfriends.putString("friend_key", friend.getKey());
                      bundlelistfriends.putString("person_key", database.getUserId());
                      navController.navigate(R.id.navi_todo_list, bundlelistfriends);
                      add_expense_btn.setVisibility(View.GONE);
                      list_titleTv.setVisibility(View.VISIBLE);
                      events_titleTv.setVisibility(View.GONE);
                  }

                    if ((control_list.equals("group_list"))) {
                        Bundle bundlelistgroup = new Bundle();
                        bundlelistgroup.putString("group_title", group.getGroupKey());
                        navController.navigate(R.id.navi_todo_list, bundlelistgroup);
                        add_expense_btn.setVisibility(View.GONE);
                        list_titleTv.setVisibility(View.VISIBLE);
                        events_titleTv.setVisibility(View.GONE);

                    }
                }
            }

        });

        eventsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("event butonuna basıldı");
                navController.navigate(R.id.navi_events);
                events_titleTv.setVisibility(View.VISIBLE);
                list_titleTv.setVisibility(View.GONE);
                add_expense_btn.setVisibility(View.VISIBLE);
                if (!ctrlType)
                    group_op_titletV.setVisibility(View.GONE);
            }
        });

        if (!ctrlType) {
            groupOpBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.navi_operation);
                    group_op_titletV.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);
                    add_expense_btn.setVisibility(View.GONE);
                    list_titleTv.setVisibility(View.GONE);
                }
            });
        } else {
            removeFriendBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.removeFriend(person_id, friend.getKey(), new Database.DatabaseCallBack() {
                        @Override
                        public void onSuccess(String success) {
                            Log.i(TAG, success);
                            Toast.makeText(getBaseContext(), "Arkadaş silindi", Toast.LENGTH_SHORT).show();
                           loadActivity("friends");
                        }

                        @Override
                        public void onError(String error_tag, String error) {
                            Log.e(TAG, error_tag + ": " + error);
                        }
                    });
                }
            });
        }


        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        add_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                if (ctrlType == false) {
                    String json = gson.toJson(group);
                    intent.putExtra("group", json);
                } else {
                    String json = gson.toJson(friend);
                    intent.putExtra("friend", json);
                }
                startActivity(intent);

            }

        });

    }
    private final Database.GetMemberInfoCallBack databaseCallBack = new Database.GetMemberInfoCallBack() {
        @Override
        public void onGetMemberInfoRetrieveSuccess(ArrayList<Friend> members) {
            Bundle bundle = new Bundle();
            if(members.size()==1){
                members.get(0).setFriendshipsKey(friend.getFriendshipsKey());
            }else{
                for (Friend member : members){
                    member.setFriendshipsKey(group.getKey());
                }
            }

            bundle.putString("membersInfos", new Gson().toJson(members));
            navController.navigate(naviIdPay, bundle);
            add_expense_btn.setVisibility(View.GONE);

        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);

        }
    };


    public void setNaviPay(int id){
        naviIdPay = id;
        ArrayList<Friend> members = new ArrayList<>();
        if(!ctrlType) {
            database.getGroupMembersInfo(group.getGroup_members(), members, databaseCallBack);
        }else{
            ArrayList<String> f = new ArrayList<>();
            f.add(friend.getKey());
            database.getGroupMembersInfo(f, members, databaseCallBack);
        }
    }

    public void setNavController(int i){
        navController.navigate(i);
        add_expense_btn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(control_list.equals("friend_list")){
            loadActivity("friends");
            finish();
        }
        if(control_list.equals("group_list")) {
            loadActivity("groups");
            finish();
        }
        else{
            super.onBackPressed();
            finish();
        }

    }
}