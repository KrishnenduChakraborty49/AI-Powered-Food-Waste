import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import DonorDashboard from './pages/DonorDashboard';
import NgoFeed from './pages/NgoFeed';
import VolunteerDashboard from './pages/VolunteerDashboard';
import AdminDashboard from './pages/AdminDashboard';

const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, loading } = useAuth();
  
  if (loading) return <div>Loading...</div>;
  
  if (!user) {
    return <Navigate to="/login" />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/" />;
  }

  return children;
};

function App() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      
      <Route 
        path="/donor" 
        element={
          <ProtectedRoute allowedRoles={['DONOR']}>
            <DonorDashboard />
          </ProtectedRoute>
        } 
      />
      
      <Route 
        path="/ngo" 
        element={
          <ProtectedRoute allowedRoles={['NGO']}>
            <NgoFeed />
          </ProtectedRoute>
        } 
      />

      <Route 
        path="/volunteer" 
        element={
          <ProtectedRoute allowedRoles={['VOLUNTEER']}>
            <VolunteerDashboard />
          </ProtectedRoute>
        } 
      />

      <Route 
        path="/admin" 
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AdminDashboard />
          </ProtectedRoute>
        } 
      />

      {/* Default route redirect based on role */}
      <Route 
        path="/" 
        element={
          user ? (
            user.role === 'DONOR' ? <Navigate to="/donor" /> :
            user.role === 'NGO' ? <Navigate to="/ngo" /> :
            user.role === 'VOLUNTEER' ? <Navigate to="/volunteer" /> :
            user.role === 'ADMIN' ? <Navigate to="/admin" /> :
            <Navigate to="/login" />
          ) : (
            <Navigate to="/login" />
          )
        } 
      />
    </Routes>
  );
}

export default App;
