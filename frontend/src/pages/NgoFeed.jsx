import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import ListingCard from '../components/ListingCard';
import { useAuth } from '../context/AuthContext';
import { Search, Star } from 'lucide-react';
import toast from 'react-hot-toast';
import NotificationBell from '../components/NotificationBell';

const NgoFeed = () => {
  const { logout } = useAuth();
  const [listings, setListings] = useState([]);
  const [radius, setRadius] = useState(5000);
  const [loading, setLoading] = useState(true);
  const [myTransactions, setMyTransactions] = useState([]);
  const [ratingModal, setRatingModal] = useState(null); // { transactionId, donorName }

  const [profile, setProfile] = useState(null);

  const fetchProfile = async () => {
    try {
      const res = await api.get('/users/me');
      setProfile(res.data);
    } catch (error) {
      console.error("Failed to fetch NGO profile", error);
    }
  };

  const fetchNearbyListings = async () => {
    if (!profile) return;
    setLoading(true);
    try {
      const response = await api.get(`/listings/nearby?lat=${profile.lat}&lng=${profile.lng}&radius=${radius}`);
      setListings(response.data.content || []);
    } catch (error) {
      console.error("Failed to fetch nearby listings", error);
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchProfile();
    fetchMyTransactions();
  }, []);

  useEffect(() => {
    fetchNearbyListings();
  }, [radius, profile]);

  const handleClaim = async (id) => {
    try {
      await api.post(`/transactions/claim/${id}`);
      toast.success('Food claimed successfully! A volunteer will be matched soon.');
      fetchNearbyListings();
      fetchMyTransactions();
    } catch (error) {
      toast.error('Failed to claim food. It might have already been claimed.');
      console.error(error);
    }
  };

  const fetchMyTransactions = async () => {
    try {
      const res = await api.get('/transactions/my');
      setMyTransactions(res.data.content || []);
    } catch (error) {
      console.error('Failed to fetch transactions', error);
    }
  };

  const handleRate = async (transactionId, rating) => {
    try {
      await api.post(`/transactions/${transactionId}/rate?rating=${rating}`);
      toast.success('Rating submitted! Thank you for your feedback.');
      setRatingModal(null);
      fetchMyTransactions();
    } catch (error) {
      toast.error('Failed to submit rating.');
    }
  };

  return (
    <>
    <div className="min-h-screen bg-gray-50 p-6 md:p-10">
      <div className="max-w-6xl mx-auto space-y-8">
        
        <header className="flex justify-between items-center pb-6 border-b border-gray-200">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Available Food Feed</h1>
            <p className="text-gray-500 mt-1">Real-time nearby donations sorted by urgency & priority</p>
          </div>
          <div className="flex items-center gap-3">
            <NotificationBell />
            <button onClick={logout} className="text-gray-500 hover:text-gray-700 font-medium text-sm">Logout</button>
          </div>
        </header>

        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex items-center justify-between">
          <div className="flex items-center text-gray-700 font-medium">
            <Search className="w-5 h-5 text-gray-400 mr-2" />
            Showing listings within
          </div>
          <div className="flex items-center space-x-4">
            <input 
              type="range" 
              min="1000" 
              max="20000" 
              step="1000"
              value={radius} 
              onChange={(e) => setRadius(e.target.value)}
              className="w-48 accent-green-500"
            />
            <span className="font-bold text-green-600 min-w-[60px] text-right">
              {radius / 1000} km
            </span>
          </div>
        </div>

        {loading ? (
          <div className="flex justify-center py-20">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500"></div>
          </div>
        ) : listings.length === 0 ? (
          <div className="text-center py-20 bg-white rounded-xl border border-dashed border-gray-300">
            <p className="text-gray-500 text-lg">No available food found within this radius.</p>
            <p className="text-gray-400 mt-2">Try expanding your search radius.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {listings.map(listing => (
              <ListingCard key={listing.id} listing={listing} onClaim={handleClaim} />
            ))}
          </div>
        )}

        {/* My Past Claims with Rating */}
        {myTransactions.length > 0 && (
          <div>
            <h2 className="text-xl font-bold text-gray-800 mb-4 flex items-center">
              <span className="w-2 h-6 bg-amber-400 rounded-full mr-3"></span>
              My Claims History
            </h2>
            <div className="space-y-3">
              {myTransactions.map((tx) => (
                <div key={tx.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center justify-between">
                  <div>
                    <p className="font-semibold text-gray-900">{tx.foodName}</p>
                    <p className="text-sm text-gray-500">From {tx.donorName}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className={`text-xs font-bold px-2 py-1 rounded-full ${
                      tx.deliveryStatus === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                      tx.deliveryStatus === 'IN_TRANSIT' ? 'bg-blue-100 text-blue-700' :
                      'bg-amber-100 text-amber-700'
                    }`}>{tx.deliveryStatus}</span>
                    {tx.deliveryStatus === 'DELIVERED' && !tx.donorRating && (
                      <button
                        onClick={() => setRatingModal({ transactionId: tx.id, donorName: tx.donorName })}
                        className="flex items-center gap-1 px-3 py-1.5 bg-amber-50 hover:bg-amber-100 border border-amber-200 text-amber-700 text-sm font-medium rounded-lg transition-colors"
                      >
                        <Star className="w-4 h-4" /> Rate Donor
                      </button>
                    )}
                    {tx.donorRating && (
                      <div className="flex items-center gap-1 text-amber-500">
                        {[1,2,3,4,5].map(s => (
                          <Star key={s} className={`w-4 h-4 ${s <= tx.donorRating ? 'fill-current' : 'text-gray-300'}`} />
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

      </div>
    </div>

    {ratingModal && (
      <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-sm">
          <h3 className="text-xl font-bold text-gray-900 mb-1">Rate the Donor</h3>
          <p className="text-gray-500 text-sm mb-6">How was your experience with <span className="font-semibold text-gray-800">{ratingModal.donorName}</span>?</p>
          <div className="flex justify-center gap-3 mb-6">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                onClick={() => handleRate(ratingModal.transactionId, star)}
                className="group flex flex-col items-center gap-1"
              >
                <Star className="w-10 h-10 text-gray-300 group-hover:text-amber-400 group-hover:fill-amber-400 transition-all" />
                <span className="text-xs text-gray-400">{star}</span>
              </button>
            ))}
          </div>
          <button
            onClick={() => setRatingModal(null)}
            className="w-full py-2 text-sm text-gray-500 hover:text-gray-700 border border-gray-200 rounded-lg"
          >
            Cancel
          </button>
        </div>
      </div>
    )}
    </>
  );
};

export default NgoFeed;
