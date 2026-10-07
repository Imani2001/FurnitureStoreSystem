=========================================================
FURNITURE STORE SALES SYSTEM
Enterprise Application Development 1 — CW1
=========================================================

1. SYSTEM REQUIREMENTS
----------------------
- Windows 10 or later
- Java JDK 8 or higher
- XAMPP (MySQL running on port 3306) OR standalone MySQL Server

2. INSTALLATION
---------------
1. Extract this folder somewhere (e.g., C:\FurnitureStoreSetup)
2. Open XAMPP Control Panel and make sure MySQL is running
3. Double-click setup.bat
4. The script will:
     - verify Java is installed
     - create the database "furniture_store" with sample data
     - launch the application
5. Login with:
     Username: admin
     Password: admin123

3. FEATURES
-----------
- Secure login (user authentication)
- Dashboard with live statistics
- Customer Management: Add / Edit / Delete / Search
- Product Management: Add / Edit / Delete / Search
- Sales Order Processing:
     * Add products to cart
     * Live subtotal, tax (10%), total calculation
     * Stock validation
     * Auto stock reduction on save
- 5 JasperReports:
     1. Sales Summary
     2. Top Selling Products
     3. Customer Order History
     4. Low Stock Alert
     5. Monthly Revenue

4. DESIGN PATTERNS USED
-----------------------
- Singleton     → DatabaseConnection, SessionManager
- DAO           → CustomerDAO, ProductDAO, SalesOrderDAO, etc.
- MVC           → Separate model, dao, ui packages
- Service layer → SalesOrderDAO.saveOrder() transaction

5. TROUBLESHOOTING
------------------
- "mysql is not recognized"
     → Add C:\xampp\mysql\bin to system PATH

- "Access denied for user 'root'"
     → XAMPP MySQL is not running. Start it in XAMPP Control Panel.

- "Unknown database 'furniture_store'"
     → Re-run setup.bat

- "Java not found"
     → Install Java JDK 8+ and ensure java.exe is on PATH

6. FILES IN THIS PACKAGE
------------------------
FurnituresStoreSystem.jar     Main application
lib\                          All dependency JARs
database_setup.sql            Database schema + sample data
setup.bat                     Automated setup script
README.txt                    This file
docs\ERD.png                  Entity Relationship Diagram
docs\Screenshots\             Screenshots of the application

=========================================================
Author: [Your Name]
Student ID: [Your ID]
Date: [Today's Date]
=========================================================