import { useState } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { account } from "./api/account";
import { GoogleLoginButton } from "./components/GoogleLoginButton";
import "./App.css";

const queryClient = new QueryClient();

// ✅ правилният начин да вземеш от .env:
const googleAuthentication = import.meta.env.VITE_GOOGLE_CLIENT_ID as string;

function App() {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await account.login(identifier, password);
      console.log("Logged in:", response.data);
    } catch (err) {
      setError("Login failed. Please check your credentials.");
      console.error("Login error:", err);
    }
  };

  return (
    <QueryClientProvider client={queryClient}>
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

        {/* ✅ подаваме clientId от .env */}
        <GoogleLoginButton clientId={googleAuthentication} />
      </div>
    </QueryClientProvider>
  );
}

export default App;
