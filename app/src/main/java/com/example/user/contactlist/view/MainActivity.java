package com.example.user.contactlist.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.user.contactlist.R;
import com.example.user.contactlist.databinding.ActivityMainBinding;
import com.example.user.contactlist.model.ContactRepository;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    public final int REQUEST_CODE = 1;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS))
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
            if(hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS))
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ContactListFragment.newInstance())
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                        .commitNow();
            }
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ContactListFragment.newInstance())
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                    .commitNow();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ContactListFragment.newInstance())
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                    .commitNow();
        }
    }

    private boolean hasPhoneContactsPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            return hasPermission == PackageManager.PERMISSION_GRANTED;
        } else
            return true;
    }
}
