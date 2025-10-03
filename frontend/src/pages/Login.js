import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authAPI } from "../services/api";
import toast from "react-hot-toast";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await authAPI.login({ email, password });
      
      if (response.data) {
        // Store user data and token
        if (response.data.token) {
          localStorage.setItem('authToken', response.data.token);
        }
        
        if (response.data.user || response.data.customer) {
          const userData = response.data.user || response.data.customer;
          localStorage.setItem('user', JSON.stringify(userData));
          
          toast.success(`Welcome back, ${userData.firstName || userData.email}!`);
          
          // Redirect to dashboard or home
          navigate('/dashboard');
        } else {
          toast.success('Login successful!');
          navigate('/');
        }
      }
    } catch (error) {
      console.error('Login error:', error);
      const errorMessage = error.response?.data?.message || 'Login failed. Please check your credentials.';
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: "#EEF4FB" }}>
      <div 
        className="w-[300px] bg-white rounded-[14px] p-0 shadow-sm"
        style={{ 
          border: "1px solid #D9E1EA",
          padding: "30px 20px"
        }}
      >
        <h1 
          className="text-center mb-[14px]"
          style={{
            fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
            fontSize: "20px",
            fontWeight: 700,
            fontStyle: "italic",
            color: "#2F3E4D",
            lineHeight: "24px"
          }}
        >
          Log in
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-[5px]">
          <div>
            <label 
              htmlFor="email"
              className="block mb-[5px]"
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px",
                fontWeight: 400,
                color: "#738396",
                lineHeight: "15px"
              }}
            >
              email
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full h-[32px] rounded-md px-3 outline-none"
              style={{
                backgroundColor: "#F6F8FB",
                border: "1px solid #D9E1EA",
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px"
              }}
              required
            />
          </div>

          <div className="mt-[8px]">
            <label 
              htmlFor="password"
              className="block mb-[5px]"
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px",
                fontWeight: 400,
                color: "#738396",
                lineHeight: "15px"
              }}
            >
              password
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full h-[32px] rounded-md px-3 outline-none"
              style={{
                backgroundColor: "#F6F8FB",
                border: "1px solid #D9E1EA",
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px"
              }}
              required
            />
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full h-[40px] rounded-lg mt-[14px] hover:opacity-90 transition-opacity disabled:opacity-50"
            style={{
              backgroundColor: "#3F8EFC",
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              fontSize: "14px",
              fontWeight: 700,
              fontStyle: "italic",
              color: "#FFFFFF",
              textDecoration: "underline",
              border: "none",
              cursor: isLoading ? "not-allowed" : "pointer"
            }}
          >
            {isLoading ? "Logging in..." : "Log in"}
          </button>
        </form>

        <div 
          className="mt-[28px] text-center"
          style={{
            fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
            fontSize: "12px",
            lineHeight: "15px"
          }}
        >
          <span style={{ color: "#6B7785", fontWeight: 400 }}>
            Don't have an account yet?{" "}
          </span>
          <Link 
            to="/register"
            className="hover:opacity-80 transition-opacity"
            style={{
              color: "#3F8EFC",
              fontWeight: 700,
              fontStyle: "italic",
              textDecoration: "underline"
            }}
          >
            Register
          </Link>
        </div>

        <div className="mt-4 text-center">
          <Link 
            to="/"
            className="hover:opacity-80 transition-opacity"
            style={{
              color: "#738396",
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              fontSize: "12px",
              textDecoration: "underline"
            }}
          >
            ← Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
}