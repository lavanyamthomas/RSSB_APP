function RefreshIcon() {
  return (
    <svg viewBox="0 0 24 24" className="status-icon" aria-hidden="true">
      <path
        d="M20 6v5h-5M4 18v-5h5"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M6.5 8.2A7 7 0 0119 11M17.5 15.8A7 7 0 015 13"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  );
}

function StatusBar() {
  return (
    <div className="status-wrap" role="status" aria-live="polite">
      <div className="online-pill">
        <span className="dot" />
        <span>Online</span>
      </div>
      <div className="sync-pill">
        <RefreshIcon />
        <span>Records Pending Sync</span>
      </div>
    </div>
  );
}

export default StatusBar;