# PPE Inventory Management System 🧤📦

*PPE Inventory Management System* is a Java-based desktop application developed to manage the stock, distribution, and tracking of Personal Protective Equipment (PPE) within a warehouse environment. Built using Object-Oriented Programming (OOP) principles, the system supports multiple user roles and ensures the smooth operation of PPE distribution to hospitals, integrating modular components for usability, security, and accountability.

---

## 🎯 Objectives

- Manage and monitor PPE stock levels for hospital distribution.
- Enforce role-based access control (RBAC) for system administrators, warehouse managers, and staff.
- Allow staff to record and track PPE received from suppliers and dispatched to hospitals.
- Provide a secure, traceable, and modular system with logging and inventory reset capabilities.
- Support report generation, real-time alerts, and user permission management.

---

## 🛠 Technologies Used

- *Programming Language:* Java
- *Data Storage:* Plain text files (`ppe.txt`, `users.txt`, `transactions.txt`, etc.)
- *GUI:* Java Swing (built with NetBeans GUI Builder)
- *Security:* Password-protected login with validation and error logging
- *Architecture:* OOP with layered design (controllers, services, models, utils, GUI)

---

## 🧩 Key Features

- 👥 *Role-Based Access Control* – System Admin, Warehouse Manager, and Warehouse Staff have unique permissions.
- 📥 *Inventory Management* – Track incoming PPE from suppliers and outgoing PPE to hospitals.
- ⚠ *Low Stock Alerts* – Notify users when stock drops below 25 boxes.
- 🗂 *Report Generation* – Generate PDF reports for PPE inventory, low stock, received, and dispatched items.
- 📝 *User Management* – Admin can create, modify, or delete users with permission controls.
- 🔐 *System Security* – Logs activities, failed attempts, and enforces strong password requirements.
- 🔄 *System & Inventory Reset* – Admin can reinitialize or fully reset the system with confirmation prompts.

---

## 🧪 Testing & Feedback

Comprehensive testing was performed for login validation, inventory calculations, permission enforcement, and report exports. GUI feedback and confirmation dialogs were integrated to support error prevention and user-friendly interactions. All file operations were validated to ensure accurate data persistence and integrity.

---

## 👨‍💻 Contributors

- Heng Xin Hui (TP077232)  
- Phang Shea Wen (TP075813)

---

## 📚 License

This project is for academic purposes and not licensed for commercial use. For educational reference only.
