import React, { useState, useEffect } from 'react';
import { analyticsAPI } from '../services/api';
import toast from 'react-hot-toast';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import './Analytics.css';

const Analytics = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [financialData, setFinancialData] = useState(null);
  const [occupancyData, setOccupancyData] = useState(null);
  const [routeData, setRouteData] = useState(null);
  const [loyaltyData, setLoyaltyData] = useState(null);
  const [seasonalData, setSeasonalData] = useState(null);
  const [cancellationData, setCancellationData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [dateRange, setDateRange] = useState({
    startDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    endDate: new Date().toISOString().split('T')[0]
  });
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());

  useEffect(() => {
    fetchAnalyticsData();
  }, [dateRange, selectedYear]);

  const fetchAnalyticsData = async () => {
    setLoading(true);
    try {
      const [dashboard, financial, occupancy, routes, loyalty, seasonal, cancellation] = await Promise.all([
        analyticsAPI.getDashboardSummary(),
        analyticsAPI.getFinancialIndicators(null, dateRange.startDate, dateRange.endDate),
        analyticsAPI.getOccupancyByCabinClass(null, dateRange.startDate, dateRange.endDate),
        analyticsAPI.getRoutePerformance(null, dateRange.startDate, dateRange.endDate),
        analyticsAPI.getLoyaltyStatistics(),
        analyticsAPI.getOccupancyBySeason(selectedYear),
        analyticsAPI.getCancellationRate(dateRange.startDate, dateRange.endDate)
      ]);

      setDashboardData(dashboard.data);
      setFinancialData(financial.data);
      setOccupancyData(occupancy.data);
      setRouteData(routes.data);
      setLoyaltyData(loyalty.data);
      setSeasonalData(seasonal.data);
      setCancellationData(cancellation.data);
      
      toast.success('Analytics data loaded successfully');
    } catch (error) {
      console.error('Error fetching analytics:', error);
      toast.error('Failed to load analytics data: ' + (error.response?.data || error.message));
    } finally {
      setLoading(false);
    }
  };

  const downloadPDF = async () => {
    try {
      toast.loading('Generating PDF...');
      const element = document.getElementById('analytics-content');
      const canvas = await html2canvas(element, {
        scale: 2,
        useCORS: true,
        logging: false
      });

      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'mm', 'a4');
      const imgWidth = 210;
      const pageHeight = 297;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;
      let position = 0;

      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      pdf.save(`analytics-report-${dateRange.startDate}-to-${dateRange.endDate}.pdf`);
      toast.dismiss();
      toast.success('PDF downloaded successfully');
    } catch (error) {
      console.error('Error generating PDF:', error);
      toast.dismiss();
      toast.error('Failed to generate PDF');
    }
  };

  if (loading) {
    return (
      <div className="analytics-main">
        <div className="loading-spinner">Loading analytics...</div>
      </div>
    );
  }

  return (
    <div className="analytics-main">
        <div className="analytics-header">
          <div>
            <h1>Analytics Dashboard</h1>
            <p className="analytics-subtitle">Business insights and performance metrics</p>
          </div>
          <div className="analytics-actions">
            <div className="date-range-selector">
              <label>
                From:
                <input
                  type="date"
                  value={dateRange.startDate}
                  onChange={(e) => setDateRange({ ...dateRange, startDate: e.target.value })}
                />
              </label>
              <label>
                To:
                <input
                  type="date"
                  value={dateRange.endDate}
                  onChange={(e) => setDateRange({ ...dateRange, endDate: e.target.value })}
                />
              </label>
            </div>
            <button className="download-pdf-btn" onClick={downloadPDF}>
              📄 Download PDF
            </button>
          </div>
        </div>

        <div id="analytics-content" className="analytics-content">
          {/* Financial Summary */}
          <div className="analytics-section">
            <h2>💰 Financial Overview</h2>
            <div className="metrics-grid">
              <div className="metric-card">
                <div className="metric-label">Total Revenue</div>
                <div className="metric-value">
                  ${financialData?.totalRevenue?.toLocaleString() || '0.00'}
                </div>
              </div>
              <div className="metric-card">
                <div className="metric-label">Average Ticket Price</div>
                <div className="metric-value">
                  ${financialData?.averageTicketPrice?.toLocaleString() || '0.00'}
                </div>
              </div>
              <div className="metric-card">
                <div className="metric-label">Tickets Sold</div>
                <div className="metric-value">
                  {financialData?.totalTicketsSold?.toLocaleString() || '0'}
                </div>
              </div>
              <div className="metric-card">
                <div className="metric-label">Revenue Per Seat</div>
                <div className="metric-value">
                  ${financialData?.revenuePerSeat?.toLocaleString() || '0.00'}
                </div>
              </div>
            </div>
          </div>

          {/* Occupancy Statistics */}
          <div className="analytics-section">
            <h2>✈️ Seat Occupancy by Cabin Class</h2>
            <div className="occupancy-grid">
              {occupancyData?.cabinClassStats?.map((cabin, index) => (
                <div key={index} className="occupancy-card">
                  <h3>{cabin.cabinClass}</h3>
                  <div className="occupancy-bar">
                    <div 
                      className="occupancy-fill"
                      style={{ width: `${cabin.occupancyRate}%` }}
                    >
                      <span className="occupancy-text">{cabin.occupancyRate}%</span>
                    </div>
                  </div>
                  <div className="occupancy-details">
                    <span>{cabin.soldSeats} / {cabin.totalCapacity} seats</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Route Performance */}
          <div className="analytics-section">
            <h2>🗺️ Top Performing Routes</h2>
            <div className="routes-table">
              <table>
                <thead>
                  <tr>
                    <th>Route</th>
                    <th>Tickets Sold</th>
                    <th>Total Revenue</th>
                    <th>Avg. Ticket Price</th>
                  </tr>
                </thead>
                <tbody>
                  {routeData?.routePerformance
                    ?.sort((a, b) => b.totalRevenue - a.totalRevenue)
                    ?.slice(0, 10)
                    ?.map((route, index) => (
                      <tr key={index}>
                        <td className="route-name">{route.routeName}</td>
                        <td>{route.ticketsSold?.toLocaleString()}</td>
                        <td className="revenue">${route.totalRevenue?.toLocaleString()}</td>
                        <td>${route.averageTicketPrice?.toFixed(2)}</td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Seasonal Occupancy Analysis */}
          <div className="analytics-section">
            <h2>🌍 Seasonal Occupancy Analysis</h2>
            <div className="season-controls">
              <label>
                Year:
                <select 
                  value={selectedYear} 
                  onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                >
                  <option value={2024}>2024</option>
                  <option value={2025}>2025</option>
                  <option value={2026}>2026</option>
                </select>
              </label>
            </div>
            <div className="seasonal-grid">
              {seasonalData?.seasonalStats?.map((season, index) => {
                const getSeasonIcon = (seasonName) => {
                  switch(seasonName) {
                    case 'Winter': return '❄️';
                    case 'Spring': return '🌸';
                    case 'Summer': return '☀️';
                    case 'Autumn': return '🍂';
                    default: return '🌍';
                  }
                };
                
                return (
                  <div key={index} className="season-card">
                    <h3>
                      {getSeasonIcon(season.season)} {season.season}
                    </h3>
                    <div className="season-stats">
                      <div className="stat-row">
                        <span>Tickets Sold:</span>
                        <strong>{season.ticketsSold?.toLocaleString() || '0'}</strong>
                      </div>
                      <div className="stat-row">
                        <span>Revenue:</span>
                        <strong>${season.totalRevenue?.toLocaleString() || '0'}</strong>
                      </div>
                      <div className="stat-row">
                        <span>Avg. Price:</span>
                        <strong>${season.averageTicketPrice?.toFixed(2) || '0.00'}</strong>
                      </div>
                      <div className="occupancy-indicator">
                        <div className="occupancy-bar-small">
                          <div 
                            className="occupancy-fill-small"
                            style={{ width: `${season.occupancyRate || 0}%` }}
                          ></div>
                        </div>
                        <span>{season.occupancyRate || 0}% Occupancy</span>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Cancellation Rate Statistics */}
          <div className="analytics-section">
            <h2>📊 Cancellation Rate Analysis</h2>
            <div className="cancellation-overview">
              <div className="cancellation-metrics">
                <div className="cancellation-metric">
                  <div className="metric-icon">📋</div>
                  <div className="metric-content">
                    <div className="metric-label">Total Tickets Issued</div>
                    <div className="metric-value">
                      {cancellationData?.totalTickets?.toLocaleString() || '0'}
                    </div>
                  </div>
                </div>
                <div className="cancellation-metric">
                  <div className="metric-icon">❌</div>
                  <div className="metric-content">
                    <div className="metric-label">Cancelled Tickets</div>
                    <div className="metric-value">
                      {cancellationData?.cancelledTickets?.toLocaleString() || '0'}
                    </div>
                  </div>
                </div>
                <div className="cancellation-metric">
                  <div className="metric-icon">📈</div>
                  <div className="metric-content">
                    <div className="metric-label">Cancellation Rate</div>
                    <div className="metric-value highlight">
                      {cancellationData?.cancellationRate?.toFixed(2) || '0.00'}%
                    </div>
                  </div>
                </div>
              </div>
              
              {cancellationData?.cancellationReasons && 
               Object.keys(cancellationData.cancellationReasons).length > 0 && (
                <div className="cancellation-reasons">
                  <h3>Cancellation Reasons</h3>
                  <div className="reasons-list">
                    {Object.entries(cancellationData.cancellationReasons).map(([reason, count]) => (
                      <div key={reason} className="reason-item">
                        <span className="reason-text">{reason || 'Not Specified'}</span>
                        <span className="reason-count">{count} tickets</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Loyalty Program Statistics */}
          <div className="analytics-section">
            <h2>🎁 Loyalty Program Insights</h2>
            <div className="loyalty-grid">
              <div className="loyalty-card">
                <h3>Total Points Distributed</h3>
                <div className="loyalty-value">
                  {loyaltyData?.totalPointsDistributed?.toLocaleString() || '0'}
                </div>
                <div className="loyalty-subtitle">Points</div>
              </div>
              <div className="loyalty-card">
                <h3>Members by Tier</h3>
                <div className="loyalty-tiers">
                  {Object.entries(loyaltyData?.membersByTier || {}).map(([tier, count]) => (
                    <div key={tier} className="tier-row">
                      <span className={`tier-badge ${tier.toLowerCase()}`}>{tier}</span>
                      <span className="tier-count">{count} members</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Payment Methods Distribution */}
          <div className="analytics-section">
            <h2>💳 Payment Methods Overview</h2>
            <div className="payment-summary">
              <p className="info-text">
                Most customers prefer credit card payments with an average transaction value of 
                ${financialData?.averageTicketPrice?.toFixed(2) || '0.00'}
              </p>
              <div className="payment-stats">
                <div className="payment-stat">
                  <span className="stat-icon">💳</span>
                  <span className="stat-text">Credit/Debit Cards</span>
                </div>
                <div className="payment-stat">
                  <span className="stat-icon">🎁</span>
                  <span className="stat-text">Loyalty Points</span>
                </div>
              </div>
            </div>
          </div>

          {/* Report Footer */}
          <div className="analytics-footer">
            <p>Report generated on {new Date().toLocaleString()}</p>
            <p>Date range: {dateRange.startDate} to {dateRange.endDate}</p>
          </div>
        </div>
      </div>
  );
};

export default Analytics;
