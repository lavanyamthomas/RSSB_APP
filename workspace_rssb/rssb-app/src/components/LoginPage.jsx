import { useState } from 'react';
import './dashboard.css';

function LoginPage({ onLogin }) {
  const [username, setUsername] = useState('sewadar');
  const [password, setPassword] = useState('Sewadar@123');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    try {
      setIsLoading(true);
      await onLogin(username, password);
    } catch (err) {
      setError(err.message || 'Login failed');
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <main className="screen">
      <section className="panel" aria-label="Login">
        <div className="header-bar">
          <h1>RSSB Sewadar Assistance</h1>
          <div className="header-right">
            <span>Login</span>
          </div>
        </div>
        <div className="detail-wrap">
          <div className="detail-card blue">
            <h2>Sign In</h2>
            <form className="detail-form" onSubmit={handleSubmit}>
              <label>
                Username
                <input value={username} onChange={(event) => setUsername(event.target.value)} required />
              </label>
              <label>
                Password
                <input
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  required
                />
              </label>
              <button type="submit" className="detail-action-btn" disabled={isLoading}>
                {isLoading ? 'Signing In...' : 'Sign In'}
              </button>
              {error ? <p className="detail-feedback">{error}</p> : null}
            </form>
          </div>
        </div>
      </section>
    </main>
  );
}

export default LoginPage;
