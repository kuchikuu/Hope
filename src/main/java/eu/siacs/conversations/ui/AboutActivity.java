package eu.siacs.conversations.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import eu.siacs.conversations.R;
import eu.siacs.conversations.ui.util.SettingsUtils;
import eu.siacs.conversations.utils.ThemeHelper;

import static eu.siacs.conversations.ui.XmppActivity.configureActionBar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onResume(){
        super.onResume();
        SettingsUtils.applyScreenshotPreventionSetting(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeHelper.find(this));

        setContentView(R.layout.activity_about);
        Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(Theme.getThemedActionBar(bar, this));
        getWindow().setStatusBarColor(Theme.getStatusBarColor(this));
        setTitle(getString(R.string.title_activity_about_x, getString(R.string.app_name)));
    }
}
