import React, { useState, useEffect, useRef } from 'react';
import { Search, Clock, Database } from 'lucide-react';

interface SearchInputProps {
    onSearch: (query: string) => void;
    indexes: string[];
    selectedIndex: string;
    onIndexChange: (index: string) => void;
}

export const SearchInput: React.FC<SearchInputProps> = ({
    onSearch,
    indexes,
    selectedIndex,
    onIndexChange
}) => {
    const [inputValue, setInputValue] = useState('');
    const debounceTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

    useEffect(() => {
        return () => {
            if (debounceTimer.current) clearTimeout(debounceTimer.current);
        };
    }, []);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setInputValue(value);

        if (debounceTimer.current) clearTimeout(debounceTimer.current);

        debounceTimer.current = setTimeout(() => {
            onSearch(value);
        }, 500); // 500ms debounce
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            if (debounceTimer.current) clearTimeout(debounceTimer.current);
            onSearch(inputValue);
        }
    };

    return (
        <div className="search-bar-container">
            <div className="index-selector">
                <Database size={16} />
                <select
                    className="index-select"
                    value={selectedIndex}
                    onChange={(e) => onIndexChange(e.target.value)}
                >
                    {indexes.map(idx => (
                        <option key={idx} value={idx}>{idx}</option>
                    ))}
                </select>
            </div>

            <div className="search-input-wrapper">
                <Search className="search-icon" size={20} />
                <input
                    type="text"
                    placeholder="Search logs (e.g. level:ERROR message:timeout)"
                    className="search-input"
                    value={inputValue}
                    onChange={handleInputChange}
                    onKeyDown={handleKeyDown}
                />
            </div>

            <div className="time-range-selector">
                <Clock size={16} />
                <select className="time-select">
                    <option value="15m">Last 15 mins</option>
                    <option value="1h">Last 1 hour</option>
                    <option value="24h">Last 24 hours</option>
                    <option value="7d">Last 7 days</option>
                </select>
            </div>
        </div>
    );
};
