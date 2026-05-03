import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { account, type UserDTO } from '../api/account';
import { useAuth } from '../context/AuthContext';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import './ProfilePage.css';

const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID as string;

export const ProfilePage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user) return;
    account.getMe()
      .then((res) => setProfile(res.data))
      .catch(() => setError('Failed to load profile.'))
      .finally(() => setLoading(false));
  }, [user]);

  const handleLogout = async () => {
    await account.logout();
    logout();
    navigate('/login');
  };

  const handleLinkGoogle = async (credential: string) => {
    try {
      await account.linkGoogle(credential);
      const updated = await account.getMe();
      setProfile(updated.data);
    } catch {
      alert('Failed to link Google account.');
    }
  };

  if (loading) return <div className="profile-loading">Loading profile…</div>;
  if (error) return <div className="profile-error">{error}</div>;
  if (!profile) return null;

  const fullName = [profile.firstName, profile.lastName].filter(Boolean).join(' ') || profile.username;
  const initials = fullName.split(' ').map((n) => n[0]).join('').toUpperCase().slice(0, 2);

  return (
    <div className="profile-page">
      <header className="profile-header">
        <span className="profile-brand">foodshop</span>
        <div className="profile-header-right">
          <span className="profile-login-badge">
            {user?.loginMethod === 'google' ? '🔵 Google account' : '🔑 Password account'}
          </span>
          <button className="profile-logout-btn" onClick={handleLogout}>
            Log out
          </button>
        </div>
      </header>

      <main className="profile-main">
        <section className="profile-hero">
          <div className="profile-avatar">
            {profile.profilePicture ? (
              <img src={profile.profilePicture} alt={fullName} />
            ) : (
              <span className="profile-initials">{initials}</span>
            )}
          </div>
          <div className="profile-hero-text">
            <h1 className="profile-name">{fullName}</h1>
            <p className="profile-username">@{profile.username}</p>
            <div className="profile-roles">
              {profile.role?.map((r) => (
                <span key={r} className="profile-role-tag">
                  {r.replace('ROLE_', '')}
                </span>
              ))}
              {profile.googleLinked && (
                <span className="profile-role-tag profile-role-tag--google">
                  ✓ Google linked
                </span>
              )}
            </div>
          </div>
        </section>

        {/* Link Google — only shown for password users who haven't linked yet */}
        {!profile.googleLinked && user?.loginMethod === 'password' && (
          <section className="profile-link-section">
            <h2 className="profile-section-title">Connect accounts</h2>
            <p className="profile-link-description">
              Link your Google account so you can sign in with either method.
            </p>
            <GoogleOAuthProvider clientId={googleClientId}>
              <GoogleLogin
                onSuccess={(cred) => {
                  if (cred.credential) handleLinkGoogle(cred.credential);
                }}
                onError={() => alert('Google linking failed')}
              />
            </GoogleOAuthProvider>
          </section>
        )}

        <section className="profile-details">
          <h2 className="profile-section-title">Account details</h2>
          <div className="profile-grid">
            <ProfileField label="Email" value={profile.email} />
            <ProfileField label="Phone" value={profile.number} />
            <ProfileField label="Address" value={profile.address} />
            <ProfileField label="City" value={profile.city} />
            <ProfileField label="Country" value={profile.country} />
            <ProfileField label="Postal code" value={profile.postalCode} />
          </div>
        </section>
      </main>
    </div>
  );
};

const ProfileField = ({ label, value }: { label: string; value?: string }) => (
  <div className="profile-field">
    <span className="profile-field-label">{label}</span>
    <span className="profile-field-value">
      {value || <em className="profile-field-empty">Not set</em>}
    </span>
  </div>
);