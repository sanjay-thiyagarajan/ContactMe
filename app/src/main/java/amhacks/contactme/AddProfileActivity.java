package amhacks.contactme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddProfileActivity extends AppCompatActivity {

    private EditText FullNameET,DesignET,OrgET,PhoneET;
    private Button NextButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    String currentUserID;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = mAuth.getCurrentUser().getUid();

        FullNameET = (EditText) findViewById(R.id.add_profile_fullname_et);
        DesignET = (EditText) findViewById(R.id.add_profile_design_et);
        OrgET = (EditText) findViewById(R.id.add_profile_org_et);
        PhoneET = (EditText) findViewById(R.id.add_profile_phone_et);
        NextButton = (Button) findViewById(R.id.profile_next_button);
        progressDialog = new ProgressDialog(this);

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname, designation, organisation, phone;
                fullname = FullNameET.getText().toString();
                designation = DesignET.getText().toString();
                organisation = OrgET.getText().toString();
                phone = PhoneET.getText().toString();

                if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(designation) ||
                        TextUtils.isEmpty(organisation) || TextUtils.isEmpty(phone))
                {
                    Toast.makeText(AddProfileActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("We are building your profile");
                    progressDialog.show();
                    HashMap hashMap = new HashMap();
                    hashMap.put("fullname",fullname);
                    hashMap.put("designation",designation);
                    hashMap.put("organisation",organisation);
                    hashMap.put("phone",phone);
                    usersRef.child(currentUserID).updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Intent dashIntent = new Intent(AddProfileActivity.this,DashboardActivity.class);
                                        dashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(dashIntent);
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(AddProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }
}