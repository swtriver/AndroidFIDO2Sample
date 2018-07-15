package app.prprhyt.androidfido2sample;

import android.Manifest;
import android.app.Application;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.fido.Fido;
import com.google.android.gms.fido.fido2.*;
import com.google.android.gms.fido.fido2.api.common.BrowserMakeCredentialOptions;
import com.google.android.gms.fido.fido2.api.common.MakeCredentialOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import app.prprhyt.androidfido2sample.ret.RegisteredCredentialOptions;


public class MainActivity extends AppCompatActivity {

    private Fido2ApiClient mFido2Client;
    private RegisterCommunicator mRegisterCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFido2Client = Fido.getFido2ApiClient(this.getApplicationContext());

        mRegisterCommunicator = new RegisterCommunicator(this);
        mRegisterCommunicator.register_begin("", "", "", "", new RegisterCommunicator.FIDO2RegistorBeginListener() {
            @Override
            public void onResponse(MakeCredentialOptions credentialOptions) {
                Task<Fido2PendingIntent> result = mFido2Client.getRegisterIntent(credentialOptions);
                result.addOnSuccessListener(new OnSuccessListener<Fido2PendingIntent>() {
                    @Override
                    public void onSuccess(Fido2PendingIntent fido2PendingIntent) {
                        Log.d("a","success");
                    }
                });
                result.addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                // Fail
                                Log.d("a","failed");
                            }
                        });
                Log.d("FIDO2RegistorBeginListener","ok");
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

        //byte[] testByte = {0x00};
        //MakeCredentialOptions credentialOptions;
        //credentialOptions = MakeCredentialOptions.deserializeFromBytes(testByte);

    }
}
