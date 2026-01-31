# 🚨 UniSafe – Campus Emergency Alert System

UniSafe is an **Android-based campus safety application** designed to help students and campus authorities **report, track, and respond to emergencies quickly and effectively**. The system focuses on **speed, simplicity, and real-time communication**, making it ideal for university and college environments.

---

## 📱 Project Overview

UniSafe enables:

* 👨‍🎓 **Students** to report emergencies *with or without login*
* 🛂 **Authorities (Admins)** to receive instant alerts and manage emergencies via a live dashboard
* 🚨 **Extreme SOS alerts** using a simple **double-tap** gesture

The application ensures **fast response**, **accurate location sharing**, and **centralized monitoring** during critical situations.

---

## ✨ Key Features

### 🔓 Anonymous Emergency Reporting (Without Login)

* Students can report emergencies without logging in
* User is marked as **"Unknown"**
* Helpful during panic or urgent situations when login is not possible

---

### 🔐 Student Login

Students log in using:

* Name
* Enrollment Number
* Department
* Phone Number

After successful login, the student is redirected to the **Emergency Panel**.

---

### 🚨 Emergency Panel (Student Side)

After login, students can choose from **4 emergency options**:

* 🔥 Fire Emergency
* 🏥 Medical Emergency
* 🛡️ Safety Emergency
* 🚨 SOS (Extreme Emergency)

---

### 📝 Emergency Details Flow (Fire / Medical / Safety)

After selecting an emergency type:

1. Select **Building** (e.g., EC Building, IT Building)
2. Enter **Room Number**
3. Confirm the emergency

📤 **Details sent to Authority/Admin:**

* Student profile (or **Unknown**)
* Emergency type
* Building & room number
* Date & time
* Live location

---

### 🚨 SOS Feature (Double Tap)

* Activated using a **double tap**
* No form or confirmation required
* Designed for **life-threatening or extreme emergencies**

📡 Instantly shares:

* Live location
* Date & time
* User info (or Unknown)

---

## 🛂 Authority (Admin) Side

Authorities have access to:

* Emergency sending options (same as students)
* A **Live Dashboard** to monitor and manage all emergencies

---

## 📊 Live Dashboard (Authority)

### 👨‍🎓 Student Records

* List of all logged-in students
* Displays student profile and login details

### 🚨 SOS & Emergency Records

Shows all reported emergencies:

* Fire
* Medical
* Safety
* SOS

Each record includes:

* Emergency type
* User name (or Unknown)
* Location
* Date & time
* Status:

  * 🟡 Active
  * 🔵 In Progress
  * 🟢 Resolved

Admins can **update emergency status in real time**.

---

## 🔔 Notifications

* Every emergency triggers an **instant notification** on the admin’s phone

📩 Notification details include:

* Emergency type
* Location
* User details
* Date & time

---

## 🛠️ Technology Stack

### 📱 Frontend

* Android Studio
* Java / Kotlin
* XML (Material Design)

### 📍 Services & Tools

* GPS / Location Services
* Phone Notification System

> Backend and cloud services can be integrated in future versions.

---

## 🎯 Objective

The main goal of **UniSafe** is to:

* ⏱️ Reduce emergency response time
* 🏫 Improve overall campus safety
* ⚡ Provide a fast, simple, and reliable emergency reporting system

---

## 📌 Future Enhancements

* 🗺️ Live map tracking
* 📊 Emergency analytics
* 👥 Multi-authority roles
* ☁️ Cloud database integration
* 🔔 Push notifications

---

## 🏆 Hackathon Note

UniSafe is designed as a **hackathon-ready solution** focusing on real-world campus safety problems with a clean UI, clear workflow, and scalable architecture.

> *"Because safety should be one tap away."* ❤️
