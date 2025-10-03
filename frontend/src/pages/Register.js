import React, { useState } from "react";
import { Link } from "react-router-dom";

export default function Register() {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: ""
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (formData.password !== formData.confirmPassword) {
      alert("Passwords don't match!");
      return;
    }
    
    console.log("Register submitted:", formData);
    // TODO: Implement actual registration logic
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
          Register
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-[5px]">
          <div>
            <label 
              htmlFor="firstName"
              className="block mb-[5px]"
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px",
                fontWeight: 400,
                color: "#738396",
                lineHeight: "15px"
              }}
            >
              first name
            </label>
            <input
              id="firstName"
              name="firstName"
              type="text"
              value={formData.firstName}
              onChange={handleInputChange}
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
              htmlFor="lastName"
              className="block mb-[5px]"
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px",
                fontWeight: 400,
                color: "#738396",
                lineHeight: "15px"
              }}
            >
              last name
            </label>
            <input
              id="lastName"
              name="lastName"
              type="text"
              value={formData.lastName}
              onChange={handleInputChange}
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
              name="email"
              type="email"
              value={formData.email}
              onChange={handleInputChange}
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
              name="password"
              type="password"
              value={formData.password}
              onChange={handleInputChange}
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
              htmlFor="confirmPassword"
              className="block mb-[5px]"
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px",
                fontWeight: 400,
                color: "#738396",
                lineHeight: "15px"
              }}
            >
              confirm password
            </label>
            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              value={formData.confirmPassword}
              onChange={handleInputChange}
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
            className="w-full h-[40px] rounded-lg mt-[14px] hover:opacity-90 transition-opacity"
            style={{
              backgroundColor: "#3F8EFC",
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              fontSize: "14px",
              fontWeight: 700,
              fontStyle: "italic",
              color: "#FFFFFF",
              textDecoration: "underline",
              border: "none",
              cursor: "pointer"
            }}
          >
            Register
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
            Already have an account?{" "}
          </span>
          <Link 
            to="/login"
            className="hover:opacity-80 transition-opacity"
            style={{
              color: "#3F8EFC",
              fontWeight: 700,
              fontStyle: "italic",
              textDecoration: "underline"
            }}
          >
            Log in
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