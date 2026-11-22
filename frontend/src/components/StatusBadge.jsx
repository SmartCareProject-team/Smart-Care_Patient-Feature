const STYLES = {
  EN_ATTENTE: { label: 'Pending', background: '#fefcbf', color: '#744210' },
  CONFIRME: { label: 'Confirmed', background: '#c6f6d5', color: '#22543d' },
  ANNULE: { label: 'Cancelled', background: '#fed7d7', color: '#822727' },
  TERMINE: { label: 'Completed', background: '#e2e8f0', color: '#2d3748' },
};

export default function StatusBadge({ status }) {
  const s = STYLES[status] || { label: status, background: '#e2e8f0', color: '#2d3748' };
  return (
    <span
      style={{
        display: 'inline-block',
        padding: '2px 10px',
        borderRadius: '12px',
        fontSize: '13px',
        fontWeight: 600,
        background: s.background,
        color: s.color,
      }}
    >
      {s.label}
    </span>
  );
}