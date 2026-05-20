import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { MapPin, Navigation, KeyRound, CheckCircle2 } from 'lucide-react';
import toast from 'react-hot-toast';
import NotificationBell from '../components/NotificationBell';

const VolunteerDashboard = () => {
  const { logout } = useAuth();
  const [deliveries, setDeliveries] = useState([]);
  const [otp, setOtp] = useState('');
  const [activeDeliveryId, setActiveDeliveryId] = useState(null);

  useEffect(() => {
    fetchAvailableDeliveries();
  }, []);

  const fetchAvailableDeliveries = async () => {
    try {
      const response = await api.get('/volunteer/available');
      setDeliveries(response.data.content || []);
    } catch (error) {
      console.error("Failed to fetch available deliveries", error);
    }
  };

  const handleAccept = async (id) => {
    try {
      await api.post(`/volunteer/${id}/accept`);
      toast.success('Delivery accepted! Please proceed to pickup.');
      fetchAvailableDeliveries();
    } catch (error) {
      toast.error("Failed to accept delivery");
      console.error("Failed to accept delivery", error);
    }
  };

  const handleVerifyOtp = async (id) => {
    try {
      await api.post(`/transactions/${id}/confirm-otp?otpCode=${otp}`);
      setOtp('');
      setActiveDeliveryId(null);
      toast.success('OTP Verified! Proceed with delivery.');
      fetchAvailableDeliveries(); // Refresh to see updated status
    } catch (error) {
      toast.error("Invalid OTP Code");
    }
  };

  const handleDelivered = async (id) => {
    try {
      await api.post(`/volunteer/${id}/delivered`);
      toast.success('Delivery marked as complete! You earned 20 points.');
      fetchAvailableDeliveries();
    } catch (error) {
      toast.error("Failed to mark delivered");
      console.error("Failed to mark delivered", error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6 md:p-10">
      <div className="max-w-6xl mx-auto space-y-8">
        <header className="flex justify-between items-center pb-6 border-b border-gray-200">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Volunteer Map</h1>
            <p className="text-gray-500 mt-1">Available food rescues near you</p>
          </div>
          <div className="flex items-center gap-3">
            <NotificationBell />
            <button onClick={logout} className="text-gray-500 hover:text-gray-700 font-medium text-sm">Logout</button>
          </div>
        </header>

        {/* Mock Map View */}
        <div className="bg-gray-200 h-64 rounded-2xl w-full flex items-center justify-center relative overflow-hidden shadow-sm">
          <div className="absolute inset-0 opacity-20 bg-[url('https://maps.googleapis.com/maps/api/staticmap?center=37.7749,-122.4194&zoom=13&size=800x400&sensor=false')] bg-cover bg-center"></div>
          <div className="bg-white/90 backdrop-blur px-6 py-4 rounded-xl shadow-lg z-10 flex flex-col items-center">
            <Navigation className="w-8 h-8 text-primary-500 mb-2" />
            <h3 className="font-bold text-gray-800">Live Tracking Mode</h3>
            <p className="text-sm text-gray-500">Map view is simplified for this demo.</p>
          </div>
        </div>

        <h2 className="text-xl font-bold text-gray-800 mb-4 flex items-center">
          <span className="w-2 h-6 bg-primary-500 rounded-full mr-3"></span>
          Nearby Deliveries
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {deliveries.map((delivery) => (
            <div key={delivery.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
              
              <div className="flex justify-between items-start mb-4">
                <h3 className="text-lg font-bold text-gray-900">{delivery.foodName}</h3>
                <span className={`text-xs font-bold px-2 py-1 rounded-full ${
                  delivery.deliveryStatus === 'PENDING' ? 'bg-amber-100 text-amber-700' :
                  delivery.deliveryStatus === 'IN_TRANSIT' ? 'bg-blue-100 text-blue-700' :
                  'bg-green-100 text-green-700'
                }`}>
                  {delivery.deliveryStatus}
                </span>
              </div>

              <div className="space-y-3 text-sm text-gray-600 mb-6">
                <div className="flex items-start">
                  <MapPin className="w-4 h-4 mr-2 mt-0.5 text-gray-400 flex-shrink-0" />
                  <div>
                    <p className="font-medium text-gray-900">Pickup</p>
                    <p>{delivery.donorName}</p>
                  </div>
                </div>
                <div className="flex items-start">
                  <MapPin className="w-4 h-4 mr-2 mt-0.5 text-primary-400 flex-shrink-0" />
                  <div>
                    <p className="font-medium text-gray-900">Drop-off</p>
                    <p>{delivery.ngoName}</p>
                  </div>
                </div>
              </div>

              <div className="border-t border-gray-50 pt-4">
                {!delivery.volunteerName ? (
                  <button onClick={() => handleAccept(delivery.id)} className="w-full py-2 bg-primary-600 hover:bg-primary-500 text-white rounded-lg font-medium transition-colors">
                    Accept Delivery
                  </button>
                ) : !delivery.otpVerified ? (
                  activeDeliveryId === delivery.id ? (
                    <div className="flex space-x-2">
                      <input 
                        type="text" 
                        placeholder="6-digit OTP" 
                        maxLength={6}
                        value={otp}
                        onChange={(e) => setOtp(e.target.value)}
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500"
                      />
                      <button onClick={() => handleVerifyOtp(delivery.id)} className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-500">
                        Verify
                      </button>
                    </div>
                  ) : (
                    <button onClick={() => setActiveDeliveryId(delivery.id)} className="w-full py-2 flex items-center justify-center bg-amber-500 hover:bg-amber-400 text-white rounded-lg font-medium transition-colors">
                      <KeyRound className="w-4 h-4 mr-2" /> Enter Pickup OTP
                    </button>
                  )
                ) : delivery.deliveryStatus === 'IN_TRANSIT' ? (
                  <button onClick={() => handleDelivered(delivery.id)} className="w-full py-2 flex items-center justify-center bg-green-600 hover:bg-green-500 text-white rounded-lg font-medium transition-colors">
                    <CheckCircle2 className="w-4 h-4 mr-2" /> Mark Delivered
                  </button>
                ) : (
                  <button disabled className="w-full py-2 bg-gray-100 text-gray-400 rounded-lg font-medium">
                    Delivery Complete
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>

      </div>
    </div>
  );
};

export default VolunteerDashboard;
