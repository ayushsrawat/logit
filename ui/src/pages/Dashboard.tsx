import React, { useEffect, useState } from 'react';
import { AgCharts } from 'ag-charts-react';
import type { AgChartOptions } from 'ag-charts-community';

interface IndexStat {
    index: string;
    count: number;
}

export const Dashboard: React.FC = () => {
    const [chartOptions, setChartOptions] = useState<AgChartOptions>({
        data: [],
        series: [{ type: 'pie', angleKey: 'count', legendItemKey: 'index' }],
        background: { fill: 'transparent' },
    });

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('/api/search/stats/ic');
                if (!response.ok) throw new Error('Failed to fetch stats');
                const data: IndexStat[] = await response.json();

                setChartOptions({
                    data: data,
                    title: {
                        text: 'Log Distribution by Index',
                        color: '#f8fafc',
                    },
                    background: {
                        fill: 'transparent',
                    },
                    series: [
                        {
                            type: 'pie',
                            angleKey: 'count',
                            legendItemKey: 'index',
                            sectorLabelKey: 'count',
                            sectorLabel: {
                                color: 'white',
                                fontWeight: 'bold',
                            },
                            fills: ['#38bdf8', '#818cf8', '#fbbf24', '#f87171', '#34d399'],
                            strokeWidth: 0,
                            calloutLabel: {
                                color: '#94a3b8',
                            }
                        },
                    ],
                    legend: {
                        item: {
                            label: {
                                color: '#94a3b8',
                            },
                        },
                    },
                });
            } catch (error) {
                console.error('Error fetching stats:', error);
            }
        };

        fetchData();
        // Refresh every 5 seconds
        const interval = setInterval(fetchData, 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="page-container">
            <header className="page-header">
                <h1 className="page-title">Dashboard</h1>
            </header>

            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))',
                gap: '1.5rem'
            }}>
                <div style={{
                    backgroundColor: 'var(--bg-secondary)',
                    borderRadius: '0.5rem',
                    padding: '1.5rem',
                    border: '1px solid var(--border-color)',
                    height: '400px'
                }}>
                    <AgCharts options={chartOptions} style={{ height: '100%' }} />
                </div>
            </div>
        </div>
    );
};
