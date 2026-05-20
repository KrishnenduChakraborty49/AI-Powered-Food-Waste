import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import NotificationBell from '../components/NotificationBell';
import {
  BarChart3, Users, PackageOpen, Leaf, ShieldCheck,
  Trash2, ChevronLeft, ChevronRight, CheckCircle2,
  Clock, XCircle, TrendingUp, Award, Box
} from 'lucide-react';

const AdminDashboard = () => {
  const { logout } = useAuth();
  const [summary, setSummary] = useState(null);
  const [leaderboard, setLeaderboard] = useState([]);
  const [users, setUsers] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [listings, setListings] = useState([]);
  const [activeTab, setActiveTab] = useState('overview');
  const [usersPage, setUsersPage] = useState(0);
  const [txPage, setTxPage] = useState(0);
  const [usersTotalPages, setUsersTotalPages] = useState(0);
  const [txTotalPages, setTxTotalPages] = useState(0);

  useEffect(() => { fetchSummary(); fetchLeaderboard(); }, []);
  useEffect(() => { if (activeTab === 'users') fetchUsers(); }, [activeTab, usersPage]);
  useEffect(() => { if (activeTab === 'transactions') fetchTransactions(); }, [activeTab, txPage]);
  useEffect(() => { if (activeTab === 'listings') fetchListings(); }, [activeTab]);

  const fetchSummary = async () => {
    try {
      const res = await api.get('/analytics/summary');
      setSummary(res.data);
    } catch { setSummary({ totalMealsSaved: 0, totalCo2ReducedKg: 0, activeListingsCount: 0, activeUsersCount: 0 }); }
  };

  const fetchLeaderboard = async () => {
    try {
      const res = await api.get('/analytics/leaderboard');
      setLeaderboard(res.data);
    } catch { console.error('Failed to fetch leaderboard'); }
  };

  const fetchUsers = async () => {
    try {
      const res = await api.get(`/analytics/admin/users?page=${usersPage}&size=8`);
      setUsers(res.data.content || []);
      setUsersTotalPages(res.data.totalPages || 0);
    } catch { console.error('Failed to fetch users'); }
  };

  const fetchTransactions = async () => {
    try {
      const res = await api.get(`/analytics/admin/transactions?page=${txPage}&size=8`);
      setTransactions(res.data.content || []);
      setTxTotalPages(res.data.totalPages || 0);
    } catch { console.error('Failed to fetch transactions'); }
  };

  const fetchListings = async () => {
    try {
      const res = await api.get('/analytics/admin/listings?size=20');
      setListings(res.data.content || []);
    } catch { console.error('Failed to fetch listings'); }
  };

  const handleDeleteUser = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete user "${name}"?`)) return;
    try {
      await api.delete(`/analytics/admin/users/${id}`);
      fetchUsers();
      fetchSummary();
    } catch { alert('Failed to delete user.'); }
  };

  const StatCard = ({ title, value, icon: Icon, gradient, sub }) => (
    <div className={`relative overflow-hidden rounded-2xl p-6 text-white ${gradient}`}>
      <div className="relative z-10">
        <p className="text-sm font-medium opacity-80">{title}</p>
        <p className="text-4xl font-black mt-1">{value}</p>
        {sub && <p className="text-xs opacity-70 mt-1">{sub}</p>}
      </div>
      <Icon className="absolute -bottom-3 -right-3 w-24 h-24 opacity-10" />
    </div>
  );

  const badgeColors = {
    PLATINUM: 'bg-purple-100 text-purple-700',
    GOLD: 'bg-yellow-100 text-yellow-700',
    SILVER: 'bg-gray-100 text-gray-600',
    BRONZE: 'bg-orange-100 text-orange-700',
  };
  const roleColors = {
    DONOR: 'bg-green-100 text-green-700',
    NGO: 'bg-blue-100 text-blue-700',
    VOLUNTEER: 'bg-violet-100 text-violet-700',
    ADMIN: 'bg-red-100 text-red-700',
  };
  const statusColors = {
    PENDING: 'bg-amber-100 text-amber-700',
    IN_TRANSIT: 'bg-blue-100 text-blue-700',
    DELIVERED: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-red-100 text-red-700',
  };
  const listingStatusColors = {
    AVAILABLE: 'bg-green-100 text-green-700',
    CLAIMED: 'bg-blue-100 text-blue-700',
    EXPIRED: 'bg-gray-100 text-gray-500',
  };

  const tabs = [
    { id: 'overview', label: 'Overview', icon: BarChart3 },
    { id: 'users', label: 'Users', icon: Users },
    { id: 'transactions', label: 'Transactions', icon: TrendingUp },
    { id: 'listings', label: 'Food Listings', icon: Box },
  ];

  return (
    <div className="min-h-screen bg-gray-950 text-white">
      {/* Sidebar */}
      <div className="flex">
        <aside className="w-64 min-h-screen bg-gray-900 border-r border-gray-800 fixed top-0 left-0 flex flex-col">
          <div className="p-6 border-b border-gray-800">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-green-400 to-teal-500 flex items-center justify-center">
                <ShieldCheck className="w-5 h-5 text-white" />
              </div>
              <div>
                <p className="font-bold text-white text-sm">FoodLink</p>
                <p className="text-xs text-gray-400">Admin Panel</p>
              </div>
            </div>
          </div>
          <nav className="flex-1 p-4 space-y-1">
            {tabs.map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all ${
                  activeTab === tab.id
                    ? 'bg-green-500/20 text-green-400 border border-green-500/30'
                    : 'text-gray-400 hover:bg-gray-800 hover:text-white'
                }`}
              >
                <tab.icon className="w-4 h-4" />
                {tab.label}
              </button>
            ))}
          </nav>
          <div className="p-4 border-t border-gray-800">
            <button
              onClick={logout}
              className="w-full px-4 py-2.5 rounded-xl bg-gray-800 text-gray-400 hover:bg-red-500/20 hover:text-red-400 text-sm font-medium transition-all"
            >
              Logout
            </button>
          </div>
        </aside>

        {/* Main Content */}
        <main className="ml-64 flex-1 p-8">
          {/* Header */}
          <div className="flex items-center justify-between mb-8">
            <div>
              <h1 className="text-2xl font-black text-white">
                {tabs.find(t => t.id === activeTab)?.label}
              </h1>
              <p className="text-gray-400 text-sm mt-0.5">FoodLink system administration</p>
            </div>
            <div className="flex items-center gap-3">
              <NotificationBell />
              <div className="w-9 h-9 rounded-full bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center text-xs font-bold">
                AD
              </div>
            </div>
          </div>

          {/* OVERVIEW TAB */}
          {activeTab === 'overview' && (
            <div className="space-y-8">
              {summary && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
                  <StatCard title="Total Meals Saved" value={summary.totalMealsSaved?.toLocaleString()} icon={BarChart3} gradient="bg-gradient-to-br from-green-500 to-emerald-600" sub="Across all donations" />
                  <StatCard title="CO₂ Reduced" value={`${summary.totalCo2ReducedKg} kg`} icon={Leaf} gradient="bg-gradient-to-br from-teal-500 to-cyan-600" sub="Carbon footprint saved" />
                  <StatCard title="Active Listings" value={summary.activeListingsCount} icon={PackageOpen} gradient="bg-gradient-to-br from-amber-500 to-orange-600" sub="Food available now" />
                  <StatCard title="Total Users" value={summary.activeUsersCount} icon={Users} gradient="bg-gradient-to-br from-violet-500 to-purple-600" sub="Registered accounts" />
                </div>
              )}

              {/* Leaderboard */}
              <div className="bg-gray-900 rounded-2xl border border-gray-800 p-6">
                <h2 className="text-lg font-bold text-white mb-5 flex items-center gap-2">
                  <Award className="w-5 h-5 text-yellow-400" /> Top Contributors Leaderboard
                </h2>
                <div className="space-y-3">
                  {leaderboard.map((user, index) => (
                    <div key={user.id} className="flex items-center justify-between p-4 bg-gray-800 rounded-xl hover:bg-gray-750 transition-colors">
                      <div className="flex items-center gap-4">
                        <div className={`w-9 h-9 rounded-full flex items-center justify-center font-black text-sm ${
                          index === 0 ? 'bg-yellow-400 text-yellow-900' :
                          index === 1 ? 'bg-gray-300 text-gray-700' :
                          index === 2 ? 'bg-orange-400 text-orange-900' : 'bg-gray-700 text-gray-300'
                        }`}>#{index + 1}</div>
                        <div>
                          <p className="font-semibold text-white text-sm">{user.name}</p>
                          <p className="text-xs text-gray-400">{user.email}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${roleColors[user.role] || 'bg-gray-700 text-gray-300'}`}>{user.role}</span>
                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${badgeColors[user.badgeLevel] || 'bg-gray-700 text-gray-300'}`}>{user.badgeLevel}</span>
                        <span className="text-green-400 font-bold text-sm">{user.points} pts</span>
                      </div>
                    </div>
                  ))}
                  {leaderboard.length === 0 && <p className="text-gray-500 text-center py-8">No users yet.</p>}
                </div>
              </div>
            </div>
          )}

          {/* USERS TAB */}
          {activeTab === 'users' && (
            <div className="bg-gray-900 rounded-2xl border border-gray-800 overflow-hidden">
              <table className="w-full">
                <thead className="bg-gray-800 border-b border-gray-700">
                  <tr>
                    {['ID', 'Name', 'Email', 'Role', 'Trust Score', 'Points', 'Badge', 'Actions'].map(h => (
                      <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800">
                  {users.map(user => (
                    <tr key={user.id} className="hover:bg-gray-800/50 transition-colors">
                      <td className="px-4 py-4 text-gray-400 text-sm">#{user.id}</td>
                      <td className="px-4 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-green-400 to-teal-500 flex items-center justify-center text-xs font-bold text-white">
                            {user.name?.[0]?.toUpperCase()}
                          </div>
                          <span className="text-sm font-medium text-white">{user.name}</span>
                        </div>
                      </td>
                      <td className="px-4 py-4 text-gray-400 text-sm">{user.email}</td>
                      <td className="px-4 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${roleColors[user.role] || 'bg-gray-700 text-gray-300'}`}>{user.role}</span>
                      </td>
                      <td className="px-4 py-4 text-amber-400 font-bold text-sm">⭐ {user.trustScore}</td>
                      <td className="px-4 py-4 text-green-400 font-semibold text-sm">{user.points} pts</td>
                      <td className="px-4 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${badgeColors[user.badgeLevel] || 'bg-gray-700 text-gray-300'}`}>{user.badgeLevel}</span>
                      </td>
                      <td className="px-4 py-4">
                        <button
                          onClick={() => handleDeleteUser(user.id, user.name)}
                          className="p-2 rounded-lg text-gray-500 hover:bg-red-500/20 hover:text-red-400 transition-colors"
                          title="Delete user"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {users.length === 0 && <p className="text-gray-500 text-center py-12">No users found.</p>}
              {/* Pagination */}
              <div className="px-4 py-3 bg-gray-800 border-t border-gray-700 flex items-center justify-between">
                <p className="text-sm text-gray-400">Page {usersPage + 1} of {usersTotalPages || 1}</p>
                <div className="flex gap-2">
                  <button onClick={() => setUsersPage(p => Math.max(0, p - 1))} disabled={usersPage === 0} className="p-2 rounded-lg bg-gray-700 text-gray-300 hover:bg-gray-600 disabled:opacity-40 disabled:cursor-not-allowed"><ChevronLeft className="w-4 h-4" /></button>
                  <button onClick={() => setUsersPage(p => p + 1)} disabled={usersPage >= usersTotalPages - 1} className="p-2 rounded-lg bg-gray-700 text-gray-300 hover:bg-gray-600 disabled:opacity-40 disabled:cursor-not-allowed"><ChevronRight className="w-4 h-4" /></button>
                </div>
              </div>
            </div>
          )}

          {/* TRANSACTIONS TAB */}
          {activeTab === 'transactions' && (
            <div className="bg-gray-900 rounded-2xl border border-gray-800 overflow-hidden">
              <table className="w-full">
                <thead className="bg-gray-800 border-b border-gray-700">
                  <tr>
                    {['ID', 'Food', 'Donor', 'NGO', 'Volunteer', 'Status', 'OTP', 'Rating'].map(h => (
                      <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800">
                  {transactions.map(tx => (
                    <tr key={tx.id} className="hover:bg-gray-800/50 transition-colors">
                      <td className="px-4 py-4 text-gray-400 text-sm">#{tx.id}</td>
                      <td className="px-4 py-4 text-white font-medium text-sm">{tx.foodName}</td>
                      <td className="px-4 py-4 text-gray-300 text-sm">{tx.donorName}</td>
                      <td className="px-4 py-4 text-gray-300 text-sm">{tx.ngoName}</td>
                      <td className="px-4 py-4 text-gray-400 text-sm">{tx.volunteerName || '—'}</td>
                      <td className="px-4 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${statusColors[tx.deliveryStatus] || 'bg-gray-700 text-gray-300'}`}>{tx.deliveryStatus}</span>
                      </td>
                      <td className="px-4 py-4">
                        {tx.otpVerified
                          ? <CheckCircle2 className="w-4 h-4 text-green-500" />
                          : <Clock className="w-4 h-4 text-gray-500" />}
                      </td>
                      <td className="px-4 py-4 text-amber-400 text-sm">
                        {tx.donorRating ? `★ ${tx.donorRating}` : '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {transactions.length === 0 && <p className="text-gray-500 text-center py-12">No transactions found.</p>}
              <div className="px-4 py-3 bg-gray-800 border-t border-gray-700 flex items-center justify-between">
                <p className="text-sm text-gray-400">Page {txPage + 1} of {txTotalPages || 1}</p>
                <div className="flex gap-2">
                  <button onClick={() => setTxPage(p => Math.max(0, p - 1))} disabled={txPage === 0} className="p-2 rounded-lg bg-gray-700 text-gray-300 hover:bg-gray-600 disabled:opacity-40 disabled:cursor-not-allowed"><ChevronLeft className="w-4 h-4" /></button>
                  <button onClick={() => setTxPage(p => p + 1)} disabled={txPage >= txTotalPages - 1} className="p-2 rounded-lg bg-gray-700 text-gray-300 hover:bg-gray-600 disabled:opacity-40 disabled:cursor-not-allowed"><ChevronRight className="w-4 h-4" /></button>
                </div>
              </div>
            </div>
          )}

          {/* LISTINGS TAB */}
          {activeTab === 'listings' && (
            <div className="bg-gray-900 rounded-2xl border border-gray-800 overflow-hidden">
              <table className="w-full">
                <thead className="bg-gray-800 border-b border-gray-700">
                  <tr>
                    {['ID', 'Food Name', 'Type', 'Quantity', 'Donor', 'Status', 'Priority Score', 'Expires'].map(h => (
                      <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800">
                  {listings.map(listing => (
                    <tr key={listing.id} className="hover:bg-gray-800/50 transition-colors">
                      <td className="px-4 py-4 text-gray-400 text-sm">#{listing.id}</td>
                      <td className="px-4 py-4 text-white font-medium text-sm">{listing.foodName}</td>
                      <td className="px-4 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${listing.foodType === 'VEG' ? 'bg-green-900 text-green-300' : listing.foodType === 'NON_VEG' ? 'bg-red-900 text-red-300' : 'bg-amber-900 text-amber-300'}`}>{listing.foodType}</span>
                      </td>
                      <td className="px-4 py-4 text-gray-300 text-sm">{listing.quantity} {listing.unit}</td>
                      <td className="px-4 py-4 text-gray-300 text-sm">{listing.donor?.name || '—'}</td>
                      <td className="px-4 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${listingStatusColors[listing.status] || 'bg-gray-700 text-gray-300'}`}>{listing.status}</span>
                      </td>
                      <td className="px-4 py-4">
                        <div className={`text-xs font-bold px-2 py-1 rounded-full inline-block ${
                          listing.priorityScore >= 80 ? 'bg-red-900 text-red-300' :
                          listing.priorityScore >= 50 ? 'bg-amber-900 text-amber-300' :
                          'bg-green-900 text-green-300'
                        }`}>{listing.priorityScore}</div>
                      </td>
                      <td className="px-4 py-4 text-gray-400 text-xs">
                        {listing.expiryTime ? new Date(listing.expiryTime).toLocaleString() : '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {listings.length === 0 && <p className="text-gray-500 text-center py-12">No listings found.</p>}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default AdminDashboard;
