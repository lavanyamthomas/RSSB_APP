function PowerIcon() {
  return (
    <svg viewBox="0 0 24 24" className="mini-icon" aria-hidden="true">
      <path d="M12 3v8" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" />
      <path
        d="M7.4 5.8A8 8 0 1016.6 5.8"
        fill="none"
        stroke="currentColor"
        strokeWidth="2.2"
        strokeLinecap="round"
      />
    </svg>
  );
}

function HeaderBar({ onLogout }) {
  return (
    <header className="header-bar">
      <h1>RSSB Sewadar Assistance</h1>
      <div className="header-right">
        <span>Sewadar</span>
        <button type="button" className="power-btn" onClick={onLogout} aria-label="Logout">
          <PowerIcon />
        </button>
      </div>
    </header>
  );
}

export default HeaderBar;