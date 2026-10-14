import { useState } from 'react';
import DashboardCard from './components/DashboardCard';
import ActionDetailPage from './components/ActionDetailPage';
import LoginPage from './components/LoginPage';
import { login, setAuthToken } from './services/rssbApi';

function App() {
  const [selectedAction, setSelectedAction] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(Boolean(localStorage.getItem('rssb_token')));

  async function handleLogin(username, password) {
    const response = await login(username, password);
    setAuthToken(response.token);
    setIsAuthenticated(true);
  }

  function handleLogout() {
    localStorage.removeItem('rssb_token');
    setSelectedAction(null);
    setIsAuthenticated(false);
  }

  if (!isAuthenticated) {
    return <LoginPage onLogin={handleLogin} />;
  }

  if (selectedAction) {
    return <ActionDetailPage action={selectedAction} onBack={() => setSelectedAction(null)} onLogout={handleLogout} />;
  }

  return <DashboardCard onActionSelect={setSelectedAction} onLogout={handleLogout} />;
}

export default App;