import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import ListingCard from '../components/ListingCard';
import TrustRing from '../components/TrustRing';
import { useAuth } from '../context/AuthContext';
import { Plus } from 'lucide-react';
import toast from 'react-hot-toast';
import LocationPicker from '../components/LocationPicker';
import NotificationBell from '../components/NotificationBell';

const DonorDashboard = () => {
  const { user, logout } = useAuth();
  const [listings, setListings] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [profile, setProfile] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [smartText, setSmartText] = useState('');
  const [isParsing, setIsParsing] = useState(false);
  
  const [formData, setFormData] = useState({
    foodName: '',
    foodType: 'VEG',
    quantity: '',
    unit: 'PORTIONS',
    description: '',
    pickupAddress: '',
    lat: 22.5726,
    lng: 88.3639,
    expiryTime: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString().slice(0, 16)
  });

  const fetchListings = async () => {
    try {
      const response = await api.get('/listings/my');
      setListings(response.data.content || []);
    } catch (error) {
      console.error("Failed to fetch listings", error);
    }
  };

  const fetchDashboardData = async () => {
    try {
      const [profileRes, analyticsRes] = await Promise.all([
        api.get('/users/me'),
        api.get('/analytics/summary')
      ]);
      setProfile(profileRes.data);
      setAnalytics(analyticsRes.data);
    } catch (error) {
      console.error("Failed to fetch dashboard data", error);
    }
  };

  useEffect(() => {
    fetchListings();
    fetchDashboardData();
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/listings', formData);
      setShowForm(false);
      toast.success('Food listing posted successfully!');
      fetchListings(); // Refresh the list
    } catch (error) {
      console.error("Failed to create listing", error);
      toast.error('Failed to post listing. Please check all fields.');
    }
  };

  const handleSmartParse = async () => {
    if (!smartText) return;
    setIsParsing(true);
    try {
      const response = await api.post('/ai/parse-listing', { text: smartText });
      
      setFormData(prev => {
        let newExpiry = prev.expiryTime;
        if (response.data && response.data.expiryTime) {
          if (typeof response.data.expiryTime === 'string') {
            newExpiry = response.data.expiryTime.substring(0, 16);
          } else if (Array.isArray(response.data.expiryTime)) {
            const [y, m, d, h, min] = response.data.expiryTime;
            newExpiry = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}T${String(h || 0).padStart(2, '0')}:${String(min || 0).padStart(2, '0')}`;
          }
        }
        
        return {
          ...prev,
          ...response.data,
          expiryTime: newExpiry
        };
      });
      
      toast.success('Form auto-filled by AI!');
      setSmartText('');
    } catch (error) {
      console.error("AI Parse Error:", error);
      toast.error('Failed to parse text. Please try manually.');
    } finally {
      setIsParsing(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6 md:p-10">
      <div className="max-w-6xl mx-auto space-y-8">
        
        <header className="flex justify-between items-center pb-6 border-b border-gray-200">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Donor Dashboard</h1>
            <p className="text-gray-500 mt-1">Manage your donations and track your impact</p>
          </div>
          <div className="flex items-center gap-3">
            <NotificationBell />
            <button onClick={logout} className="text-gray-500 hover:text-gray-700 font-medium text-sm">
              Logout
            </button>
          </div>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="md:col-span-1 space-y-6">
            {/* Gamification Sidebar */}
            <TrustRing score={profile?.trustScore || 5.0} totalRatings={profile?.totalRatings || 0} />
            
            <div className="bg-white p-5 rounded-xl shadow-sm border border-gray-100 text-center">
              <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Impact</h3>
              <p className="text-4xl font-extrabold text-green-500 mt-2">{analytics?.totalMealsSaved || 0}</p>
              <p className="text-sm text-gray-600 mt-1">Meals Donated</p>
            </div>
            <div className="bg-white p-5 rounded-xl shadow-sm border border-gray-100 text-center">
              <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Environment</h3>
              <p className="text-3xl font-bold text-blue-500 mt-2">{analytics?.totalCo2ReducedKg || 0} kg</p>
              <p className="text-sm text-gray-600 mt-1">CO₂ Saved</p>
            </div>
            
            <button 
              onClick={() => setShowForm(!showForm)}
              className="w-full flex items-center justify-center py-3 px-4 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors shadow-sm"
            >
              <Plus className="w-5 h-5 mr-2" />
              Post New Food
            </button>
          </div>

          <div className="md:col-span-3">
            {showForm && (
              <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 mb-6">
                <h2 className="text-xl font-bold text-gray-800 mb-4 flex items-center">
                  ✨ AI Smart Entry
                </h2>
                <div className="mb-6 flex gap-2">
                  <input 
                    type="text" 
                    value={smartText} 
                    onChange={e => setSmartText(e.target.value)} 
                    placeholder="E.g. I have 20 portions of chicken biryani expiring tonight" 
                    className="flex-1 px-4 py-2 border border-purple-200 rounded-lg focus:ring-purple-500 focus:border-purple-500"
                  />
                  <button 
                    onClick={handleSmartParse}
                    disabled={isParsing}
                    className="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:opacity-50"
                  >
                    {isParsing ? 'Parsing...' : 'Auto-Fill'}
                  </button>
                </div>
                
                <h2 className="text-xl font-bold text-gray-800 mb-4">Manual Details</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Food Name</label>
                      <input type="text" name="foodName" value={formData.foodName} onChange={handleChange} required className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md" />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Food Type</label>
                      <select name="foodType" value={formData.foodType} onChange={handleChange} className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md">
                        <option value="VEG">Vegetarian</option>
                        <option value="NON_VEG">Non-Vegetarian</option>
                        <option value="GRAIN">Grains/Raw</option>
                        <option value="DAIRY">Dairy</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Quantity</label>
                      <input type="number" name="quantity" value={formData.quantity} onChange={handleChange} required className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md" />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Unit</label>
                      <select name="unit" value={formData.unit} onChange={handleChange} className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md">
                        <option value="PORTIONS">Portions</option>
                        <option value="KG">Kilograms</option>
                        <option value="LITRES">Litres</option>
                      </select>
                    </div>
                    <div className="col-span-2">
                      <label className="block text-sm font-medium text-gray-700">Expiry Time (Local)</label>
                      <input type="datetime-local" name="expiryTime" value={formData.expiryTime} onChange={handleChange} required className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md" />
                    </div>
                    <div className="col-span-2">
                      <label className="block text-sm font-medium text-gray-700">Pickup Address</label>
                      <input type="text" name="pickupAddress" value={formData.pickupAddress} onChange={handleChange} required placeholder="e.g. 12 MG Road, Kolkata" className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md" />
                    </div>
                    <div className="col-span-2" onClick={(e) => e.stopPropagation()}>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Pickup Location (click map to pin)</label>
                      <LocationPicker 
                        defaultLat={formData.lat}
                        defaultLng={formData.lng}
                        onLocationChange={(loc) => setFormData(prev => ({ ...prev, lat: loc.lat, lng: loc.lng }))}
                      />
                    </div>
                  </div>
                  <div className="flex justify-end pt-4 border-t border-gray-50">
                    <button type="button" onClick={() => setShowForm(false)} className="mr-3 px-4 py-2 text-gray-600 hover:text-gray-900">Cancel</button>
                    <button type="submit" className="px-4 py-2 bg-primary-600 text-white rounded-lg">Submit Listing</button>
                  </div>
                </form>
              </div>
            )}

            <h2 className="text-xl font-bold text-gray-800 mb-4 flex items-center">
              <span className="w-2 h-6 bg-primary-500 rounded-full mr-3"></span>
              Your Active Listings
            </h2>
            
            {listings.length === 0 ? (
              <div className="text-center py-12 bg-white rounded-xl border border-dashed border-gray-300">
                <p className="text-gray-500">You don't have any active listings.</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {listings.map(listing => (
                  <ListingCard key={listing.id} listing={listing} />
                ))}
              </div>
            )}
          </div>
        </div>

      </div>
    </div>
  );
};

export default DonorDashboard;
