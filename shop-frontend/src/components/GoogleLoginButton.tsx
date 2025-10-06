import { GoogleOAuthProvider, GoogleLogin, type CredentialResponse } from "@react-oauth/google";
import { account } from "../api/account";

interface Props {
  clientId: string;
}

export const GoogleLoginButton = ({ clientId }: Props) => {
  const handleSuccess = async (credentialResponse: CredentialResponse) => {
    if (!credentialResponse.credential) return;
    try {
      const res = await account.googleLogin(credentialResponse.credential);
      console.log("Google login success:", res.data);
    } catch (err) {
      console.error("Google login error:", err);
    }
  };

  return (
    <GoogleOAuthProvider clientId={clientId}>
      <GoogleLogin onSuccess={handleSuccess} onError={() => console.error("Google login failed")} />
    </GoogleOAuthProvider>
  );
};
