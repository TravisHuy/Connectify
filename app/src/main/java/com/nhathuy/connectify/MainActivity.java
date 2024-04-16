package com.nhathuy.connectify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.emoji2.bundled.BundledEmojiCompatConfig;
import androidx.emoji2.text.EmojiCompat;
import androidx.room.Query;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.nhathuy.connectify.databinding.ActivityMainBinding;
import com.nhathuy.connectify.fragment.MainFragment;
import com.nhathuy.connectify.interfaces.OnFetchUserListener;
import com.nhathuy.connectify.model.User;
import com.nhathuy.connectify.query.QueryUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityMainBinding binding;
    private String username;
    private String profilePic;
    private int following;
    private int follower;
    private List<String> followingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Home");

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        auth=FirebaseAuth.getInstance();

        checkStoragePermission();

        binding.bottomNavigationLinearView.setCurrentActiveItem(0);
        binding.bottomNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        Intent intent=new Intent(MainActivity.this,AddPostActivity.class);
                        intent.putExtra("username",username);
                        intent.putExtra("profilePic",profilePic);
                        startActivity(intent);
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        Sprite  doubleBounce=new DoubleBounce();
        binding.progressBar.setIndeterminateDrawable(doubleBounce);
        binding.progressBar.setVisibility(View.VISIBLE);

        QueryUtils.fetchUserInfo(auth.getCurrentUser().getDisplayName(),new OnFetchUserListener(){

            @Override
            public void onSuccess(User user) {
                username=user.getUsername();
                profilePic=user.getProfilePic();
                followingList=user.getFollowing_list();
                follower=user.getFollowers();
                following=user.getFollowing();
                for (String following:followingList
                     ) {
                    QueryUtils.fetchPost(MainActivity.this,following);
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {

            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new MainFragment())
                .commit();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        MenuItem mSearch=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) mSearch.getActionView();
        searchView.setOnQueryTextListener();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}