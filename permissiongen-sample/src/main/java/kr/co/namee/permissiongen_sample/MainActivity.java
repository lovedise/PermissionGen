package kr.co.namee.permissiongen_sample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import kr.co.namee.permissiongen.internal.Dlog;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    String[] p = {Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS};
    PermissionGen.requestPermission(this, 100, p);
  }

  @PermissionSuccess(requestCode = 100)
  public void test(){
    Dlog.debug("success");
  }

  @PermissionFail(requestCode = 100)
  public void test2() {
    Dlog.debug("fail");
  }
  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    PermissionGen.requestResult(this, requestCode, permissions, grantResults);
  }
}
