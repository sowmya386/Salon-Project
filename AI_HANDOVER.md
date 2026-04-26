# 🚀 Salon SaaS Platform: Complete System Architecture & AI Handover Context

*Purpose:* Provide complete, unabridged context to any AI Agent, ChatGPT, or Developer taking over this repository so they instantly understand the tech stack, the deployment structure, and the current state of the code.

---

## 1. Application Overview
This is a modern, enterprise-grade, multi-role "Software as a Service" (SaaS) application built for Salon Management. 
* *Key Features:* Customer booking, admin dashboard, staff management, retail product purchasing, simulated mock-payments (PhonePe, Google Pay), and automated notification systems.
* *Core Philosophy:* Zero-cost startup architecture. The system relies heavily on free tiers (Supabase, Render, Vercel) with "smart mock fallbacks" built directly into the Java/React layers so that testing costs nothing.

---

## 2. Tech Stack & Repository Setup
### Frontend (The UI Layer)
* *GitHub Repository:* sowmya386/Salon-frontend
* *Framework:* React.js powered by Vite.
* *Styling:* TailwindCSS + Lucide Icons.
* *API Communication:* Axios (axios.js interceptors inject JWT bearer tokens).
* *Live Deployment:* Hosted on *Vercel* (https://salon-frontend-pied.vercel.app).
* *Critical Note:* The frontend repo contains a subfolder named salon-frontend. Vercel's "Root Directory" must be set to this folder, and a vercel.json file handles SPA routing fallbacks.

### Backend (The API Layer)
* *GitHub Repository:* sowmya386/Salon-Project
* *Framework:* Spring Boot 3 + Java 20 (built via Maven).
* *Security:* Spring Security with stateless JWT Authentication.
* *Live Deployment:* Hosted as a Web Service on *Render* using a custom Dockerfile (https://salon-project-5f0z.onrender.com).

### Database (The Persistence Layer)
* *Host:* *Supabase* (PostgreSQL pooler).
* The database is live and centralized. Both local testing (localhost:8081) and the production backend (Render) connect to the exact same Supabase cloud database provided in application.properties.

---

## 3. Environment Variables (Critical for Deployment)

### Frontend Variables (Vercel)
* VITE_API_URL = https://salon-project-5f0z.onrender.com/api (Tells React frontend where the live backend is).

### Backend Variables (Render / local .env)
The backend is designed to run perfectly with zero environment variables by aggressively using fallback mock logic. However, for production perfection, these variables exist:
* SUPABASE_JWT_SECRET: For OAuth/Supabase identity syncing (if enabled).
* STRIPE_SECRET_KEY: Leave completely blank to use the "Mock Mode" zero-cost simulated UI flow. Add a real key later to accept live credit cards.
* SMTP_HOST / SMTP_PORT / SMTP_USER / SMTP_PASS: Adding these variables instantly turns on real email sending. Without them, EmailService.java simply catches the error and gracefully logs the email to the console to prevent application crashes.

---

## 4. Key Architectural Decisions (DO NOT BREAK THESE)

1. *salonName Multi-tenant approach:* Instead of foreign key integer IDs, entities (like Bookings, Services, Users) map boundaries using the string salonName (e.g., John Salon). Do not revert this to salonId.
2. *Nullable Dates / NPE Prevention:* The Booking.java entity uses LocalDateTime appointmentTime. Previous NullPointerExceptions were patched by removing empty getter methods; the system securely logs dates via DateTimeFormatter.ofPattern.
3. *The "Mock Gateway" Setup:* Inside the React checkout screens (BookAppointment.jsx and Products.jsx), digital payments like "PhonePe" and "Google Pay" execute a Javascript await new Promise(...) to simulate a 2-second gateway delay before successfully posting to the backend. If upgrading to Razorpay/Stripe, replace these manual delays with proper SDK injections.

---

## 5. Live Application Links For Testing

* *Live Frontend:* [https://salon-frontend-pied.vercel.app](https://salon-frontend-pied.vercel.app)
* *Live Backend API:* [https://salon-project-5f0z.onrender.com/api](https://salon-project-5f0z.onrender.com/api)
* *API Health Test:* [https://salon-project-5f0z.onrender.com/api/salons](https://salon-project-5f0z.onrender.com/api/salons) (Returns JSON array of live salons indicating successful Database connection).
