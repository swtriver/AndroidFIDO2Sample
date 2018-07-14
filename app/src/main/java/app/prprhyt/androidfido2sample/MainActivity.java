package app.prprhyt.androidfido2sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.fido.Fido;
import com.google.android.gms.fido.fido2.*;
import com.google.android.gms.fido.fido2.api.common.MakeCredentialOptions;


public class MainActivity extends AppCompatActivity {

    private Fido2ApiClient mFido2Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        byte[] testByte = {0x00};
        mFido2Client = Fido.getFido2ApiClient(this);
        MakeCredentialOptions credentialOptions;
        credentialOptions = MakeCredentialOptions.deserializeFromBytes(testByte);

        mFido2Client.getRegisterIntent(credentialOptions);
    }
}
