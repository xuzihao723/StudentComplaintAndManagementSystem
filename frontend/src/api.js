import axios from 'axios';

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
  baseURL: apiBaseUrl
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('scfs_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message || error.message || 'Request failed';
    return Promise.reject(new Error(message));
  }
);

export async function login(payload) {
  const { data } = await api.post('/auth/login', payload);
  return data;
}

export async function registerStudent(payload) {
  const { data } = await api.post('/auth/register/student', payload);
  return data;
}

export async function me() {
  const { data } = await api.get('/auth/me');
  return data;
}

export async function getDepartments() {
  const { data } = await api.get('/reference/departments');
  return data;
}

export async function getCategories() {
  const { data } = await api.get('/reference/categories');
  return data;
}

export async function getUsers() {
  const { data } = await api.get('/admin/users');
  return data;
}

export async function createStaffUser(payload) {
  const { data } = await api.post('/admin/users', payload);
  return data;
}

export async function updateUser(id, payload) {
  const { data } = await api.put(`/admin/users/${id}`, payload);
  return data;
}

export async function getNotifications() {
  const { data } = await api.get('/notifications');
  return data;
}

export async function markNotificationRead(id) {
  const { data } = await api.post(`/notifications/${id}/read`);
  return data;
}

export async function getDashboardSummary() {
  const { data } = await api.get('/dashboard/summary');
  return data;
}

export function multipartRequestConfig() {
  return {};
}

export async function submitStudentCase(payload) {
  const form = new FormData();
  form.append('categoryId', payload.categoryId);
  form.append('title', payload.title);
  form.append('description', payload.description);
  form.append('anonymous', payload.anonymous ? 'true' : 'false');
  if (payload.priority) form.append('priority', payload.priority);
  for (const file of payload.files || []) {
    form.append('files', file.raw || file);
  }
  const { data } = await api.post('/student/cases', form, multipartRequestConfig());
  return data;
}

export async function submitPublicCase(payload) {
  const form = new FormData();
  form.append('categoryId', payload.categoryId);
  form.append('title', payload.title);
  form.append('description', payload.description);
  if (payload.publicContactEmail) form.append('publicContactEmail', payload.publicContactEmail);
  if (payload.publicSubmitterType) form.append('publicSubmitterType', payload.publicSubmitterType);
  if (payload.priority) form.append('priority', payload.priority);
  for (const file of payload.files || []) {
    form.append('files', file.raw || file);
  }
  const { data } = await api.post('/public/cases', form, multipartRequestConfig());
  return data;
}

export async function trackPublicCase(payload) {
  const { data } = await api.post('/public/cases/track', payload);
  return data;
}

export async function sendPublicMessage(payload) {
  const { data } = await api.post('/public/cases/track/messages', payload);
  return data;
}

export async function getStudentCases() {
  const { data } = await api.get('/student/cases');
  return data;
}

export async function getStudentCase(id) {
  const { data } = await api.get(`/student/cases/${id}`);
  return data;
}

export async function sendStudentMessage(id, content) {
  const { data } = await api.post(`/student/cases/${id}/messages`, { content });
  return data;
}

export async function requestFollowUp(id, content) {
  const { data } = await api.post(`/student/cases/${id}/follow-up`, { content });
  return data;
}

export async function requestReopen(id, content) {
  const { data } = await api.post(`/student/cases/${id}/reopen`, { content });
  return data;
}

export async function submitSatisfaction(id, payload) {
  const { data } = await api.post(`/student/cases/${id}/satisfaction`, payload);
  return data;
}

export async function searchCases(params) {
  const { data } = await api.get('/cases/search', { params });
  return data;
}

export async function getOverdueCases() {
  const { data } = await api.get('/cases/overdue');
  return data;
}

export async function getCaseDetail(id) {
  const { data } = await api.get(`/cases/${id}`);
  return data;
}

export async function createReminder(id, note) {
  const { data } = await api.post(`/cases/${id}/reminders`, { note });
  return data;
}

export async function getPrivateNotes(id) {
  const { data } = await api.get(`/cases/${id}/private-notes`);
  return data;
}

export async function addPrivateNote(id, payload) {
  const { data } = await api.post(`/cases/${id}/private-notes`, payload);
  return data;
}

export function attachmentDownloadUrl(caseId, attachmentId) {
  return `${apiBaseUrl}/cases/${caseId}/attachments/${attachmentId}/download`;
}

export async function getOfficerCases() {
  const { data } = await api.get('/officer/cases/new');
  return data;
}

export async function getOfficerCase(id) {
  const { data } = await api.get(`/officer/cases/${id}`);
  return data;
}

export async function assignCase(id, payload) {
  const { data } = await api.post(`/officer/cases/${id}/assign`, payload);
  return data;
}

export async function requestCaseInfo(id, content) {
  const { data } = await api.post(`/officer/cases/${id}/request-info`, { content });
  return data;
}

export async function closeCase(id, content) {
  const { data } = await api.post(`/officer/cases/${id}/close`, { content });
  return data;
}

export async function getDepartmentCases() {
  const { data } = await api.get('/department/cases');
  return data;
}

export async function getDepartmentCase(id) {
  const { data } = await api.get(`/department/cases/${id}`);
  return data;
}

export async function updateCaseProgress(id, content) {
  const { data } = await api.post(`/department/cases/${id}/progress`, { content });
  return data;
}

export async function resolveCase(id, content) {
  const { data } = await api.post(`/department/cases/${id}/resolve`, { content });
  return data;
}

export async function getWeeklyReports() {
  const { data } = await api.get('/admin/reports/weekly');
  return data;
}

export async function generateWeeklyReport() {
  const { data } = await api.post('/admin/reports/weekly/generate');
  return data;
}

export async function getAuditLogs() {
  const { data } = await api.get('/admin/audit-logs');
  return data;
}

export async function exportReports() {
  const { data } = await api.post('/admin/reports/export', {}, { responseType: 'blob' });
  return data;
}

export async function forgotPassword(usernameOrEmail) {
  const { data } = await api.post('/auth/forgot-password', { usernameOrEmail });
  return data;
}

export async function resetPassword(payload) {
  const { data } = await api.post('/auth/reset-password', payload);
  return data;
}

export async function verifyEmail(payload) {
  const { data } = await api.post('/auth/verify-email', payload);
  return data;
}

export async function getProfile() {
  const { data } = await api.get('/profile');
  return data;
}

export async function updateProfile(payload) {
  const { data } = await api.put('/profile', payload);
  return data;
}

export async function changePassword(payload) {
  const { data } = await api.post('/profile/password', payload);
  return data;
}

export async function getEmailSettings() {
  const { data } = await api.get('/settings/email');
  return data;
}

export async function updateEmailSettings(payload) {
  const { data } = await api.put('/settings/email', payload);
  return data;
}

export async function sendTestEmail(to) {
  const { data } = await api.post('/settings/email/test', { to });
  return data;
}

export async function createDepartment(payload) {
  const { data } = await api.post('/admin/reference/departments', payload);
  return data;
}

export async function createCategory(payload) {
  const { data } = await api.post('/admin/reference/categories', payload);
  return data;
}

export default api;
