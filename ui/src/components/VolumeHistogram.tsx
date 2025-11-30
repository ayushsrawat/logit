import React from 'react';

export const VolumeHistogram: React.FC = () => {
    return (
        <div className="histogram-container">
            <div className="histogram-placeholder">
                <span>Log Volume (Last 1 Hour)</span>
                <div className="histogram-bars">
                    {/* Simulated bars */}
                    {Array.from({ length: 40 }).map((_, i) => (
                        <div
                            key={i}
                            className="histogram-bar"
                            style={{ height: `${Math.random() * 100}%` }}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
};
