import { useState, useRef, useEffect } from 'react';
import HeaderBar from './HeaderBar';
import {
  createRegistration,
  getCompletedRecords,
  searchPersons,
  transmitWalkieMessage,
  getParkingLocations,
  createPersonRegistration,
  getPersonRegistrations,
  updatePersonRegistration,
  uploadPhoto,
  getPhotoUrl,
} from '../services/rssbApi';
import './dashboard.css';

function ZoomablePhoto({ src, alt, className }) {
  const [isOpen, setIsOpen] = useState(false);
  const [zoom, setZoom] = useState(1);

  function openModal() {
    setIsOpen(true);
    setZoom(1);
  }

  function closeModal() {
    setIsOpen(false);
  }

  function zoomIn() {
    setZoom((prev) => Math.min(3, prev + 0.25));
  }

  function zoomOut() {
    setZoom((prev) => Math.max(0.5, prev - 0.25));
  }

  function resetZoom() {
    setZoom(1);
  }

  return (
    <>
      <button type="button" className="zoom-photo-btn" onClick={openModal} aria-label="Open image viewer">
        <img src={src} alt={alt} className={className} />
      </button>

      {isOpen && (
        <div className="photo-modal-overlay" role="dialog" aria-modal="true" onClick={closeModal}>
          <div className="photo-modal-content" onClick={(event) => event.stopPropagation()}>
            <div className="photo-modal-toolbar">
              <button type="button" className="detail-muted-btn" onClick={zoomOut}>-</button>
              <button type="button" className="detail-muted-btn" onClick={zoomIn}>+</button>
              <button type="button" className="detail-muted-btn" onClick={resetZoom}>Reset</button>
              <button type="button" className="detail-muted-btn" onClick={closeModal}>Close</button>
            </div>
            <div className="photo-modal-stage">
              <img
                src={src}
                alt={alt}
                className="photo-modal-image"
                style={{ transform: `scale(${zoom})` }}
              />
            </div>
          </div>
        </div>
      )}
    </>
  );
}

function formatDateTime(value) {
  return value ? new Date(value).toLocaleString() : 'N/A';
}

function normalizeStatus(value) {
  if (!value) return 'PENDING';
  if (value === 'SYNCED') return 'COMPLETE';
  if (value === 'FAILED') return 'SEARCHING';
  if (value === 'PEDING') return 'PENDING';
  return value;
}

function normalizeUserType(value) {
  if (!value) return 'VISITOR';
  const normalized = String(value).toUpperCase();
  if (normalized === 'USER' || normalized === 'SEWADAR') return 'SEWADAR';
  if (normalized === 'VISITOR') return 'VISITOR';
  return 'VISITOR';
}

function buildActivityLogs(person) {
  const logs = [];

  if (person.registrationTime) {
    logs.push({
      time: person.registrationTime,
      action: 'Registered user',
      by: person.registeredByUsername || 'System',
    });
  }

  if (person.syncStatusUpdatedAt) {
    logs.push({
      time: person.syncStatusUpdatedAt,
      action: `Status changed to ${normalizeStatus(person.syncStatus)}`,
      by: person.completedByUsername || person.registeredByUsername || 'User',
    });
  }

  if (person.commentsUpdatedAt) {
    logs.push({
      time: person.commentsUpdatedAt,
      action: 'Comments updated',
      by: person.completedByUsername || person.registeredByUsername || 'User',
    });
  }

  if (person.completionTime) {
    logs.push({
      time: person.completionTime,
      action: 'Marked as completed',
      by: person.completedByUsername || 'User',
    });
  }

  return logs.sort((a, b) => new Date(b.time) - new Date(a.time));
}

function PersonDetailModal({ person, onClose, onUpdated }) {
  const [parkingLocations, setParkingLocations] = useState([]);
  const [personName, setPersonName] = useState(person.personName || '');
  const [phoneNumber, setPhoneNumber] = useState(person.phoneNumber || '');
  const [attendantName, setAttendantName] = useState(person.attendantName || '');
  const [vehicleNumber, setVehicleNumber] = useState(person.vehicleNumber || '');
  const [parkingLocationId, setParkingLocationId] = useState(person.parkingLocationId || '');
  const [localDeviceRecordId, setLocalDeviceRecordId] = useState(person.localDeviceRecordId || '');
  const [userType, setUserType] = useState(normalizeUserType(person.userType));
  const [syncStatus, setSyncStatus] = useState(normalizeStatus(person.syncStatus));
  const [completedFlag, setCompletedFlag] = useState(person.completedFlag || false);
  const [comments, setComments] = useState(person.comments || '');
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const activityLogs = buildActivityLogs(person);

  useEffect(() => {
    getParkingLocations()
      .then((data) => setParkingLocations(data || []))
      .catch(() => setParkingLocations([]));
  }, []);

  async function handleSave() {
    if (!personName.trim()) {
      setMessage('Person name is required.');
      return;
    }
    if (!phoneNumber.trim()) {
      setMessage('Phone number is required.');
      return;
    }

    try {
      setSaving(true);
      setMessage('');
      const updated = await updatePersonRegistration(person.registrationId, {
        localDeviceRecordId: localDeviceRecordId.trim(),
        personName: personName.trim(),
        phoneNumber: phoneNumber.trim(),
        attendantName: attendantName.trim() || null,
        vehicleNumber: vehicleNumber.trim() || null,
        parkingLocationId: parkingLocationId ? Number(parkingLocationId) : null,
        userType,
        syncStatus,
        completedFlag,
        comments,
      });
      setMessage('Saved successfully.');
      if (onUpdated) onUpdated(updated);
    } catch (err) {
      setMessage(err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="photo-modal-overlay" role="dialog" aria-modal="true" onClick={onClose}>
      <div className="person-detail-modal" onClick={(e) => e.stopPropagation()}>
        <div className="person-detail-modal-header">
          <h3 style={{ margin: 0 }}>{person.personName}</h3>
          <button type="button" className="detail-muted-btn" onClick={onClose} aria-label="Close">✕</button>
        </div>

        <div className="person-detail-modal-body">
          {person.photoId && (
            <div className="person-detail-modal-photo">
              <ZoomablePhoto
                src={getPhotoUrl(person.photoId)}
                alt={person.personName}
                className="person-detail-full-photo"
              />
            </div>
          )}

          <table className="person-detail-table">
            <tbody>
              <tr><th>ID</th><td>{person.registrationId}</td></tr>
              <tr><th>User Type</th><td>{normalizeUserType(person.userType)}</td></tr>
              <tr><th>Completed At</th><td>{formatDateTime(person.completionTime)}</td></tr>
              <tr><th>Registered By</th><td>{person.registeredByUsername || 'N/A'}</td></tr>
              <tr><th>Completed By</th><td>{person.completedByUsername || 'N/A'}</td></tr>
            </tbody>
          </table>

          <div className="person-detail-modal-edit">
            <label>
              Person Name
              <input value={personName} onChange={(e) => setPersonName(e.target.value)} type="text" />
            </label>
            <label>
              Phone Number
              <input value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} type="tel" />
            </label>
            <label>
              Attendant Name
              <input value={attendantName} onChange={(e) => setAttendantName(e.target.value)} type="text" />
            </label>
            <label>
              Vehicle Number
              <input value={vehicleNumber} onChange={(e) => setVehicleNumber(e.target.value)} type="text" />
            </label>
            <label>
              Parking Location
              <select value={parkingLocationId} onChange={(e) => setParkingLocationId(e.target.value)}>
                <option value="">Select parking location</option>
                {parkingLocations.map((location) => (
                  <option key={location.id} value={location.id}>
                    {location.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Local Device Record ID
              <input value={localDeviceRecordId} onChange={(e) => setLocalDeviceRecordId(e.target.value)} type="text" />
            </label>
            <label>
              User Type
              <select value={userType} onChange={(e) => setUserType(e.target.value)}>
                <option value="VISITOR">VISITOR</option>
                <option value="SEWADAR">SEWADAR</option>
              </select>
            </label>
            <label>
              Status
              <select value={syncStatus} onChange={(e) => setSyncStatus(e.target.value)}>
                <option value="PENDING">PENDING</option>
                <option value="SEARCHING">SEARCHING</option>
                <option value="COMPLETE">COMPLETE</option>
              </select>
            </label>

            <label>
              Comments
              <textarea
                value={comments}
                onChange={(e) => setComments(e.target.value)}
                rows={3}
                placeholder="Add comments…"
                style={{ resize: 'vertical' }}
              />
            </label>
            <p className="person-comment-history">
              Last comment update: {formatDateTime(person.commentsUpdatedAt)}
            </p>
            <button type="button" className="detail-action-btn" onClick={handleSave} disabled={saving}>
              {saving ? 'Saving…' : 'Save Changes'}
            </button>
            {message && <p className="detail-feedback" style={{ color: message.startsWith('Saved') ? '#4caf50' : undefined }}>{message}</p>}
          </div>

          <div className="person-activity-log">
            <h4>User Action Log</h4>
            {activityLogs.length === 0 ? (
              <p className="person-log-empty">No actions yet.</p>
            ) : (
              <table className="person-activity-table" aria-label="User action logs">
                <thead>
                  <tr>
                    <th>Action</th>
                    <th>Time</th>
                    <th>By</th>
                  </tr>
                </thead>
                <tbody>
                  {activityLogs.map((log, idx) => (
                    <tr key={`${log.action}-${log.time}-${idx}`}>
                      <td>{log.action}</td>
                      <td>{formatDateTime(log.time)}</td>
                      <td>{log.by}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function PersonListItem({ item, onOpenDetails }) {
  return (
    <div
      role="listitem"
      className="detail-list-item detail-list-item-clickable"
      onClick={() => onOpenDetails(item)}
      title="Click to view / edit"
    >
      <div className="detail-item-media">
        {item.photoId ? (
          <ZoomablePhoto
            src={getPhotoUrl(item.photoId)}
            alt={item.personName}
            className="detail-item-photo"
          />
        ) : (
          <div className="detail-item-photo detail-item-placeholder" aria-hidden="true">No Photo</div>
        )}
      </div>
      <div className="detail-item-body">
        <strong>{item.personName}</strong>
        <span>
          #{item.registrationId} | {item.phoneNumber}
          {item.parkingLocationName ? ` | ${item.parkingLocationName}` : ''}
          {item.vehicleNumber ? ` | ${item.vehicleNumber}` : ''}
          {item.userType ? ` | ${normalizeUserType(item.userType)}` : ''}
          {' | '}
          <em>{normalizeStatus(item.syncStatus)}</em>
          {item.completedFlag ? ' ✓ Completed' : ''}
        </span>
        <button
          type="button"
          className="detail-muted-btn detail-item-action"
          onClick={(event) => {
            event.stopPropagation();
            onOpenDetails(item);
          }}
        >
          Open Details
        </button>
      </div>
    </div>
  );
}

function RegistrationForm() {
  const [statusMessage, setStatusMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [parkingLocations, setParkingLocations] = useState([]);
  const [registrations, setRegistrations] = useState([]);
  const [totalRegistrations, setTotalRegistrations] = useState(null);
  const [listError, setListError] = useState('');
  const [regPage, setRegPage] = useState(0);
  const [selectedPerson, setSelectedPerson] = useState(null);
  const PAGE_SIZE = 5;

  // Photo upload state
  const [photoFile, setPhotoFile] = useState(null);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [photoId, setPhotoId] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadMessage, setUploadMessage] = useState('');

  const formRef = useRef(null);
  const fileInputRef = useRef(null);

  useEffect(() => {
    getParkingLocations()
      .then(setParkingLocations)
      .catch(() => setParkingLocations([]));
    loadRegistrations();
  }, []);

  async function loadRegistrations() {
    try {
      const data = await getPersonRegistrations(0, 1000);
      setRegistrations(data.content || []);
      setTotalRegistrations(data.totalElements ?? (data.content || []).length);
    } catch (error) {
      setListError(error.message);
    }
  }

  function handlePhotoSelect(event) {
    const file = event.target.files[0];
    if (!file) return;

    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
    if (!allowedTypes.includes(file.type)) {
      setUploadMessage('Only JPEG, PNG, WEBP or GIF images are allowed.');
      return;
    }
    if (file.size > 10 * 1024 * 1024) {
      setUploadMessage('File size must be under 10 MB.');
      return;
    }

    setPhotoFile(file);
    setPhotoId(null);
    setUploadMessage('');
    setPhotoPreview(URL.createObjectURL(file));
  }

  async function handlePhotoUpload() {
    if (!photoFile) {
      setUploadMessage('Please select a photo first.');
      return;
    }
    try {
      setIsUploading(true);
      setUploadMessage('Uploading…');
      const result = await uploadPhoto(photoFile);
      setPhotoId(result.photoId);
      setUploadMessage(`Photo uploaded (ID: ${result.photoId})`);
    } catch (error) {
      setUploadMessage(error.message);
      setPhotoId(null);
    } finally {
      setIsUploading(false);
    }
  }

  function handleRemovePhoto() {
    setPhotoFile(null);
    setPhotoPreview(null);
    setPhotoId(null);
    setUploadMessage('');
    if (fileInputRef.current) fileInputRef.current.value = '';
  }

  async function handleSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    let resolvedPhotoId = photoId;

    if (photoFile && !resolvedPhotoId) {
      try {
        setIsUploading(true);
        setUploadMessage('Uploading photo before save...');
        const uploadResult = await uploadPhoto(photoFile);
        resolvedPhotoId = uploadResult.photoId;
        setPhotoId(uploadResult.photoId);
      } catch (error) {
        setStatusMessage(error.message);
        return;
      } finally {
        setIsUploading(false);
      }
    }

    const payload = {
      personName: formData.get('personName')?.toString().trim(),
      phoneNumber: formData.get('phoneNumber')?.toString().trim(),
      attendantName: formData.get('attendantName')?.toString().trim() || null,
      vehicleNumber: formData.get('vehicleNumber')?.toString().trim() || null,
      userType: normalizeUserType(formData.get('userType')?.toString().trim() || 'VISITOR'),
      parkingLocationId: formData.get('parkingLocationId') ? Number(formData.get('parkingLocationId')) : null,
      photoId: resolvedPhotoId || null,
      localDeviceRecordId: formData.get('localDeviceRecordId')?.toString().trim() || null,
    };

    try {
      setIsSaving(true);
      const result = await createPersonRegistration(payload);
      setStatusMessage(`Registration #${result.registrationId} created for ${result.personName}`);
      if (formRef.current) formRef.current.reset();
      handleRemovePhoto();
      loadRegistrations();
    } catch (error) {
      setStatusMessage(error.message);
    } finally {
      setIsSaving(false);
    }
  }

  function handlePersonUpdated(updated) {
    setRegistrations((prev) => prev.map((r) => (r.registrationId === updated.registrationId ? updated : r)));
    setSelectedPerson(updated);
  }

  return (
    <div>
      <form ref={formRef} className="detail-form" onSubmit={handleSubmit}>
        <label>
          Person Name *
          <input type="text" name="personName" placeholder="Enter person name" required />
        </label>
        <label>
          Phone Number *
          <input type="tel" name="phoneNumber" placeholder="Enter phone number" required />
        </label>
        <label>
          Attendant Name
          <input type="text" name="attendantName" placeholder="Enter attendant name" />
        </label>
        <label>
          Vehicle Number
          <input type="text" name="vehicleNumber" placeholder="Enter vehicle number" />
        </label>
        <label>
          User Type
          <select name="userType" defaultValue="VISITOR">
            <option value="VISITOR">VISITOR</option>
            <option value="SEWADAR">SEWADAR</option>
          </select>
        </label>
        <label>
          Parking Location
          <select name="parkingLocationId" defaultValue="">
            <option value="">— Select parking location —</option>
            {parkingLocations.map((loc) => (
              <option key={loc.id} value={loc.id}>
                {loc.name}
              </option>
            ))}
          </select>
        </label>

        {/* Photo Upload */}
        <div className="photo-upload-section">
          <span className="photo-upload-label">Photo</span>
          {photoPreview && (
            <div className="photo-preview-wrap">
              <img
                src={photoPreview}
                alt="Preview"
                className="photo-preview-img"
              />
              <button
                type="button"
                className="photo-remove-btn"
                onClick={handleRemovePhoto}
                aria-label="Remove photo"
              >
                ✕
              </button>
            </div>
          )}
          <div className="photo-upload-controls">
            <input
              ref={fileInputRef}
              type="file"
              accept="image/jpeg,image/png,image/webp,image/gif"
              onChange={handlePhotoSelect}
              className="photo-file-input"
              id="photoFileInput"
            />
            <label htmlFor="photoFileInput" className="detail-muted-btn" style={{ cursor: 'pointer' }}>
              {photoFile ? 'Change Photo' : 'Choose Photo'}
            </label>
            {photoFile && !photoId && (
              <button
                type="button"
                className="detail-action-btn"
                onClick={handlePhotoUpload}
                disabled={isUploading}
                style={{ marginLeft: '0.5rem' }}
              >
                {isUploading ? 'Uploading…' : 'Upload'}
              </button>
            )}
          </div>
          {uploadMessage && (
            <p className={`detail-feedback ${photoId ? 'success' : ''}`}>{uploadMessage}</p>
          )}
          {photoId && (
            <p style={{ fontSize: '0.8rem', color: '#4caf50', margin: '0.25rem 0 0' }}>
              ✓ Photo ready (ID: {photoId})
            </p>
          )}
        </div>

        <label>
          Local Device Record ID
          <input type="text" name="localDeviceRecordId" placeholder="Offline temp ID (optional)" />
        </label>
        <button type="submit" className="detail-action-btn" disabled={isSaving}>
          {isSaving ? 'Saving…' : 'Save Registration'}
        </button>
        {statusMessage ? <p className="detail-feedback">{statusMessage}</p> : null}
      </form>

      <h3 style={{ marginTop: '1.5rem' }}>
        Recent Registrations
        {totalRegistrations !== null && (
          <span
            style={{
              marginLeft: '0.75rem',
              fontSize: '0.88rem',
              fontWeight: 700,
              color: '#0b5394',
              backgroundColor: '#e8f1ff',
              border: '1px solid #b8d4ff',
              borderRadius: '999px',
              padding: '0.2rem 0.65rem',
              display: 'inline-block',
              verticalAlign: 'middle',
            }}
          >
            Total: {totalRegistrations}
          </span>
        )}
      </h3>
      {listError ? <p className="detail-feedback">{listError}</p> : null}
      <div className="detail-list" role="list" aria-label="Person registrations">
        {registrations.length === 0 && <p className="detail-feedback">No registrations yet.</p>}
        {registrations.slice(regPage * PAGE_SIZE, (regPage + 1) * PAGE_SIZE).map((item) => (
          <PersonListItem
            key={item.registrationId}
            item={item}
            onOpenDetails={setSelectedPerson}
          />
        ))}
      </div>
      {registrations.length > PAGE_SIZE && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginTop: '0.75rem' }}>
          <button
            type="button"
            className="detail-muted-btn"
            onClick={() => setRegPage((p) => Math.max(0, p - 1))}
            disabled={regPage === 0}
          >
            ← Prev
          </button>
          <span style={{ fontSize: '0.85rem', color: '#666' }}>
            Page {regPage + 1} of {Math.ceil(registrations.length / PAGE_SIZE)}
          </span>
          <button
            type="button"
            className="detail-muted-btn"
            onClick={() => setRegPage((p) => Math.min(Math.ceil(registrations.length / PAGE_SIZE) - 1, p + 1))}
            disabled={regPage >= Math.ceil(registrations.length / PAGE_SIZE) - 1}
          >
            Next →
          </button>
        </div>
      )}

      {selectedPerson && (
        <PersonDetailModal
          person={selectedPerson}
          onClose={() => setSelectedPerson(null)}
          onUpdated={handlePersonUpdated}
        />
      )}
    </div>
  );
}

function SearchPerson() {
  const [query, setQuery] = useState('');
  const [location, setLocation] = useState('all');
  const [results, setResults] = useState([]);
  const [statusMessage, setStatusMessage] = useState('Search for a person to view results.');
  const [selectedPerson, setSelectedPerson] = useState(null);

  async function handleSearch() {
    try {
      const data = await searchPersons(query, location);
      setResults(data.results || []);
      setStatusMessage(data.results?.length ? '' : 'No person found.');
    } catch (error) {
      setStatusMessage(error.message);
      setResults([]);
    }
  }

  function handlePersonUpdated(updated) {
    setResults((prev) => prev.map((r) => r.registrationId === updated.registrationId ? updated : r));
    setSelectedPerson(updated);
  }

  return (
    <section className="detail-form" aria-label="Search person">
      <label>
        Person Name or Phone or Vehicle
        <input
          type="text"
          placeholder="Search by person name, phone number, or vehicle number"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
        />
      </label>
      <label>
        Location
        <select value={location} onChange={(event) => setLocation(event.target.value)}>
          <option value="all">All locations</option>
          <option value="north-gate">North Gate</option>
          <option value="main-hall">Main Hall</option>
          <option value="langar">Langar Area</option>
        </select>
      </label>
      <button type="button" className="detail-action-btn" onClick={handleSearch}>
        Search
      </button>
      <div className="detail-list" role="list" aria-label="Search results">
        {results.map((item) => (
          <PersonListItem
            key={item.registrationId}
            item={item}
            onOpenDetails={setSelectedPerson}
          />
        ))}
      </div>
      {statusMessage ? <p className="detail-feedback">{statusMessage}</p> : null}

      {selectedPerson && (
        <PersonDetailModal
          person={selectedPerson}
          onClose={() => setSelectedPerson(null)}
          onUpdated={handlePersonUpdated}
        />
      )}
    </section>
  );
}

function CompletedRecords() {
  const [date, setDate] = useState('');
  const [records, setRecords] = useState([]);
  const [statusMessage, setStatusMessage] = useState('Load records to view completed items.');

  async function loadRecords() {
    try {
      const data = await getCompletedRecords(date);
      setRecords(data.records || []);
      setStatusMessage(data.records?.length ? '' : 'No completed records found.');
    } catch (error) {
      setStatusMessage(error.message);
      setRecords([]);
    }
  }

  return (
    <section className="detail-form" aria-label="Completed records">
      <label>
        Filter by date
        <input type="date" value={date} onChange={(event) => setDate(event.target.value)} />
      </label>
      <button type="button" className="detail-action-btn" onClick={loadRecords}>
        Load Records
      </button>
      <div className="detail-list" role="list" aria-label="Completed records list">
        {records.map((item) => (
          <div key={item.recordId} role="listitem" className="detail-list-item">
            <strong>{item.title}</strong>
            <span>
              {item.recordId} | {item.completedAt}
            </span>
          </div>
        ))}
      </div>
      <button type="button" className="detail-action-btn">
        Export Records
      </button>
      {statusMessage ? <p className="detail-feedback">{statusMessage}</p> : null}
    </section>
  );
}

function WalkieTalkie() {
  const [channel, setChannel] = useState('channel-1');
  const [message, setMessage] = useState('');
  const [statusMessage, setStatusMessage] = useState('');

  async function handleTransmit() {
    if (!message.trim()) {
      setStatusMessage('Please enter a message before transmitting.');
      return;
    }

    try {
      const response = await transmitWalkieMessage({ channel, message });
      setStatusMessage(`Transmitted to ${response.channel} at ${response.timestamp}`);
      setMessage('');
    } catch (error) {
      setStatusMessage(error.message);
    }
  }

  return (
    <section className="detail-form" aria-label="Walkie talkie controls">
      <label>
        Channel
        <select value={channel} onChange={(event) => setChannel(event.target.value)}>
          <option value="channel-1">Channel 1 - Security</option>
          <option value="channel-2">Channel 2 - Registration</option>
          <option value="channel-3">Channel 3 - Transport</option>
        </select>
      </label>
      <label>
        Message
        <textarea
          rows="3"
          placeholder="Type your message"
          value={message}
          onChange={(event) => setMessage(event.target.value)}
        />
      </label>
      <div className="detail-inline-buttons">
        <button type="button" className="detail-action-btn" onClick={handleTransmit}>
          Hold to Talk
        </button>
        <button type="button" className="detail-muted-btn">
          Mute
        </button>
      </div>
      {statusMessage ? <p className="detail-feedback">{statusMessage}</p> : null}
    </section>
  );
}

function ActionContent({ action }) {
  if (action.id === 'reg') return <RegistrationForm />;
  if (action.id === 'search') return <SearchPerson />;
  if (action.id === 'records') return <CompletedRecords />;
  if (action.id === 'talk') return <WalkieTalkie />;
  return <p>Select an option from the dashboard.</p>;
}

function ActionDetailPage({ action, onBack, onLogout }) {
  return (
    <main className="screen">
      <section className="panel" aria-label={`${action.label} Page`}>
        <HeaderBar onLogout={onLogout} />
        <div className="detail-wrap">
          <button type="button" className="back-btn" onClick={onBack}>
            Back
          </button>
          <div className={`detail-card ${action.tone}`}>
            <h2>{action.label}</h2>
            <ActionContent action={action} />
          </div>
        </div>
      </section>
    </main>
  );
}

export default ActionDetailPage;