function PlusIcon() {
  return (
    <svg viewBox="0 0 24 24" className="action-icon" aria-hidden="true">
      <circle cx="12" cy="12" r="9" fill="rgba(255,255,255,0.95)" />
      <path d="M12 8v8M8 12h8" fill="none" stroke="#2d76db" strokeWidth="2.2" strokeLinecap="round" />
    </svg>
  );
}

function SearchIcon() {
  return (
    <svg viewBox="0 0 24 24" className="action-icon" aria-hidden="true">
      <circle cx="10.5" cy="10.5" r="5.2" fill="none" stroke="white" strokeWidth="2.2" />
      <path d="M14.4 14.4l4.2 4.2" fill="none" stroke="white" strokeWidth="2.2" strokeLinecap="round" />
    </svg>
  );
}

function CheckIcon() {
  return (
    <svg viewBox="0 0 24 24" className="action-icon" aria-hidden="true">
      <circle cx="12" cy="12" r="9" fill="white" opacity="0.94" />
      <path
        d="M8 12.2l2.8 2.7L16.2 9"
        fill="none"
        stroke="#f08b33"
        strokeWidth="2.2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function MicIcon() {
  return (
    <svg viewBox="0 0 24 24" className="action-icon" aria-hidden="true">
      <rect x="9" y="5" width="6" height="10" rx="3" fill="white" />
      <path d="M7 11.5a5 5 0 0010 0" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" />
      <path d="M12 16.5v2.5M9.5 19h5" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}

function ActionIcon({ type }) {
  if (type === 'plus') return <PlusIcon />;
  if (type === 'search') return <SearchIcon />;
  if (type === 'check') return <CheckIcon />;
  return <MicIcon />;
}

function ActionCard({ label, tone, icon, onClick }) {
  return (
    <button className={`action-card ${tone}`} type="button" onClick={onClick} aria-label={label}>
      <div className="icon-wrap">
        <ActionIcon type={icon} />
      </div>
      <span>{label}</span>
    </button>
  );
}

export default ActionCard;