package com.nttdata.asegawam.androidfido2sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.fido.Fido;
import com.google.android.gms.fido.fido2.Fido2ApiClient;
import com.google.android.gms.fido.fido2.Fido2PendingIntent;
import com.google.android.gms.fido.fido2.api.common.AttestationConveyancePreference;
import com.google.android.gms.fido.fido2.api.common.EC2Algorithm;
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialCreationOptions;
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialParameters;
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialRpEntity;
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialType;
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialUserEntity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Fido2ApiClient mFido2Client;
    private final static int REQUEST_CODE = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d("fido2log", "------ Call MainActivity.onCreate ------");

        mFido2Client = Fido.getFido2ApiClient(this.getApplicationContext());

        // Builder を準備する
        PublicKeyCredentialCreationOptions.Builder pccob = new PublicKeyCredentialCreationOptions.Builder();

        // パラメータをセットしていく
        // Ref https://www.w3.org/TR/webauthn/#dictionary-makecredentialoptions
        pccob.setRp(new PublicKeyCredentialRpEntity("localhost", "MyRpSample", "http://example.com/icon.png"));
        pccob.setUser(new PublicKeyCredentialUserEntity("asegawam".getBytes(), "asegawa minoru", "http://asegawa.com/user.icon", "AsegawaAsDisplayname"));

        pccob.setChallenge("challenge-string-ofwaruiara".getBytes());
        List<PublicKeyCredentialParameters> params = new ArrayList<>();
        params.add(new PublicKeyCredentialParameters(
                PublicKeyCredentialType.PUBLIC_KEY.toString(),
                EC2Algorithm.ES256.getAlgoValue()
        ));
        pccob.setParameters(params);

        //pccob.setAuthenticatorSelection();

        pccob.setAttestationConveyancePreference(AttestationConveyancePreference.NONE);
        pccob.setTimeoutSeconds(30000D);

        // Buildする
        PublicKeyCredentialCreationOptions pcco = pccob.build();

        // create を実行する
        Log.d("fido2log", "----- Start RegistrationIntent -----");
        Task result = mFido2Client.getRegisterIntent(pcco);

        // Listenerで成否で処理を行う
        result.addOnSuccessListener(
                new OnSuccessListener<Fido2PendingIntent>() {
                    @Override
                    public void onSuccess(Fido2PendingIntent fido2PendingIntent) {
                        Log.d("fido2log", "----- onSuccess getRegisterIntent -----");
                        if (fido2PendingIntent.hasPendingIntent()) {
                            // Start a FIDO2 registration request.
                            try {
                                Log.d("fido2log", "----- getParent: " + MainActivity.this.getClass().toString());
                                fido2PendingIntent.launchPendingIntent(MainActivity.this, REQUEST_CODE);

                            } catch (Exception e) {
                                Log.e("fido2log", e.getMessage());
                            }
                        }

                    }
                });

        result.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d("fido2log", "----- onError getRegisterIntent -----");
                        // Fail
                        Log.e("fido2log", e.getMessage());
                    }
                });

        // 結果を取得する？


        Log.d("fido2log", "----- End MainActivity -----");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("fido2log", "----- result: " + resultCode + " " + data.getData().toString());

    }

    ;


    private void register_test() {

        //// SAMPLE DATA ////
        // {'publicKey': {'challenge': b'\x1dO\x1cu\xa0\x92Z\xe9\xc5R\xafEU\xfd\x08\x81\xdbY\xf4\xcc\x15\xab\x9c)\x11\x07yG\x0b\x0e0r', 'pubKeyCredParams': [{'alg': -7, 'type': 'public-key'}], 'attestation': 'none', 'rp': {'name': 'Demo server', 'id': 'localhost'}, 'timeout': 30000, 'user': {'name': 'a_user', 'displayName': 'A. User', 'icon': 'https://example.com/image.png', 'id': b'user_id'}, 'excludeCredentials': []}}
        /////////////////////


        ///////// TEST CODE //////////////
        /*
        try {
            //ByteArrayをCBORへ変換
            CBORObject cborObject = CBORObject.Read();
            Gson gson = new Gson();
            RegisteredCredentialOptions registeredCredentialOptions = gson.fromJson(cborObject.ToJSONString(), RegisteredCredentialOptions.class);
            List<PublicKeyCredentialParameters> PublicKeyCredentialParametersList = new ArrayList<PublicKeyCredentialParameters>();
            for(RegisteredCredentialOptions.PublicKey.PubKeyCredParams pubKeyCredParams:registeredCredentialOptions.publicKey.pubKeyCredParams) {
                //ECDSAを指定しているが変更が必要？
                PublicKeyCredentialParametersList.add(new PublicKeyCredentialParameters(pubKeyCredParams.type, ECDSA.toString()));
                //Integer.toString(registeredCredentialOptions.publicKey.pubKeyCredParams.get(0).alg)));
            }

            //ここでoptionsをセットしている
            MakeCredentialOptions.Builder mcob = new MakeCredentialOptions.Builder();
            mcob.setChallenge(registeredCredentialOptions.publicKey.challenge.getBytes());
            mcob.setParameters(PublicKeyCredentialParametersList);
            mcob.setRp(new PublicKeyCredentialEntity(registeredCredentialOptions.publicKey.rp.id,registeredCredentialOptions.publicKey.rp.name,registeredCredentialOptions.publicKey.user.icon));
            mcob.setTimeoutSeconds(registeredCredentialOptions.publicKey.timeout);
            mcob.setUser(new PublicKeyCredentialUserEntity(registeredCredentialOptions.publicKey.user.id,registeredCredentialOptions.publicKey.user.name,registeredCredentialOptions.publicKey.user.icon,registeredCredentialOptions.publicKey.user.displayName));
            MakeCredentialOptions mco = mcob.build();

        }catch(IOException e){
            Log.e("ase", e.getMessage());
            e.printStackTrace();
        }

        /////////////////////////////////

        */
    }

}
