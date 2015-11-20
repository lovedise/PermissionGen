package kr.co.namee.permissiongen_sample.contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import kr.co.namee.permissiongen_sample.R;

public class ContactActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contact);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, ContactFragment.newInstance())
        .commitAllowingStateLoss();
  }
}
