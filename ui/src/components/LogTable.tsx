import React, { useState } from 'react';
import { X } from 'lucide-react';
import { formatClassName } from '../utils/formatters';

interface LogEntry {
    timestamp: string;
    level: string;
    message: string;
    class: string;
    method: string;
}

interface LogTableProps {
    logs: LogEntry[];
}

export const LogTable: React.FC<LogTableProps> = ({ logs }) => {
    const [selectedLog, setSelectedLog] = useState<LogEntry | null>(null);

    return (
        <>
            <div className="log-table-container">
                <table className="log-table">
                    <thead>
                        <tr>
                            <th style={{ width: '220px' }}>Timestamp</th>
                            <th style={{ width: '80px' }}>Level</th>
                            <th style={{ width: '200px' }}>Class</th>
                            <th style={{ width: '150px' }}>Method</th>
                            <th>Message</th>
                        </tr>
                    </thead>
                    <tbody>
                        {logs.map((log, index) => (
                            <tr
                                key={index}
                                className={`log-row level-${log.level?.toLowerCase() || 'info'} cursor-pointer`}
                                onClick={() => setSelectedLog(log)}
                                title="Click to view details"
                            >
                                <td className="font-mono text-sm whitespace-nowrap">{log.timestamp}</td>
                                <td>
                                    <span className={`log-badge badge-${log.level?.toLowerCase() || 'info'}`}>
                                        {log.level}
                                    </span>
                                </td>
                                <td className="font-mono text-sm truncate" title={log.class} style={{ color: 'var(--text-accent)', maxWidth: '200px' }}>
                                    {formatClassName(log.class)}
                                </td>
                                <td className="font-mono text-sm truncate" title={log.method} style={{ color: 'var(--text-secondary)', maxWidth: '150px' }}>
                                    {log.method}
                                </td>
                                <td
                                    className="log-message font-mono text-sm truncate"
                                >
                                    {log.message}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {selectedLog && (
                <div className="modal-overlay" onClick={() => setSelectedLog(null)}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3 className="modal-title">Log Details</h3>
                            <button className="modal-close" onClick={() => setSelectedLog(null)}>
                                <X size={20} />
                            </button>
                        </div>
                        <div className="modal-body">
                            <div className="log-detail-row">
                                <span className="label">Timestamp:</span>
                                <span className="value font-mono">{selectedLog.timestamp}</span>
                            </div>
                            <div className="log-detail-row">
                                <span className="label">Level:</span>
                                <span className={`log-badge badge-${selectedLog.level?.toLowerCase() || 'info'}`}>
                                    {selectedLog.level}
                                </span>
                            </div>
                            <div className="log-detail-row">
                                <span className="label">Class:</span>
                                <span className="value font-mono text-accent">{selectedLog.class}</span>
                            </div>
                            <div className="log-detail-row">
                                <span className="label">Method:</span>
                                <span className="value font-mono">{selectedLog.method}</span>
                            </div>
                            <div className="log-detail-section">
                                <span className="label">Message:</span>
                                <pre className="log-full-message">{selectedLog.message}</pre>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};
