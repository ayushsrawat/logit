import React, { useEffect, useState } from 'react';
import { SearchInput } from '../components/SearchInput';
import { LogTable } from '../components/LogTable';
import { useSearch } from '../context/SearchContext';

interface SearchHit {
    hit: {
        timestamp: string;
        clazz: string;
        method: string;
        level: string;
        message: string;
    };
    score: number;
    docId: number;
}

interface IndexStat {
    index: string;
    count: number;
}

export const Search: React.FC = () => {
    const {
        logs, setLogs,
        selectedIndex, setSelectedIndex,
        searchQuery, setSearchQuery
    } = useSearch();

    const [indexes, setIndexes] = useState<string[]>([]);

    useEffect(() => {
        // Fetch available indexes
        fetch('/api/search/stats/ic')
            .then(res => res.json())
            .then((data: IndexStat[]) => {
                const idxList = data.map(d => d.index);
                setIndexes(idxList);
                // Only set default index if none is selected in context
                if (idxList.length > 0 && !selectedIndex) {
                    setSelectedIndex(idxList[0]);
                }
            })
            .catch(err => console.error('Failed to fetch indexes:', err));
    }, []); // Only run once on mount

    useEffect(() => {
        if (!selectedIndex) return;

        const fetchLogs = async () => {
            try {
                const response = await fetch('/api/search/fluent', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    body: JSON.stringify({
                        index: selectedIndex,
                        query: searchQuery || '*:*',
                        topN: 20,
                        fields: ['timestamp', 'message', 'level', 'class', 'method'],
                        stem: true
                    }),
                });

                if (!response.ok) throw new Error('Search failed');

                const data: SearchHit[] = await response.json();
                const mappedLogs = data.map(item => ({
                    timestamp: item.hit.timestamp,
                    level: item.hit.level,
                    message: item.hit.message,
                    class: item.hit.clazz,
                    method: item.hit.method
                }));

                setLogs(mappedLogs);
            } catch (error) {
                console.error('Search error:', error);
            }
        };

        fetchLogs();
    }, [selectedIndex, searchQuery, setLogs]);

    return (
        <div className="page-container">
            <header className="page-header">
                <h1 className="page-title">Search Logs</h1>
            </header>

            <SearchInput
                onSearch={setSearchQuery}
                indexes={indexes}
                selectedIndex={selectedIndex}
                onIndexChange={setSelectedIndex}
            />
            <LogTable logs={logs} />
        </div>
    );
};
