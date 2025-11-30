import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Search, Terminal, Activity } from 'lucide-react';

export const Sidebar: React.FC = () => {
    return (
        <aside className="sidebar">
            <div className="sidebar-header">
                <div className="app-title">
                    <Activity size={24} color="var(--text-accent)" />
                    <span>Logit</span>
                </div>
            </div>

            <nav className="sidebar-nav">
                <NavLink
                    to="/dashboard"
                    className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                >
                    <LayoutDashboard />
                    <span>Dashboard</span>
                </NavLink>

                <NavLink
                    to="/search"
                    className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                >
                    <Search />
                    <span>Search</span>
                </NavLink>

                <NavLink
                    to="/console"
                    className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
                >
                    <Terminal />
                    <span>Console</span>
                </NavLink>
            </nav>
        </aside>
    );
};
