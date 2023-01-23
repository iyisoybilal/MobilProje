package com.example.Cicekcim.Views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.Cicekcim.Fragments.HomePageFragment;
import com.example.Cicekcim.Fragments.MessageFragment;
import com.example.Cicekcim.R;
import com.example.Cicekcim.Adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addFragment(new HomePageFragment(),"Anasayfa");
        viewPagerAdapter.addFragment(new MessageFragment(),"Mesajlar");
        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    public void onBackPressed() {
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            finishAndRemoveTask();

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Uygulamayı kapatmak istediğine emin misin?").setPositiveButton("Evet", dialogClickListener)
                        .setNegativeButton("Hayır", dialogClickListener).show();
                break;
            case 1:
                tabLayout.getTabAt(0).select();
                break;
        }
    }
}