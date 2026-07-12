import {
  createContext,
  useContext,
  useState,
  useEffect,
  type ReactNode,
} from 'react';

import { account, type UserDTO } from '../api/account';

interface AuthUser {
  username: string;
  loginMethod: 'password' | 'google';
  role?: string[];
}

interface AuthContextType {
  user: AuthUser | null;
  loading: boolean;
  setUser: (user: AuthUser | null) => void;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);

  /**
   * 🔐 Restore session from backend (JWT cookie)
   */
  const refreshUser = async () => {
    try {
      const res = await account.getMe();

      const data: UserDTO = res.data;

      setUser({
        username: data.username,
        loginMethod: data.googleLinked ? 'google' : 'password',
        role: data.role,
      });
    } catch (err) {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Run once on app start
   */
  useEffect(() => {
    refreshUser();
  }, []);

  /**
   * Logout → clear state + optionally backend call already handled elsewhere
   */
  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        setUser,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};