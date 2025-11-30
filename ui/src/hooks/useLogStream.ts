import { useState, useEffect } from 'react';

interface LogEntry {
    timestamp: string;
    level: string;
    message: string;
    class: string;
    method: string;
}

const CLASSES = ['com.logit.AuthService', 'com.logit.PaymentGateway', 'com.logit.DatabaseManager', 'com.logit.UserController'];
const METHODS = ['authenticate', 'processPayment', 'executeQuery', 'createUser', 'validateToken'];
const LEVELS = ['INFO', 'INFO', 'INFO', 'WARN', 'DEBUG', 'ERROR'] as const;
const MESSAGES = [
    'Request received',
    'Processing payment',
    'User authenticated',
    'Database query executed',
    'Cache miss',
    'Connection timeout',
    'Job started',
    'Job completed',
];

export const useLogStream = (isActive: boolean = true) => {
    const [logs, setLogs] = useState<LogEntry[]>([]);

    useEffect(() => {
        if (!isActive) return;

        const interval = setInterval(() => {
            const newLog: LogEntry = {
                timestamp: new Date().toISOString().replace('T', ' ').substr(0, 19),
                level: LEVELS[Math.floor(Math.random() * LEVELS.length)],
                class: CLASSES[Math.floor(Math.random() * CLASSES.length)],
                method: METHODS[Math.floor(Math.random() * METHODS.length)],
                message: `${MESSAGES[Math.floor(Math.random() * MESSAGES.length)]} [${Math.floor(Math.random() * 1000)}ms]`,
            };

            setLogs(prev => [newLog, ...prev].slice(0, 100)); // Keep last 100 logs
        }, 800);

        return () => clearInterval(interval);
    }, [isActive]);

    return { logs, clearLogs: () => setLogs([]) };
};
