package app.prprhyt.androidfido2sample.ret;

import java.util.List;

public class RegisteredCredentialOptions {

    public PublicKey publicKey;

    public class PublicKey{

        public double timeout;
        public RP rp;
        public String attestation;
        public User user;
        public List<ExcludeCredentials> excludeCredentials;
        //public byte[] challenge;
        public String challenge;
        public List<PubKeyCredParams> pubKeyCredParams;

        PublicKey(){

        }

        public class ExcludeCredentials{
            String type;
            byte[] id;
            List<String> transports;
        }

        public class PubKeyCredParams{
            public int alg;
            public String type;

            PubKeyCredParams(int alg, String type){
                this.alg = alg;
                this.type = type;
            }
        }

        public class User{
            public String id;
            public String name;
            public String displayName;
            public String icon;

            User(String id, String name, String displayName, String icon){
                this.id = id;
                this.name = name;
                this.displayName = displayName;
                this.icon = icon;
            }
        }

        public class RP{
            public String id;
            public String name;
            RP(String id, String name){
                this.id = id;
                this.name = name;
            }
        }
    }
}
