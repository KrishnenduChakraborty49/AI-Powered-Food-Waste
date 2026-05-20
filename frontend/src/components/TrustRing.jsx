import React from 'react';

const TrustRing = ({ score, totalRatings }) => {
  // score is out of 5.0
  const percentage = (score / 5.0) * 100;
  
  const getRingColor = (s) => {
    if (s >= 4.5) return 'text-green-500';
    if (s >= 3.5) return 'text-amber-500';
    return 'text-red-500';
  };

  return (
    <div className="flex flex-col items-center justify-center p-4 bg-white rounded-xl shadow-sm border border-gray-100">
      <div className="relative w-24 h-24">
        {/* Background Ring */}
        <svg className="w-full h-full" viewBox="0 0 36 36">
          <path
            className="text-gray-100"
            d="M18 2.0845
              a 15.9155 15.9155 0 0 1 0 31.831
              a 15.9155 15.9155 0 0 1 0 -31.831"
            fill="none"
            stroke="currentColor"
            strokeWidth="3"
          />
          {/* Progress Ring */}
          <path
            className={`${getRingColor(score)} transition-all duration-1000 ease-out`}
            strokeDasharray={`${percentage}, 100`}
            d="M18 2.0845
              a 15.9155 15.9155 0 0 1 0 31.831
              a 15.9155 15.9155 0 0 1 0 -31.831"
            fill="none"
            stroke="currentColor"
            strokeWidth="3"
            strokeLinecap="round"
          />
        </svg>
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 flex flex-col items-center">
          <span className="text-xl font-bold text-gray-800">{score.toFixed(1)}</span>
        </div>
      </div>
      <div className="mt-3 text-center">
        <h4 className="text-sm font-semibold text-gray-700">Trust Score</h4>
        <p className="text-xs text-gray-500">{totalRatings} ratings</p>
      </div>
    </div>
  );
};

export default TrustRing;
