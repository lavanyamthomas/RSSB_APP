const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

function getAuthToken() {
  return localStorage.getItem('rssb_token');
}

export function setAuthToken(token) {
  localStorage.setItem('rssb_token', token);
}

function extractErrorMessage(payload) {
  if (!payload) return '';
  if (typeof payload === 'string') return payload;
  if (typeof payload.message === 'string' && payload.message.trim()) return payload.message.trim();
  if (typeof payload.error === 'string' && payload.error.trim()) return payload.error.trim();
  return '';
}

function getStatusFallback(status) {
  switch (status) {
    case 400:
      return 'Please check the entered details and try again.';
    case 401:
      return 'Your session has expired. Please login again.';
    case 403:
      return 'You do not have permission to perform this action.';
    case 404:
      return 'Requested record was not found.';
    case 409:
      return 'This record already exists. Please use different details.';
    case 500:
      return 'Server error occurred. Please try again in a moment.';
    default:
      return `Request failed with status ${status}`;
  }
}

function buildRequestError(response, payload) {
  const backendMessage = extractErrorMessage(payload);
  const fallback = getStatusFallback(response.status);
  return new Error(backendMessage || fallback);
}

async function request(path, options = {}) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.headers || {}),
      },
    });

    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : null;

    if (!response.ok) {
      throw buildRequestError(response, payload);
    }

    return payload;
  } catch (error) {
    if (error.name === 'TypeError') {
      throw new Error('Cannot connect to the server. Please ensure backend is running on http://localhost:8080.');
    }
    throw error;
  }
}

export function createRegistration(data) {
  return request('/registrations', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function login(username, password) {
  return request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export function registerUser(data) {
  return request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function searchPersons(query, location) {
  const params = new URLSearchParams();
  if (query) params.set('q', query);
  if (location && location !== 'all') params.set('location', location);
  params.set('size', '500');

  return request(`/person-registrations?${params.toString()}`)
    .then((page) => ({
      results: page?.content || [],
      page: page?.number,
      size: page?.size,
      totalElements: page?.totalElements,
      totalPages: page?.totalPages,
    }));
}

export function getCompletedRecords(date) {
  const params = new URLSearchParams();
  if (date) {
    params.set('dateFrom', date);
    params.set('dateTo', date);
  }

  const query = params.toString();
  return request(`/records/completed${query ? `?${query}` : ''}`);
}

export function transmitWalkieMessage(data) {
  return request('/walkie/transmit', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function getParkingLocations() {
  return request('/person-registrations/parking-locations');
}

export function createPersonRegistration(data) {
  return request('/person-registrations', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function getPersonRegistrations(page = 0, size = 20) {
  return request(`/person-registrations?page=${page}&size=${size}`);
}

export function getPersonRegistrationById(id) {
  return request(`/person-registrations/${id}`);
}

export function completePersonRegistration(id) {
  return request(`/person-registrations/${id}/complete`, { method: 'PATCH' });
}

export function updatePersonRegistration(id, data) {
  return request(`/person-registrations/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function uploadPhoto(file) {
  const token = localStorage.getItem('rssb_token');
  const formData = new FormData();
  formData.append('file', file);

  return fetch(`${API_BASE_URL}/photos/upload`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: formData,
  })
    .then(async (response) => {
      const contentType = response.headers.get('content-type') || '';
      const payload = contentType.includes('application/json') ? await response.json() : null;
      if (!response.ok) {
        throw buildRequestError(response, payload);
      }
      return payload;
    })
    .catch((error) => {
      if (error.name === 'TypeError') {
        throw new Error('Photo upload failed because the server is unreachable. Please start backend on http://localhost:8080.');
      }
      throw error;
    });
}

export function getPhotoUrl(photoId) {
  return `${API_BASE_URL}/photos/${photoId}/file`;
}
