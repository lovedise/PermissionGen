package kr.co.namee.permissiongen_sample.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import kr.co.namee.permissiongen_sample.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
  public ContactFragment() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_contact, container, false);
    v.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        PermissionGen.with(ContactFragment.this)
            .addRequestCode(100)
            .permissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.WRITE_CONTACTS)
            .request();

        //PermissionGen.needPermission(ContactFragment.this, 100, new String[] {
        //    Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS,
        //    Manifest.permission.WRITE_CONTACTS
        //});
      }
    });
    return v;
  }

  @PermissionSuccess(requestCode = 100)
  public void openContact(){
    Toast.makeText(getActivity(), "Contact permission is granted", Toast.LENGTH_SHORT).show();
  }

  @PermissionFail(requestCode = 100)
  public void failContact() {
    Toast.makeText(getActivity(), "Contact permission is not granted", Toast.LENGTH_SHORT).show();
  }
  /**
   * Callback for the result from requesting permissions. This method
   * is invoked for every call on {@link #requestPermissions(String[], int)}.
   * <p>
   * <strong>Note:</strong> It is possible that the permissions request interaction
   * with the user is interrupted. In this case you will receive empty permissions
   * and results arrays which should be treated as a cancellation.
   * </p>
   *
   * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
   * @param permissions The requested permissions. Never null.
   * @param grantResults The grant results for the corresponding permissions
   * which is either {@link PackageManager#PERMISSION_GRANTED}
   * or {@link PackageManager#PERMISSION_DENIED}. Never null.
   * @see #requestPermissions(String[], int)
   */
  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }

  public static Fragment newInstance() {
    return new ContactFragment();
  }
}
