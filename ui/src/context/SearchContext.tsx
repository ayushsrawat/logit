import React, { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';

interface LogEntry {
    timestamp: string;
    level: string;
    message: string;
    class: string;
    method: string;
}

interface SearchContextType {
    logs: LogEntry[];
    setLogs: (logs: LogEntry[]) => void;
    selectedIndex: string;
    setSelectedIndex: (index: string) => void;
    searchQuery: string;
    setSearchQuery: (query: string) => void;
}

const SearchContext = createContext<SearchContextType | undefined>(undefined);

export const SearchProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [logs, setLogs] = useState<LogEntry[]>([]);
    const [selectedIndex, setSelectedIndex] = useState<string>('');
    const [searchQuery, setSearchQuery] = useState<string>('');

    return (
        <SearchContext.Provider value={{
            logs, setLogs,
            selectedIndex, setSelectedIndex,
            searchQuery, setSearchQuery
        }}>
            {children}
        </SearchContext.Provider>
    );
};

export const useSearch = () => {
    const context = useContext(SearchContext);
    if (context === undefined) {
        throw new Error('useSearch must be used within a SearchProvider');
    }
    return context;
};
