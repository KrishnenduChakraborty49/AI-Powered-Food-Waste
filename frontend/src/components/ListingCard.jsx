import React from 'react';
import { Clock, MapPin, Package } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const ListingCard = ({ listing, onClaim }) => {
  const { foodName, foodType, quantity, unit, expiryTime, priorityScore, status } = listing;
  const { user } = useAuth();

  const getScoreColor = (score) => {
    if (score >= 80) return 'bg-red-500';
    if (score >= 50) return 'bg-amber-500';
    return 'bg-green-500';
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow relative overflow-hidden">
      <div className={`absolute top-0 left-0 w-full h-1 ${getScoreColor(priorityScore)}`}></div>
      
      <div className="flex justify-between items-start mb-4 mt-1">
        <div>
          <h3 className="text-lg font-bold text-gray-900">{foodName}</h3>
          <span className="inline-block px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded-md mt-1 font-medium">
            {foodType}
          </span>
        </div>
        <div className="flex flex-col items-end">
          <span className={`text-xs font-bold px-2 py-1 rounded-full ${
            status === 'AVAILABLE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
          }`}>
            {status}
          </span>
          <span className="text-xs text-gray-500 mt-1 font-medium">
            Score: {priorityScore}
          </span>
        </div>
      </div>

      <div className="space-y-2 text-sm text-gray-600">
        <div className="flex items-center">
          <Package className="w-4 h-4 mr-2 text-gray-400" />
          <span>{quantity} {unit}</span>
        </div>
        <div className="flex items-center">
          <Clock className="w-4 h-4 mr-2 text-gray-400" />
          <span>Expires: {new Date(expiryTime).toLocaleString()}</span>
        </div>
        <div className="flex items-center">
          <MapPin className="w-4 h-4 mr-2 text-gray-400" />
          <span>
            <a 
              href={`https://www.google.com/maps/search/?api=1&query=${listing.lat},${listing.lng}`} 
              target="_blank" 
              rel="noopener noreferrer" 
              className="text-blue-500 font-medium cursor-pointer hover:underline"
            >
              View Location
            </a>
          </span>
        </div>
      </div>

      <div className="mt-5 pt-4 border-t border-gray-50 flex justify-end">
        {status === 'AVAILABLE' && user?.role === 'NGO' && (
          <button 
            onClick={() => onClaim && onClaim(listing.id)}
            className="px-4 py-2 bg-primary-600 hover:bg-primary-500 text-white text-sm font-medium rounded-lg transition-colors"
          >
            Claim Food
          </button>
        )}
      </div>
    </div>
  );
};

export default ListingCard;
