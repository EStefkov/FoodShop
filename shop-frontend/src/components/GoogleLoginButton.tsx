import { GoogleOAuthProvider, GoogleLogin, type CredentialResponse } from '@react-oauth/google';
import { useNavigate } from 'react-router-dom';
import { account } from '../api/account';
import { useAuth } from '../context/AuthContext';

interface Props {
  clientId: string;
}

export const GoogleLoginButton = ({ clientId }: Props) => {
  const { setUser } = useAuth();
  const navigate = useNavigate();
  const {refreshUser} = useAuth();

  const handleSuccess = async (credentialResponse: CredentialResponse) => {
    if (!credentialResponse.credential) return;

    try {
      await account.googleLogin(credentialResponse.credential);
      
      await refreshUser();

      navigate('/profile');

    } catch (err) {
      console.error('Google login error:', err);
    }
  };

  return (
    <GoogleOAuthProvider clientId={clientId}>
      <GoogleLogin
        onSuccess={handleSuccess}
        onError={() => console.error('Google login failed')}
      />
    </GoogleOAuthProvider>
  );
};
