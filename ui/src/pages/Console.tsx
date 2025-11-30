import React, { useState } from 'react';
import { Play, Pause, Trash2 } from 'lucide-react';
import { LogTable } from '../components/LogTable';
import { useLogStream } from '../hooks/useLogStream';

export const Console: React.FC = () => {
    const [isStreaming, setIsStreaming] = useState(true);
    const { logs, clearLogs } = useLogStream(isStreaming);

    return (
        <div className="page-container">
            <header className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1 className="page-title">Live Console</h1>

                <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button
                        onClick={() => setIsStreaming(!isStreaming)}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem',
                            padding: '0.5rem 1rem',
                            backgroundColor: isStreaming ? 'rgba(251, 191, 36, 0.1)' : 'rgba(56, 189, 248, 0.1)',
                            color: isStreaming ? '#fbbf24' : '#38bdf8',
                            borderRadius: '0.5rem',
                            fontWeight: 500
                        }}
                    >
                        {isStreaming ? <><Pause size={16} /> Pause</> : <><Play size={16} /> Resume</>}
                    </button>

                    <button
                        onClick={clearLogs}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem',
                            padding: '0.5rem 1rem',
                            backgroundColor: 'rgba(248, 113, 113, 0.1)',
                            color: '#f87171',
                            borderRadius: '0.5rem',
                            fontWeight: 500
                        }}
                    >
                        <Trash2 size={16} /> Clear
                    </button>
                </div>
            </header>

            <LogTable logs={logs} />
        </div>
    );
};
