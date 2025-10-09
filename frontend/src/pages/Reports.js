import React, { useState, useEffect, useRef } from 'react';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { reportsAPI } from '../services/api';
import toast from 'react-hot-toast';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

export default function Reports() {
  const reportRef = useRef(null);
  const [loading, setLoading] = useState(false);
  const [dateRange, setDateRange] = useState({
    startDate: new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().split('T')[0],
    endDate: new Date().toISOString().split('T')[0]
  });

  const [reports, setReports] = useState({
    flightsPerAircraftRoute: [],
    flightStatuses: { completed: 0, canceled: 0 },
    avgTimeBetweenServices: [],
    planChangeFrequency: { avgChanges: 0, mostChanges: '' }
  });

  useEffect(() => {
    loadReports();
  }, [dateRange]);

  const loadReports = async () => {
    try {
      setLoading(true);
      
      // Get all reports from backend
      const response = await reportsAPI.getAllReports(dateRange.startDate, dateRange.endDate);
      const data = response.data;

      setReports({
        flightsPerAircraftRoute: data.flightsPerAircraftRoute || [],
        flightStatuses: data.flightStatuses || { completed: 0, canceled: 0 },
        avgTimeBetweenServices: data.avgTimeBetweenServices || [],
        planChangeFrequency: data.planChangeFrequency || { avgChanges: 0, mostChanges: '' }
      });
    } catch (error) {
      console.error('Error loading reports:', error);
      toast.error('Failed to load reports');
    } finally {
      setLoading(false);
    }
  };

  const generatePDF = async () => {
    try {
      toast.loading('Generating PDF...', { duration: 2000 });
      
      if (!reportRef.current) {
        toast.error('Report content not found');
        return;
      }

      // Create a custom PDF with styled content
      const pdf = new jsPDF('p', 'mm', 'a4');
      const pageWidth = pdf.internal.pageSize.getWidth();
      const pageHeight = pdf.internal.pageSize.getHeight();
      let yPosition = 20;

      // Add header
      pdf.setFontSize(20);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Flight Operations Report', pageWidth / 2, yPosition, { align: 'center' });
      
      yPosition += 10;
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'normal');
      const currentDate = new Date().toLocaleDateString('en-GB');
      pdf.text(`Generated on: ${currentDate}`, pageWidth / 2, yPosition, { align: 'center' });
      
      yPosition += 15;
      pdf.text(`Report Period: ${dateRange.startDate} to ${dateRange.endDate}`, pageWidth / 2, yPosition, { align: 'center' });
      yPosition += 20;

      // Add line separator
      pdf.setDrawColor(200, 200, 200);
      pdf.line(20, yPosition, pageWidth - 20, yPosition);
      yPosition += 10;

      // Flight Statuses Section
      pdf.setFontSize(16);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Flight Status Overview', 20, yPosition);
      yPosition += 10;
      
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`Completed: ${reports.flightStatuses.completed}`, 20, yPosition);
      yPosition += 7;
      pdf.text(`Canceled: ${reports.flightStatuses.canceled}`, 20, yPosition);
      yPosition += 7;
      pdf.text(`Delayed: ${reports.flightStatuses.delayed || 0}`, 20, yPosition);
      yPosition += 7;
      pdf.text(`Scheduled: ${reports.flightStatuses.scheduled || 0}`, 20, yPosition);
      yPosition += 7;
      pdf.text(`Boarding: ${reports.flightStatuses.boarding || 0}`, 20, yPosition);
      yPosition += 7;
      pdf.text(`Departed: ${reports.flightStatuses.departed || 0}`, 20, yPosition);
      yPosition += 15;

      // Check if we need a new page
      if (yPosition > pageHeight - 60) {
        pdf.addPage();
        yPosition = 20;
      }

      // Flights per Aircraft & Route Section
      pdf.setFontSize(16);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Flights per Aircraft & Route', 20, yPosition);
      yPosition += 10;
      
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'normal');
      reports.flightsPerAircraftRoute.forEach(item => {
        pdf.text(`${item.aircraft}: ${item.count} flights across ${item.routes} routes`, 20, yPosition);
        yPosition += 7;
        
        if (yPosition > pageHeight - 40) {
          pdf.addPage();
          yPosition = 20;
        }
      });
      yPosition += 10;

      // Average Time Between Services Section
      pdf.setFontSize(16);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Average Time Between Services', 20, yPosition);
      yPosition += 10;
      
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'normal');
      reports.avgTimeBetweenServices.forEach(item => {
        pdf.text(`${item.aircraft}: ${item.avgHours}h average (${item.intervals} intervals)`, 20, yPosition);
        yPosition += 7;
        
        if (yPosition > pageHeight - 40) {
          pdf.addPage();
          yPosition = 20;
        }
      });
      yPosition += 10;

      // Plan Change Frequency Section
      pdf.setFontSize(16);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Plan Change Frequency', 20, yPosition);
      yPosition += 10;
      
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`Most Problematic Route: ${reports.planChangeFrequency.mostChanges}`, 20, yPosition);
      yPosition += 7;
      pdf.text(`Average Change Rate: ${(reports.planChangeFrequency.avgChanges * 100).toFixed(1)}%`, 20, yPosition);

      // Add footer
      const footerY = pageHeight - 20;
      pdf.setFontSize(10);
      pdf.setFont('helvetica', 'italic');
      pdf.text('Generated by Air Company Flight Management System', pageWidth / 2, footerY, { align: 'center' });

      // Save the PDF
      const fileName = `flight-report-${currentDate.replace(/\//g, '-')}.pdf`;
      pdf.save(fileName);
      
      toast.success('PDF generated successfully!');
      
    } catch (error) {
      console.error('Error generating PDF:', error);
      toast.error('Failed to generate PDF');
    }
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
            Loading reports...
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
        fontFamily: 'Inter, sans-serif'
      }}>
        {/* Header */}
        <div style={{
          marginBottom: '24px'
        }}>
          <h1 style={{
            fontSize: '28px',
            fontWeight: '600',
            color: '#1A202C',
            marginBottom: '8px'
          }}>
            Reports & Statistics
          </h1>
        </div>

        {/* Date Range and PDF Button */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '20px',
          marginBottom: '24px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <label style={{
              fontSize: '14px',
              fontWeight: '500',
              color: '#4A5568'
            }}>
              Period:
            </label>
            <input
              type="date"
              value={dateRange.startDate}
              onChange={(e) => setDateRange({ ...dateRange, startDate: e.target.value })}
              style={{
                padding: '8px 12px',
                border: '1px solid #D1D5DB',
                borderRadius: '6px',
                fontSize: '14px'
              }}
            />
            <span style={{ color: '#6B7280' }}>—</span>
            <input
              type="date"
              value={dateRange.endDate}
              onChange={(e) => setDateRange({ ...dateRange, endDate: e.target.value })}
              style={{
                padding: '8px 12px',
                border: '1px solid #D1D5DB',
                borderRadius: '6px',
                fontSize: '14px'
              }}
            />
          </div>
          <button
            onClick={generatePDF}
            style={{
              padding: '10px 24px',
              backgroundColor: '#3182CE',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontSize: '14px',
              fontWeight: '500',
              cursor: 'pointer'
            }}
          >
            PDF
          </button>
        </div>

        {/* Reports Grid */}
        <div ref={reportRef} style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(2, 1fr)',
          gap: '24px'
        }}>
          {/* Flights per Aircraft & Route */}
          <div style={{
            backgroundColor: 'white',
            borderRadius: '12px',
            padding: '24px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
          }}>
            <h2 style={{
              fontSize: '18px',
              fontWeight: '600',
              color: '#1A202C',
              marginBottom: '16px'
            }}>
              Flights per Aircraft & Route
            </h2>
            <div style={{ color: '#4A5568', fontSize: '14px', lineHeight: '1.8' }}>
              {reports.flightsPerAircraftRoute.length > 0 ? (
                reports.flightsPerAircraftRoute.map((item, idx) => (
                  <div key={idx}>
                    {item.aircraft} — {item.count} flights ({item.routes} routes)
                  </div>
                ))
              ) : (
                <div style={{ color: '#9CA3AF' }}>No data available</div>
              )}
            </div>
          </div>

          {/* Flight Statuses */}
          <div style={{
            backgroundColor: 'white',
            borderRadius: '12px',
            padding: '24px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
          }}>
            <h2 style={{
              fontSize: '18px',
              fontWeight: '600',
              color: '#1A202C',
              marginBottom: '16px'
            }}>
              Flight Statuses
            </h2>
            <div style={{ color: '#4A5568', fontSize: '14px', lineHeight: '1.8' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                <span>✅ Completed:</span>
                <span style={{ fontWeight: '500' }}>{reports.flightStatuses.completed}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                <span>❌ Canceled:</span>
                <span style={{ fontWeight: '500', color: '#DC2626' }}>{reports.flightStatuses.canceled}</span>
              </div>
              {reports.flightStatuses.delayed > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span>⏰ Delayed:</span>
                  <span style={{ fontWeight: '500', color: '#F59E0B' }}>{reports.flightStatuses.delayed}</span>
                </div>
              )}
              {reports.flightStatuses.scheduled > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span>📅 Scheduled:</span>
                  <span style={{ fontWeight: '500', color: '#3182CE' }}>{reports.flightStatuses.scheduled}</span>
                </div>
              )}
              {reports.flightStatuses.boarding > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span>🚶 Boarding:</span>
                  <span style={{ fontWeight: '500', color: '#059669' }}>{reports.flightStatuses.boarding}</span>
                </div>
              )}
              {reports.flightStatuses.departed > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span>✈️ Departed:</span>
                  <span style={{ fontWeight: '500', color: '#059669' }}>{reports.flightStatuses.departed}</span>
                </div>
              )}
            </div>
          </div>

          {/* Avg Time Between Services */}
          <div style={{
            backgroundColor: 'white',
            borderRadius: '12px',
            padding: '24px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
          }}>
            <h2 style={{
              fontSize: '18px',
              fontWeight: '600',
              color: '#1A202C',
              marginBottom: '16px'
            }}>
              Avg Time Between Services
            </h2>
            <div style={{ color: '#4A5568', fontSize: '14px', lineHeight: '1.8' }}>
              {reports.avgTimeBetweenServices.length > 0 ? (
                reports.avgTimeBetweenServices.map((item, idx) => (
                  <div key={idx} style={{ marginBottom: '8px' }}>
                    <div style={{ fontWeight: '500' }}>{item.aircraft}</div>
                    <div style={{ fontSize: '13px', color: '#6B7280' }}>
                      Avg: {item.avgHours}h between services ({item.intervals} intervals)
                    </div>
                  </div>
                ))
              ) : (
                <div style={{ color: '#9CA3AF' }}>No service data available</div>
              )}
            </div>
          </div>

          {/* Plan Change Frequency */}
          <div style={{
            backgroundColor: 'white',
            borderRadius: '12px',
            padding: '24px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
          }}>
            <h2 style={{
              fontSize: '18px',
              fontWeight: '600',
              color: '#1A202C',
              marginBottom: '16px'
            }}>
              Plan Change Frequency
            </h2>
            <div style={{ color: '#4A5568', fontSize: '14px', lineHeight: '1.8' }}>
              <div style={{ marginBottom: '12px' }}>
                <div style={{ fontWeight: '500', marginBottom: '4px' }}>Average Change Rate</div>
                <div style={{ fontSize: '16px', color: '#3182CE' }}>
                  {reports.planChangeFrequency.avgChanges ? 
                    `${(reports.planChangeFrequency.avgChanges * 100).toFixed(1)}%` : 
                    '0%'
                  }
                </div>
                <div style={{ fontSize: '12px', color: '#6B7280' }}>
                  (delayed/canceled flights per route)
                </div>
              </div>
              <div>
                <div style={{ fontWeight: '500', marginBottom: '4px' }}>Most Problematic Route</div>
                <div style={{ color: '#DC2626' }}>
                  {reports.planChangeFrequency.mostChanges || 'No data available'}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer Note */}
        <div style={{
          marginTop: '24px',
          textAlign: 'center',
          color: '#9CA3AF',
          fontSize: '14px'
        }}>
          Only the four reports required by specification are shown above.
        </div>
      </div>
    </DispatcherLayout>
  );
}

