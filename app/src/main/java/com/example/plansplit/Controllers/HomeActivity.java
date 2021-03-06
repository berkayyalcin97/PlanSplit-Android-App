package com.example.plansplit.Controllers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.plansplit.Controllers.FragmentControllers.AddExpenseFragment;
import com.example.plansplit.Models.Database;
import com.example.plansplit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "HomeActivity";
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Bundle bundle;
    String navigation_key;
    public static  BottomNavigationView navView;
    NavController navController;

    private ActionBarDrawerToggle toggle;
    FirebaseAuth mAuth;
    private static final Database database = Database.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Toolbar toolbar =  findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        toggle =new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_personal, R.id.navigation_friends, R.id.navigation_groups, R.id.navigation_notifications,R.id.navigation_add_expense)

                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                navController.navigate(item.getItemId(), bundle);
                return true;
            }
        });
        navigationView=findViewById(R.id.nav_draw_view);

        //At the first login to firebase, user is with name, email, photo, and google_id registered.
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personId = acct.getId();
            String name = acct.getDisplayName();
            if (name == null){
                name = "No name";
            }
            String email = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            setHeader(personPhoto,name,email);
            String image=personPhoto.toString();
            long date = System.currentTimeMillis();

            database.registerUser(personId, name, email, image, date);
            Log.d(TAG, "user registered with this email: " + email + "\n" + "and this key: " + personId);
        }

        Bundle extras = getIntent().getExtras();
        //For those coming from the group todolist page, data retrieval and forwarding is done.
        if(extras != null && (extras.keySet().contains("group_key_list"))){
            bundle = new Bundle();
            bundle.putString("description", extras.getString("description"));
            bundle.putString("todo_key", extras.getString("todo_key"));
            bundle.putString("group_from_list", extras.getString("group_from_list"));
            bundle.putString("group_key_list", extras.getString("group_key_list"));
            navController.navigate(R.id.navigation_add_expense, bundle);
        }
           //For those coming from the friend todolist page, data retrieval and forwarding is done.
        if(extras != null && (extras.keySet().contains("friend_key_list"))){
            bundle = new Bundle();
            bundle.putString("description", extras.getString("description"));
            bundle.putString("todo_key", extras.getString("todo_key"));
            bundle.putString("friend_from_list", extras.getString("friend_from_list"));
            bundle.putString("friend_key_list", extras.getString("friend_key_list"));
            System.out.println("arkada?? keyi "+extras.getString("friend_key_list"));
            navController.navigate(R.id.navigation_add_expense, bundle);
        }
         //For those coming from the friend and group page, data retrieval and forwarding is done.
        if(extras != null && (extras.keySet().contains("friend") || extras.keySet().contains("group"))) {
            bundle = new Bundle();
            if (extras.keySet().contains("friend")) {
                bundle.putString("friend", extras.getString("friend"));
            } else if (extras.keySet().contains("group")){
                bundle.putString("group", extras.getString("group"));
            }
            navController.navigate(R.id.navigation_add_expense, bundle);
        }else if(extras != null && extras.keySet().contains("navigation")) {
            navigation_key = extras.getString("navigation");
            switch (navigation_key) {
                case "personal":
                    navController.navigate(R.id.navigation_personal);
                    break;
                case "friends":
                    navController.navigate(R.id.navigation_friends);
                    break;
                case "groups":
                    navController.navigate(R.id.navigation_groups);
                    break;
                case "notifications":
                    navController.navigate(R.id.navigation_notifications);
                    break;

            }
        }

        //----------------------------------------------------------------------

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if(id == R.id.navigation_language){
                    showChangeLanguageDialog();
                    loadLocale();
                }

                if (id==R.id.navigation_logout){
                    if (id == R.id.navigation_logout) {
                        Log.d(TAG, "SignOut yap??ld??");
                        signOut();

                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                if (id==R.id.navigation_home){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(R.id.navigation_personal);
                }
                return true;
            }
        });
    }


    private  void showChangeLanguageDialog(){
        final String[] listItems = {"Deutsch","English","T??rk??e"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        mBuilder.setTitle("Choose Language Please");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i == 0){
                    //Deutsch
                    setLocale("de");
                }
                else if(i == 1){
                    //English
                    setLocale("en");
                }
                else if(i == 2){
                    //T??rkce
                    setLocale("tr-rTR");
                }
                dialogInterface.dismiss();
                drawerLayout.close();
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }

        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("LanguageSettings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",language);
        editor.apply();
    }
    //Load language in shared prefences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("LanguageSettings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }

    private void setHeader(Uri personphoto,String name,String email) {
        View header = navigationView.getHeaderView(0);
        ImageView imageView=header.findViewById(R.id.imageViewHeaderProfilPhoto);
        TextView headerpersonname=header.findViewById(R.id.textHeaderPersonName);
        TextView headerpersonmail=header.findViewById(R.id.textHeaderMail);
        Picasso.with(this).load(personphoto).into(imageView);
        headerpersonname.setText(name);
        headerpersonmail.setText(email);
    }

    private void signOut() {
        mAuth.signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.log_out_from_app), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text=adapterView.getItemAtPosition(i).toString();
        AddExpenseFragment.sharemethod=text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

