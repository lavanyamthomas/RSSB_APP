import HeaderBar from './HeaderBar';
import StatusBar from './StatusBar';
import ActionGrid from './ActionGrid';
import './dashboard.css';

function DashboardCard({ onActionSelect, onLogout }) {
  const actions = [
    { id: 'reg', label: 'New Registration', tone: 'blue', icon: 'plus' },
    { id: 'search', label: 'Search Person', tone: 'green', icon: 'search' },
    { id: 'records', label: 'Completed Records', tone: 'orange', icon: 'check' },
    { id: 'talk', label: 'Walkie-Talkie', tone: 'red', icon: 'mic' },
  ];

  return (
    <main className="screen">
      <section className="panel" aria-label="RSSB Sewadar Assistance">
        <HeaderBar onLogout={onLogout} />
        <StatusBar />
        <ActionGrid actions={actions} onActionSelect={onActionSelect} />
      </section>
    </main>
  );
}

export default DashboardCard;