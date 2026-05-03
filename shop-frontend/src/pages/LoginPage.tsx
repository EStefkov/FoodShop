import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { account } from '../api/account';
import { useAuth } from '../context/AuthContext';
import { GoogleLoginButton } from '../components/GoogleLoginButton';

const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID as string;

export const LoginPage = () => {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const { setUser } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      const res = await account.login(identifier, password);
      setUser({
        username: res.data.username,
        token: res.data.token,
        loginMethod: 'password',
      });
      navigate('/profile');
    } catch {
      setError('Login failed. Please check your credentials.');
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleLogin}>
        <h2>Login</h2>
        {error && <div className="error">{error}</div>}
        <div>
          <input
            type="text"
            placeholder="Email or Username"
            value={identifier}
            onChange={(e) => setIdentifier(e.target.value)}
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button type="submit">Login</button>
      </form>

      <div className="divider">or</div>

      <GoogleLoginButton clientId={googleClientId} />
    </div>
  );
};
