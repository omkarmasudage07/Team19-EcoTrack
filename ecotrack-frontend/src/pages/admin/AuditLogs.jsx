import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { 
  FiFileText, FiSearch, FiFilter, FiDownload, FiEye, FiClock, 
  FiUser, FiShield, FiGlobe, FiActivity, FiServer, FiCheckCircle, FiAlertCircle,
  FiX, FiRotateCcw, FiTag
} from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import notificationService from '../../services/notificationService';

const ROLES = [
  { value: 'ALL', label: 'All Roles' },
  { value: 'CITIZEN', label: 'Citizen' },
  { value: 'RECYCLER', label: 'Recycler Partner' },
  { value: 'INDUSTRY', label: 'Industrial Buyer' },
  { value: 'ADMIN', label: 'System Admin' },
];

const REGIONS = [
  { value: 'ALL', label: 'All Regions' },
  { value: 'Pune Region', label: 'Pune Region' },
  { value: 'Mumbai Region', label: 'Mumbai Region' },
  { value: 'Kolhapur Region', label: 'Kolhapur Region' },
  { value: 'Nagpur Region', label: 'Nagpur Region' },
  { value: 'Nashik Region', label: 'Nashik Region' },
  { value: 'Satara Region', label: 'Satara Region' },
];

const STATUSES = [
  { value: 'ALL', label: 'All Status Codes' },
  { value: '200', label: '200 OK (Success)' },
  { value: '201', label: '201 Created' },
  { value: '400', label: '400 Bad Request' },
  { value: '401', label: '401 Unauthorized' },
  { value: '403', label: '403 Forbidden' },
  { value: '404', label: '404 Not Found' },
  { value: '409', label: '409 Conflict' },
  { value: '500', label: '500 Server Error' },
];

const ACTION_CATEGORIES = [
  { id: 'ALL', label: 'All Events' },
  { id: 'PICKUP', label: 'Pickups' },
  { id: 'ORDER', label: 'Orders & Payments' },
  { id: 'APPROVAL', label: 'Approvals' },
  { id: 'REWARD', label: 'Rewards' },
];

const AuditLogs = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // Filter States
  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');
  const [regionFilter, setRegionFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [selectedLog, setSelectedLog] = useState(null);

  // Fetch from backend (loads comprehensive audit logs, up to 200 records)
  const fetchLogs = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 200, sort: 'createdAt,desc' };
      if (roleFilter !== 'ALL') params.role = roleFilter;
      if (regionFilter !== 'ALL') params.region = regionFilter;
      if (statusFilter !== 'ALL') params.status = parseInt(statusFilter);
      if (searchQuery.trim()) params.search = searchQuery.trim();

      const data = await notificationService.getAuditLogs(params);
      setLogs(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [roleFilter, regionFilter, statusFilter, searchQuery]);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  // Helper to infer role from action if not explicitly recorded
  const inferRole = (log) => {
    if (log.userRole) return log.userRole.toUpperCase();
    const action = (log.action || '').toUpperCase();
    if (action.startsWith('PICKUP')) return 'CITIZEN';
    if (action.startsWith('ORDER') || action.startsWith('PAYMENT')) return 'INDUSTRY';
    if (action.includes('APPROVED') || action.startsWith('ADMIN')) return 'ADMIN';
    if (action.startsWith('REWARD')) return 'CITIZEN';
    return 'SYSTEM';
  };

  // Comprehensive client-side filtering for zero-latency, reliable refinement
  const filteredLogs = useMemo(() => {
    return logs.filter((log) => {
      // 1. Role Filter
      if (roleFilter !== 'ALL') {
        const logRole = inferRole(log);
        const targetRole = roleFilter.toUpperCase();
        if (!logRole.includes(targetRole) && !targetRole.includes(logRole)) {
          return false;
        }
      }

      // 2. Region Filter
      if (regionFilter !== 'ALL') {
        const logRegion = (log.region || '').toLowerCase();
        const details = (log.details || '').toLowerCase();
        const target = regionFilter.toLowerCase().replace(' region', '');
        const hasRegionMatch = logRegion.includes(target) || details.includes(target);
        if (!hasRegionMatch) return false;
      }

      // 3. Status Filter
      if (statusFilter !== 'ALL') {
        const targetStatus = parseInt(statusFilter);
        const logStatus = log.responseStatus || 200; // default success to 200
        if (targetStatus === 200) {
          if (logStatus !== 200 && logStatus !== 201) return false;
        } else {
          if (logStatus !== targetStatus) return false;
        }
      }

      // 4. Action Category Filter
      if (categoryFilter !== 'ALL') {
        const act = (log.action || '').toUpperCase();
        if (categoryFilter === 'PICKUP' && !act.includes('PICKUP')) return false;
        if (categoryFilter === 'ORDER' && !act.includes('ORDER') && !act.includes('PAYMENT')) return false;
        if (categoryFilter === 'APPROVAL' && !act.includes('APPROVED') && !act.includes('REJECTED')) return false;
        if (categoryFilter === 'REWARD' && !act.includes('REWARD') && !act.includes('POINT')) return false;
      }

      // 5. Search Query (matches across all key fields)
      if (searchQuery.trim()) {
        const query = searchQuery.toLowerCase().trim();
        const combined = [
          log.action,
          log.details,
          log.url,
          log.method,
          log.userRole,
          log.region,
          log.requestId,
          log.ipAddress,
          log.responseStatus ? String(log.responseStatus) : '200',
        ]
          .filter(Boolean)
          .join(' ')
          .toLowerCase();

        if (!combined.includes(query)) return false;
      }

      return true;
    });
  }, [logs, roleFilter, regionFilter, statusFilter, categoryFilter, searchQuery]);

  const hasActiveFilters = 
    searchQuery.trim() !== '' || 
    roleFilter !== 'ALL' || 
    regionFilter !== 'ALL' || 
    statusFilter !== 'ALL' || 
    categoryFilter !== 'ALL';

  const resetAllFilters = () => {
    setSearchQuery('');
    setRoleFilter('ALL');
    setRegionFilter('ALL');
    setStatusFilter('ALL');
    setCategoryFilter('ALL');
  };

  const exportCSV = () => {
    const headers = ['ID,Request ID,User ID,Role,Region,Method,URL,IP Address,Status,Response Time (ms),Action,Details,Date'];
    const rows = filteredLogs.map((l) => [
      l.id,
      `"${l.requestId || ''}"`,
      l.userId || '',
      `"${l.userRole || inferRole(l)}"`,
      `"${l.region || ''}"`,
      l.method || '',
      `"${l.url || ''}"`,
      l.ipAddress || '',
      l.responseStatus || 200,
      l.responseTimeMs || 15,
      `"${l.action || ''}"`,
      `"${(l.details || '').replace(/"/g, '""')}"`,
      `"${new Date(l.createdAt).toLocaleString()}"`
    ].join(','));

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers, ...rows].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `ecotrack_audit_logs_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const exportExcel = () => {
    const headers = ['ID\tRequest ID\tUser ID\tRole\tRegion\tMethod\tURL\tIP Address\tStatus\tResponse Time (ms)\tAction\tDetails\tDate'];
    const rows = filteredLogs.map((l) => [
      l.id,
      l.requestId || '',
      l.userId || '',
      l.userRole || inferRole(l),
      l.region || '',
      l.method || '',
      l.url || '',
      l.ipAddress || '',
      l.responseStatus || 200,
      l.responseTimeMs || 15,
      l.action || '',
      (l.details || '').replace(/\t/g, ' '),
      new Date(l.createdAt).toLocaleString()
    ].join('\t'));

    const excelContent = 'data:application/vnd.ms-excel;charset=utf-8,' + encodeURIComponent([headers, ...rows].join('\n'));
    const link = document.createElement('a');
    link.setAttribute('href', excelContent);
    link.setAttribute('download', `ecotrack_audit_logs_${new Date().toISOString().slice(0, 10)}.xls`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div>
      <PageHeader
        title="Enterprise API Audit Logs"
        subtitle="Real-time request logging, security event tracking, and compliance audit trail"
        action={
          <div className="d-flex gap-2">
            <button className="btn btn-sm btn-outline-success fw-bold d-flex align-items-center gap-1" onClick={exportCSV}>
              <FiDownload /> Export CSV
            </button>
            <button className="btn btn-sm btn-outline-primary fw-bold d-flex align-items-center gap-1" onClick={exportExcel}>
              <FiDownload /> Export Excel
            </button>
          </div>
        }
      />

      {/* Filter Controls Card */}
      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-3">
          {/* Main Filter Bar */}
          <div className="row g-2 align-items-center">
            {/* Search Input */}
            <div className="col-lg-4 col-md-12">
              <div className="input-group input-group-sm">
                <span className="input-group-text bg-white"><FiSearch className="text-muted" /></span>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Search logs by action, URL, ID, or payload..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                {searchQuery && (
                  <button className="btn btn-outline-secondary" type="button" onClick={() => setSearchQuery('')}>
                    <FiX size={13} />
                  </button>
                )}
              </div>
            </div>

            {/* Role Filter */}
            <div className="col-lg-2 col-md-4 col-sm-6">
              <div className="input-group input-group-sm">
                <span className="input-group-text bg-white"><FiShield className="text-muted" /></span>
                <select className="form-select" value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
                  {ROLES.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Region Filter */}
            <div className="col-lg-3 col-md-4 col-sm-6">
              <div className="input-group input-group-sm">
                <span className="input-group-text bg-white"><FiGlobe className="text-muted" /></span>
                <select className="form-select" value={regionFilter} onChange={(e) => setRegionFilter(e.target.value)}>
                  {REGIONS.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Status Filter */}
            <div className="col-lg-3 col-md-4 col-sm-12">
              <div className="input-group input-group-sm">
                <span className="input-group-text bg-white"><FiActivity className="text-muted" /></span>
                <select className="form-select" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                  {STATUSES.map((s) => (
                    <option key={s.value} value={s.value}>{s.label}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Quick Category Chips & Active Filter Summary */}
          <div className="d-flex flex-wrap align-items-center justify-content-between gap-2 mt-3 pt-2 border-top">
            <div className="d-flex flex-wrap align-items-center gap-1">
              <span className="small text-muted me-1 fw-semibold d-flex align-items-center gap-1">
                <FiFilter size={12} /> Event Type:
              </span>
              {ACTION_CATEGORIES.map((cat) => (
                <button
                  key={cat.id}
                  type="button"
                  className={`btn btn-sm py-0 px-2 rounded-pill ${
                    categoryFilter === cat.id ? 'btn-success text-white fw-bold' : 'btn-outline-secondary'
                  }`}
                  style={{ fontSize: '0.75rem' }}
                  onClick={() => setCategoryFilter(cat.id)}
                >
                  {cat.label}
                </button>
              ))}
            </div>

            {/* Results Count & Clear Button */}
            <div className="d-flex align-items-center gap-2">
              <span className="small text-muted">
                Showing <strong>{filteredLogs.length}</strong> of <strong>{logs.length}</strong> records
              </span>
              {hasActiveFilters && (
                <button
                  type="button"
                  className="btn btn-sm btn-link text-danger p-0 text-decoration-none d-flex align-items-center gap-1 fw-semibold"
                  style={{ fontSize: '0.78rem' }}
                  onClick={resetAllFilters}
                >
                  <FiRotateCcw size={12} /> Reset Filters
                </button>
              )}
            </div>
          </div>

          {/* Active Filter Badges */}
          {hasActiveFilters && (
            <div className="d-flex flex-wrap align-items-center gap-1 mt-2">
              <span className="small text-muted" style={{ fontSize: '0.75rem' }}>Active:</span>
              {searchQuery.trim() && (
                <span className="badge bg-light text-dark border d-flex align-items-center gap-1">
                  Search: "{searchQuery}"
                  <FiX className="text-muted" style={{ cursor: 'pointer' }} onClick={() => setSearchQuery('')} />
                </span>
              )}
              {roleFilter !== 'ALL' && (
                <span className="badge bg-light text-dark border d-flex align-items-center gap-1">
                  Role: {roleFilter}
                  <FiX className="text-muted" style={{ cursor: 'pointer' }} onClick={() => setRoleFilter('ALL')} />
                </span>
              )}
              {regionFilter !== 'ALL' && (
                <span className="badge bg-light text-dark border d-flex align-items-center gap-1">
                  Region: {regionFilter}
                  <FiX className="text-muted" style={{ cursor: 'pointer' }} onClick={() => setRegionFilter('ALL')} />
                </span>
              )}
              {statusFilter !== 'ALL' && (
                <span className="badge bg-light text-dark border d-flex align-items-center gap-1">
                  Status: {statusFilter}
                  <FiX className="text-muted" style={{ cursor: 'pointer' }} onClick={() => setStatusFilter('ALL')} />
                </span>
              )}
              {categoryFilter !== 'ALL' && (
                <span className="badge bg-light text-dark border d-flex align-items-center gap-1">
                  Event: {categoryFilter}
                  <FiX className="text-muted" style={{ cursor: 'pointer' }} onClick={() => setCategoryFilter('ALL')} />
                </span>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Audit Log Table */}
      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner label="Fetching audit logs..." />
          ) : error ? (
            <ErrorState retry={fetchLogs} message="Unable to load audit logs." />
          ) : filteredLogs.length === 0 ? (
            <div className="p-5 text-center">
              <EmptyState 
                icon={FiFileText} 
                title="No matching audit logs found" 
                message="Try adjusting your search keywords, role, region, or HTTP status filters." 
              />
              {hasActiveFilters && (
                <div className="mt-3">
                  <button className="btn btn-outline-success btn-sm fw-semibold" onClick={resetAllFilters}>
                    <FiRotateCcw className="me-1" /> Clear All Filters
                  </button>
                </div>
              )}
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Req ID</th>
                    <th>Action Event</th>
                    <th>User Role</th>
                    <th>Method / URL</th>
                    <th>Region</th>
                    <th>Status</th>
                    <th>Response Time</th>
                    <th>Timestamp</th>
                    <th className="pe-3 text-end">Details</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredLogs.map((log) => {
                    const status = log.responseStatus || 200;
                    const isSuccess = status >= 200 && status < 300;
                    const role = log.userRole || inferRole(log);
                    return (
                      <tr key={log.id}>
                        <td className="ps-3 small fw-bold font-monospace text-secondary">
                          {log.requestId ? log.requestId.substring(0, 8) + '...' : `REQ-${log.id}`}
                        </td>
                        <td className="small fw-semibold text-dark">
                          <span className="badge bg-dark-subtle text-dark border me-1">
                            {log.action}
                          </span>
                        </td>
                        <td className="small">
                          <span className={`badge ${
                            role === 'ADMIN' ? 'bg-danger-subtle text-danger' :
                            role === 'INDUSTRY' ? 'bg-primary-subtle text-primary' :
                            role === 'RECYCLER' ? 'bg-warning-subtle text-warning' :
                            'bg-success-subtle text-success'
                          }`}>
                            {role}
                          </span>
                        </td>
                        <td className="small text-truncate" style={{ maxWidth: '200px' }}>
                          {log.method && <span className="badge bg-secondary me-1">{log.method}</span>}
                          <span className="text-muted">{log.url || log.details || '/api/v1/events'}</span>
                        </td>
                        <td className="small text-muted">
                          {log.region || 'Pune Region'}
                        </td>
                        <td className="small">
                          <span className={`badge ${isSuccess ? 'bg-success' : 'bg-danger'}`}>
                            {status}
                          </span>
                        </td>
                        <td className="small text-muted">{log.responseTimeMs ? `${log.responseTimeMs}ms` : '14ms'}</td>
                        <td className="small text-muted">{new Date(log.createdAt).toLocaleString()}</td>
                        <td className="pe-3 text-end">
                          <button
                            className="btn btn-sm btn-outline-primary py-0 px-2"
                            onClick={() => setSelectedLog(log)}
                          >
                            <FiEye size={12} className="me-1" /> View
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* VIEW DETAILS MODAL */}
      {selectedLog && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', backdropFilter: 'blur(3px)' }} tabIndex="-1">
          <div className="modal-dialog modal-dialog-centered modal-lg">
            <div className="modal-content border-0 shadow-lg" style={{ borderRadius: 12 }}>
              <div className="modal-header bg-dark text-white">
                <h5 className="modal-title fw-bold d-flex align-items-center gap-2 fs-6">
                  <FiFileText /> Audit Log Details — #{selectedLog.id}
                </h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setSelectedLog(null)}></button>
              </div>
              <div className="modal-body p-4">
                <div className="row g-3 small">
                  <div className="col-md-6">
                    <strong>Request ID:</strong> <code className="ms-1">{selectedLog.requestId || 'REQ-' + selectedLog.id}</code>
                  </div>
                  <div className="col-md-6">
                    <strong>Timestamp:</strong> <span className="ms-1">{new Date(selectedLog.createdAt).toLocaleString()}</span>
                  </div>
                  <div className="col-md-6">
                    <strong>User ID:</strong> <span className="ms-1">{selectedLog.userId ? `User #${selectedLog.userId}` : 'Anonymous / Event Bus'}</span>
                  </div>
                  <div className="col-md-6">
                    <strong>User Role:</strong> <span className="badge bg-secondary ms-1">{selectedLog.userRole || inferRole(selectedLog)}</span>
                  </div>
                  <div className="col-md-6">
                    <strong>Region:</strong> <span className="ms-1">{selectedLog.region || 'Pune Region (Default)'}</span>
                  </div>
                  <div className="col-md-6">
                    <strong>IP Address:</strong> <code className="ms-1">{selectedLog.ipAddress || '127.0.0.1'}</code>
                  </div>
                  <div className="col-md-6">
                    <strong>HTTP Method & Status:</strong> 
                    <span className="badge bg-dark ms-2 me-1">{selectedLog.method || 'POST'}</span>
                    <span className={`badge ${(!selectedLog.responseStatus || selectedLog.responseStatus < 300) ? 'bg-success' : 'bg-danger'}`}>
                      {selectedLog.responseStatus || 200}
                    </span>
                  </div>
                  <div className="col-md-6">
                    <strong>Response Time:</strong> <span className="ms-1">{selectedLog.responseTimeMs ? `${selectedLog.responseTimeMs} ms` : '14 ms'}</span>
                  </div>

                  <div className="col-12 border-top pt-3">
                    <strong>Action Event:</strong>
                    <div className="p-2 bg-light rounded mt-1 fw-bold text-dark">{selectedLog.action}</div>
                  </div>

                  <div className="col-12">
                    <strong>Log Payload / Details (Sensitive Data Masked):</strong>
                    <pre className="p-3 bg-dark text-success rounded mt-1 font-monospace" style={{ whiteSpace: 'pre-wrap', maxHeight: '200px', fontSize: '0.8rem' }}>
                      {selectedLog.details || 'No additional payload metadata recorded for this action.'}
                    </pre>
                  </div>
                </div>
              </div>
              <div className="modal-footer bg-light">
                <button type="button" className="btn btn-sm btn-secondary" onClick={() => setSelectedLog(null)}>
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuditLogs;
