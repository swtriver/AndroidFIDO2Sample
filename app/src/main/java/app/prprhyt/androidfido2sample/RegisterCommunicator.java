package app.prprhyt.androidfido2sample;

import android.content.Context;

import com.google.android.gms.fido.fido2.api.common.MakeCredentialOptions;
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialRequestOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class RegisterCommunicator {
    private final FIDO2RegistorService service;

    public RegisterCommunicator(Context context) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://localhost:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(FIDO2RegistorService.class);
    }

    public void register_begin(String id, String name, String dispName, String iconUri, final FIDO2RegistorBeginListener listener){
        service.register_begin(id, name, dispName, iconUri).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    ByteArrayInputStream bais = new ByteArrayInputStream(response.body().bytes());
                    List<DataItem> dataItems = new CborDecoder(bais).decode();
                    for(DataItem dataItem : dataItems) {
                        // process data item
                    }
                    //listener.onResponse(MakeCredentialOptions.deserializeFromBytes(test));
                }catch (CborException e){
                    listener.onFailure(e);
                }catch (IOException e){
                    listener.onFailure(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onFailure(t);
            }
        });

    }

    public interface FIDO2RegistorBeginListener {
        void onResponse(MakeCredentialOptions credentialOptions);

        void onFailure(Throwable throwable);
    }

    public void register_complete(PublicKeyCredentialRequestOptions requestOptions, final FIDO2RegistorCompleteListener listener){
        byte[] requestOptionsBytes = requestOptions.serializeToBytes();
        service.register_complete(requestOptionsBytes).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    listener.onResponse(PublicKeyCredentialRequestOptions.deserializeFromBytes(response.body().bytes()));
                }catch (IOException e){
                    listener.onFailure(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onFailure(t);
            }
        });

    }

    public interface FIDO2RegistorCompleteListener {
        void onResponse(PublicKeyCredentialRequestOptions requestOptions);

        void onFailure(Throwable throwable);
    }

    private interface FIDO2RegistorService {

        @POST("api/register/begin")
        Call<ResponseBody> register_begin(@Query("id") String id, @Query("name") String name,
                                          @Query("displayName") String displayName, @Query("icon") String iconUri);

        @POST("api/register/complete")
        Call<ResponseBody> register_complete(@Body byte[] requestOptions);
    }


    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
