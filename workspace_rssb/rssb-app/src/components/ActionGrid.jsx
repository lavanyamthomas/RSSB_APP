import ActionCard from './ActionCard';

function ActionGrid({ actions, onActionSelect }) {
  return (
    <div className="action-grid">
      {actions.map((action) => (
        <ActionCard
          key={action.id}
          label={action.label}
          tone={action.tone}
          icon={action.icon}
          onClick={() => onActionSelect(action)}
        />
      ))}
    </div>
  );
}

export default ActionGrid;