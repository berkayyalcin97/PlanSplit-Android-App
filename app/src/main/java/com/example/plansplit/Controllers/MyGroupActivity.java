package com.example.plansplit.Controllers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

import com.example.plansplit.Controllers.FragmentControllers.addgroups.AddGroupsFragment;
import com.example.plansplit.Controllers.FragmentControllers.mygroup.GroupOperationsFragment;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MyGroupActivity extends AppCompatActivity {
    private static final String TAG = "MyGroupActivity";
    Database database = Database.getInstance();
    private String person_id = "";
    private Friend friend;
    private Groups group;
    boolean ctrlType = false;             //eğer friend'den geliyorsa true, gruptan geliyorsa false
    boolean crtlAddGroup;
    boolean crtlGroupOP = false;
    private final String group_type_option_home = "ev";
    private final String group_type_option_work = "iş";
    private final String group_type_option_trip = "seyahat";
    private final String group_type_option_other = "diğer";
    int homePicture = R.drawable.ic_home_black_radius;
    int workPicture = R.drawable.ic_suitcase_radius;
    int tripPicture = R.drawable.ic_trip_radius;
    int otherPicture = R.drawable.ic_other;



    public void loadActivity(String key) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("navigation", key);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);



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
        final ImageButton menu = findViewById(R.id.mygroup_menuline_button);
        final Button removeGroupBttn = findViewById(R.id.remove_group_button);




        BottomNavigationView navView = findViewById(R.id.nav_view2);
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


        final NavController navController = Navigation.findNavController(this, R.id.fragment_place_mygroup);

        String group_title = "Group title";

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.keySet().contains("group")) {
            navView.getMenu().getItem(2).setChecked(true);
            final String person_key = extras.get("person_id").toString();
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
            System.out.println("güncel title " + group_title);
            System.out.println("grouppp");
            groupOpBttn.setVisibility(View.VISIBLE);
            removeFriendBttn.setVisibility(View.INVISIBLE);
            l.setVisibility(View.INVISIBLE);
            menu.setVisibility(View.VISIBLE);

            // Pop-up menu kontrolleri başlangıcı
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(MyGroupActivity.this, menu);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.mygroup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.mygroup_group_options) {
                                AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
                                System.out.println(group.getGroup_name());
                                Bundle bundle = new Bundle();
                                bundle.putString("person_id", person_key);
                                bundle.putSerializable("group", group);
                                addGroupsFragment.setArguments(bundle);
                                //navController.navigate(R.id.navi_group_options);
                                FrameLayout fl = (FrameLayout) findViewById(R.id.fragment_place_mygroup);
                                fl.removeAllViews();

                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_place_mygroup, addGroupsFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                Toast.makeText(MyGroupActivity.this, "Grup Ayarları Seçildi", Toast.LENGTH_SHORT).show();

                                menu.setVisibility(View.INVISIBLE);

                                String group_admin_id = group.getGroup_members().get(0);
                                if (person_key.equals(group_admin_id)) {
                                    removeGroupBttn.setVisibility(View.VISIBLE);
                                }
                                crtlGroupOP = true;
                                crtlAddGroup = true;
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
            });// Pop-up menu kontrolleri bitişi

            ctrlType = false;
        } else if (extras != null && extras.keySet().contains("friend")) {
            navView.getMenu().getItem(1).setChecked(true);
            Gson gson = new Gson();
            String json = extras.getString("friend");
            friend = gson.fromJson(json, Friend.class);
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


        listBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_todo_list);
                list_titleTv.setVisibility(View.VISIBLE);
                events_titleTv.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                removeGroupBttn.setVisibility(View.INVISIBLE);
                if (!ctrlType)
                    group_op_titletV.setVisibility(View.GONE);
            }
        });

        eventsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_events);
                events_titleTv.setVisibility(View.VISIBLE);
                list_titleTv.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                removeGroupBttn.setVisibility(View.INVISIBLE);
                if (!ctrlType)
                    group_op_titletV.setVisibility(View.GONE);
            }
        });

        if (!ctrlType) {
            groupOpBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupOperationsFragment groupOperationsFragment = new GroupOperationsFragment();
                    final Bundle extras_group = getIntent().getExtras();
                    Gson gson = new Gson();
                    String json = extras_group.getString("group");
                    group = gson.fromJson(json, Groups.class);
                    System.out.println(group.getGroup_members());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", group);
                    groupOperationsFragment.setArguments(bundle);

                    navController.navigate(R.id.navi_operation, bundle);

                    group_op_titletV.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);
                    list_titleTv.setVisibility(View.GONE);
                    menu.setVisibility(View.VISIBLE);
                    removeGroupBttn.setVisibility(View.INVISIBLE);
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
                            onBackPressed();
                        }

                        @Override
                        public void onError(String error_tag, String error) {
                            Log.e(TAG, error_tag + ": " + error);
                        }
                    });
                }
            });
        }

        removeGroupBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForDeleteGroup();
            }
        });


        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                menu.setVisibility(View.VISIBLE);
                removeGroupBttn.setVisibility(View.INVISIBLE);
            }
        });


    }
    public void onBackPressed(){
        if(crtlGroupOP){
           finish();
           startActivity(getIntent());
        }else{
            super.onBackPressed();
        }
    }

    private void showAlertForDeleteGroup() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Grubu silmek istediğinizden emin misiniz?");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.deleteGroup(group, new Database.DatabaseCallBack() {
                    @Override
                    public void onSuccess(String success) {
                        Log.i(TAG, success);
                        Toast.makeText(getBaseContext(), success, Toast.LENGTH_SHORT).show();
                        finish();
                        Toast.makeText(getBaseContext(), success, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error_tag, String error) {
                        Log.e(TAG, error_tag + ": " + error);
                    }
                });
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.create().show();
    }

}

