package kr.co.namee.permissiongen_sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lovedise.permissiongen.annotation.PermissionFail;
import com.lovedise.permissiongen.annotation.PermissionSuccess;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen_sample.contacts.ContactActivity;

public class MainActivity extends AppCompatActivity {
  @Bind(R.id.btn_contact) Button btnContact;
  @Bind(R.id.btn_camera) Button btnCamera;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @OnClick({R.id.btn_contact, R.id.btn_camera})
  public void open(View view){
    switch (view.getId()){
      case R.id.btn_contact:
        PermissionGen.with(MainActivity.this)
            .addRequestCode(100)
            .permissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.WRITE_CONTACTS)
            .request();
        break;
      case R.id.btn_camera:
        PermissionGen.needPermission(this, 200, Manifest.permission.CAMERA);
        break;
    }
  }

  @PermissionSuccess(requestCode = 100)
  public void test(){
    startActivity(new Intent(this, ContactActivity.class));
  }

  @PermissionFail(requestCode = 100)
  public void test2() {
    Dlog.debug("contact fail");
  }

  @PermissionSuccess(requestCode = 200)
  public void openCamera(){
    Toast.makeText(this, "open camera success", Toast.LENGTH_SHORT).show();
  }

  @PermissionFail(requestCode = 200)
  public void failOpenCamera(){
    Toast.makeText(this, "Camera permission is not granted", Toast.LENGTH_SHORT).show();
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }
}
