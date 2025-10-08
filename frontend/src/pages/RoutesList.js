import React, { useState, useEffect } from 'react';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { routeAPI, segmentAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function RoutesList() {
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadRoutes = async () => {
      try {
        setLoading(true);
        const routesResponse = await routeAPI.getAllRoutes();
        const routesData = routesResponse.data;

        // Load segments for each route
        const routesWithSegments = await Promise.all(
          routesData.map(async (route) => {
            try {
              const segmentsResponse = await segmentAPI.getSegmentsByRouteOrdered(route.id);
              return {
                ...route,
                segments: segmentsResponse.data || []
              };
            } catch (error) {
              console.error(`Error loading segments for route ${route.id}:`, error);
              return {
                ...route,
                segments: []
              };
            }
          })
        );

        setRoutes(routesWithSegments);
      } catch (error) {
        console.error('Error loading routes:', error);
        toast.error('Failed to load routes');
      } finally {
        setLoading(false);
      }
    };

    loadRoutes();
  }, []);

  const formatRoutePath = (segments) => {
    if (!segments || segments.length === 0) {
      return 'No segments';
    }

    return segments
      .map(segment => segment.originAirportCode)
      .concat([segments[segments.length - 1].destinationAirportCode])
      .join(' → ');
  };

  const formatTime = (timeString) => {
    if (!timeString) return 'N/A';
    return timeString.substring(0, 5); // Remove seconds
  };

  if (loading) {
    return (
      <DispatcherLayout>
        <DispatcherSidebar />
        <div style={{
          flex: 1,
          padding: '24px',
          backgroundColor: '#F8FAFC',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center'
        }}>
          <div style={{
            fontSize: '18px',
            color: '#4A5568',
            fontFamily: 'Inter, sans-serif'
          }}>
            Loading routes...
          </div>
        </div>
      </DispatcherLayout>
    );
  }

  return (
    <DispatcherLayout>
      <DispatcherSidebar />
      <div style={{
        flex: 1,
        padding: '24px',
        backgroundColor: '#F8FAFC',
        overflowY: 'auto'
      }}>
        <h1 style={{
          fontSize: '28px',
          fontWeight: 'bold',
          color: '#1A202C',
          marginBottom: '24px',
          fontFamily: 'Inter, sans-serif'
        }}>
          Routes Management
        </h1>

        {/* Routes Table */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
        }}>
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '20px'
          }}>
            <h2 style={{
              fontSize: '18px',
              fontWeight: '600',
              color: '#1A202C',
              margin: 0,
              fontFamily: 'Inter, sans-serif'
            }}>
              All Routes ({routes.length})
            </h2>
          </div>

          {routes.length === 0 ? (
            <div style={{
              textAlign: 'center',
              padding: '40px',
              color: '#718096',
              fontFamily: 'Inter, sans-serif'
            }}>
              No routes found. Create your first route to get started.
            </div>
          ) : (
            <div style={{
              border: '1px solid #E2E8F0',
              borderRadius: '8px',
              overflow: 'hidden'
            }}>
              <table style={{
                width: '100%',
                borderCollapse: 'collapse',
                fontFamily: 'Inter, sans-serif'
              }}>
                <thead>
                  <tr style={{ backgroundColor: '#F7FAFC' }}>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Route</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Path</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Segments</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Total Distance</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Created</th>
                  </tr>
                </thead>
                <tbody>
                  {routes.map((route, index) => (
                    <tr key={route.id}>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0',
                        fontWeight: '500'
                      }}>
                        {route.name}
                      </td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0',
                        fontFamily: 'monospace'
                      }}>
                        {formatRoutePath(route.segments)}
                      </td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>
                        {route.segments.length}
                      </td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>
                        {route.totalDistance} km
                      </td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>
                        {new Date(route.createdAt).toLocaleDateString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </DispatcherLayout>
  );
}

