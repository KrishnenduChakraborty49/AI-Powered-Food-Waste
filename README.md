# 🍲 FoodLink - AI-Powered Food Waste Management

![Live Demo](https://img.shields.io/badge/Live-Demo-brightgreen.svg)
![React](https://img.shields.io/badge/Frontend-React_Vite-blue.svg)
![Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot-green.svg)
![Database](https://img.shields.io/badge/Database-MySQL-orange.svg)

FoodLink is a comprehensive full-stack platform designed to bridge the gap between food surplus and food scarcity. By seamlessly connecting Food Donors (restaurants, individuals), NGOs, and Delivery Volunteers, FoodLink ensures that excess food reaches those in need efficiently and safely.

🔗 **Live Demo:** [https://ai-powered-food-waste.vercel.app/](https://ai-powered-food-waste.vercel.app/)

---

## ✨ Key Features

### 🏢 1. Donor Module
* **Create Listings:** Restaurants and individuals can easily list surplus food with quantity, expiry time, and pickup location.
* **Map Integration:** Built-in Leaflet maps allow precise pinning of pickup locations.
* **Impact Tracking:** A personalized dashboard tracks total meals donated, CO2 emissions saved, and a community trust score.

### 🤝 2. NGO Module
* **Live Feed:** NGOs can browse active, nearby food donation listings in real-time.
* **Claim System:** NGOs can instantly claim food and coordinate pickup logistics.
* **Distribution Management:** Streamlines the process of getting claimed food to the final beneficiaries.

### 🚚 3. Volunteer Delivery Module
* **Transit Requests:** Volunteers can view and accept requests to transport food from Donors to NGOs.
* **Optimal Routing:** Integrated map routing helps volunteers find the quickest path between pickup and drop-off points.
* **Real-Time Status:** Update delivery status (Accepted, Picked Up, Delivered) in real-time.

### 🛡️ 4. Admin Dashboard
* **System Oversight:** Centralized console for monitoring overall platform activity and health.
* **User Verification:** Admins can verify and approve newly registered NGOs and Donors to maintain trust within the ecosystem.

---

## 🛠️ Technology Stack

### Frontend
* **Framework:** React.js (Vite)
* **Styling:** TailwindCSS
* **Maps:** React-Leaflet (OpenStreetMap)
* **Routing:** React Router v6
* **HTTP Client:** Axios

### Backend
* **Framework:** Java, Spring Boot 3
* **Security:** Spring Security & JWT (JSON Web Tokens) for role-based access control
* **Database Migration:** Flyway
* **API:** RESTful Architecture

### Database & Deployment
* **Database:** MySQL
* **Frontend Hosting:** Vercel
* **Backend Hosting:** Render

---

## 🚀 Local Setup & Installation

Follow these steps to run the project locally on your machine.

### Prerequisites
* Node.js (v18+)
* Java (JDK 17+)
* Maven
* MySQL Server

### 1. Clone the Repository
\`\`\`bash
git clone https://github.com/KrishnenduChakraborty49/AI-Powered-Food-Waste.git
cd AI-Powered-Food-Waste
\`\`\`

### 2. Backend Setup (Spring Boot)
1. Navigate to the backend directory:
   \`\`\`bash
   cd backend
   \`\`\`
2. Configure your database in `src/main/resources/application.yml`:
   \`\`\`yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/foodlink
       username: root
       password: yourpassword
   jwt:
     secret: your_super_secret_base64_key_here
   \`\`\`
3. Run the backend server:
   \`\`\`bash
   mvn spring-boot:run
   \`\`\`
   *(The backend will start on `http://localhost:8080`)*

### 3. Frontend Setup (React/Vite)
1. Open a new terminal and navigate to the frontend directory:
   \`\`\`bash
   cd frontend
   \`\`\`
2. Install dependencies:
   \`\`\`bash
   npm install
   \`\`\`
3. Create a `.env` file in the frontend root and add your local API URL:
   \`\`\`env
   VITE_API_URL=http://localhost:8080/api
   \`\`\`
4. Start the development server:
   \`\`\`bash
   npm run dev
   \`\`\`
   *(The frontend will start on `http://localhost:5173`)*

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/KrishnenduChakraborty49/AI-Powered-Food-Waste/issues).

## 📝 License
This project is [MIT](https://choosealicense.com/licenses/mit/) licensed.
