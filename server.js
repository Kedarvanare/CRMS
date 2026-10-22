// ============================================
// CRMS Backend - Express + MySQL
// ============================================
const express = require('express');
const cors = require('cors');
const mysql = require('mysql2/promise');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// ============================================
// MySQL Connection Pool
// ============================================
let pool;

async function initDatabase() {
    // First connect without a database to create it if needed
    const tempConn = await mysql.createConnection({
        host: 'localhost',
        port: 3306,
        user: 'root',
        password: ''
    });

    await tempConn.execute('CREATE DATABASE IF NOT EXISTS crms');
    await tempConn.end();

    // Now create the pool with the database selected
    pool = mysql.createPool({
        host: 'localhost',
        port: 3306,
        user: 'root',
        password: '',
        database: 'crms',
        waitForConnections: true,
        connectionLimit: 10
    });

    // Create tables
    await pool.execute(`
        CREATE TABLE IF NOT EXISTS admins (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            email VARCHAR(100) UNIQUE NOT NULL,
            username VARCHAR(50),
            password VARCHAR(255) NOT NULL,
            role VARCHAR(20) DEFAULT 'Admin',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `);

    await pool.execute(`
        CREATE TABLE IF NOT EXISTS officers (
            id VARCHAR(20) PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            email VARCHAR(100) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            department VARCHAR(255) DEFAULT '',
            status VARCHAR(20) DEFAULT 'Active',
            role VARCHAR(20) DEFAULT 'Officer',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `);

    await pool.execute(`
        CREATE TABLE IF NOT EXISTS cases_tbl (
            id VARCHAR(20) PRIMARY KEY,
            type VARCHAR(100) NOT NULL,
            complainer VARCHAR(100) NOT NULL,
            criminal VARCHAR(100) DEFAULT 'Unknown',
            date_reported DATE,
            status VARCHAR(20) DEFAULT 'Pending',
            location VARCHAR(255),
            complainer_aadhar VARCHAR(20),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `);

    await pool.execute(`
        CREATE TABLE IF NOT EXISTS criminals (
            id VARCHAR(20) PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            crime VARCHAR(100),
            status VARCHAR(20) DEFAULT 'Wanted',
            sentenceEnd DATE NULL,
            age INT NULL,
            gender VARCHAR(10) NULL,
            occupation VARCHAR(100) NULL,
            address VARCHAR(255) NULL,
            officer VARCHAR(100) DEFAULT 'Unknown',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `);

    await pool.execute(`
        CREATE TABLE IF NOT EXISTS login_history (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user VARCHAR(100),
            type VARCHAR(20),
            login VARCHAR(100),
            logout VARCHAR(100) DEFAULT 'N/A',
            ip VARCHAR(50),
            status VARCHAR(20),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `);

    // Seed default admin if none exists
    const [adminRows] = await pool.execute('SELECT COUNT(*) as count FROM admins');
    if (adminRows[0].count === 0) {
        await pool.execute(
            'INSERT INTO admins (name, email, username, password) VALUES (?, ?, ?, ?)',
            ['Admin User', 'admin@crms.gov', 'admin', 'admin123']
        );
        console.log('✅ Default admin created: admin@crms.gov / admin123');
    }

    // Seed sample officers if none exist
    const [officerRows] = await pool.execute('SELECT COUNT(*) as count FROM officers');
    if (officerRows[0].count === 0) {
        const sampleOfficers = [
            ['OFC-001', 'Inspector Raj Sharma', 'raj.sharma@crms.gov', 'officer123', 'Homicide Division', 'Active'],
            ['OFC-002', 'SI Priya Patel', 'priya.patel@crms.gov', 'officer123', 'Cyber Crime Cell', 'Active'],
            ['OFC-003', 'ASI Vikram Singh', 'vikram.singh@crms.gov', 'officer123', 'Anti-Narcotics', 'Inactive'],
            ['OFC-004', 'Inspector Neha Kulkarni', 'neha.kulkarni@crms.gov', 'officer123', 'Forensic Department', 'Active']
        ];
        for (const o of sampleOfficers) {
            await pool.execute(
                'INSERT INTO officers (id, name, email, password, department, status) VALUES (?, ?, ?, ?, ?, ?)',
                o
            );
        }
        console.log('✅ Sample officers seeded');
    }

    // Seed sample cases if none exist
    const [caseRows] = await pool.execute('SELECT COUNT(*) as count FROM cases_tbl');
    if (caseRows[0].count === 0) {
        const sampleCases = [
            ['CR-2024-001', 'Robbery', 'Amit Deshmukh', 'Suresh Kale', '2024-03-15', 'Pending'],
            ['CR-2024-002', 'Assault', 'Rekha Jadhav', 'Ramesh Patil', '2024-04-20', 'Solved'],
            ['CR-2024-003', 'Theft', 'Vikas Mane', 'Unknown', '2024-05-10', 'Pending'],
            ['CR-2024-004', 'Hacking', 'Sneha Rao', 'Ajay Verma', '2024-06-01', 'Solved'],
            ['CR-2024-005', 'Burglary', 'Kiran Mehta', 'Unknown', '2024-07-22', 'Pending'],
            ['CR-2024-006', 'Murder', 'Dept. of Police', 'Manoj Tiwari', '2024-08-05', 'Pending'],
            ['CR-2024-007', 'Online Fraud', 'Pradeep Joshi', 'Unknown', '2024-09-12', 'Pending']
        ];
        for (const c of sampleCases) {
            await pool.execute(
                'INSERT INTO cases_tbl (id, type, complainer, criminal, date_reported, status) VALUES (?, ?, ?, ?, ?, ?)',
                c
            );
        }
        console.log('✅ Sample cases seeded');
    }

    // Seed sample criminals if none exist
    const [crimRows] = await pool.execute('SELECT COUNT(*) as count FROM criminals');
    if (crimRows[0].count === 0) {
        const sampleCriminals = [
            ['CRIM-2024-001', 'Suresh Kale', 'Robbery', 'Wanted', null, 32, 'Male', 'Unemployed', 'Pune, Maharashtra', 'Inspector Raj Sharma'],
            ['CRIM-2024-002', 'Ramesh Patil', 'Assault', 'Active', null, 28, 'Male', 'Daily Laborer', 'Nashik, Maharashtra', 'SI Priya Patel'],
            ['CRIM-2024-003', 'Ajay Verma', 'Hacking', 'Released', '2025-01-15', 24, 'Male', 'IT Professional', 'Mumbai, Maharashtra', 'SI Priya Patel'],
            ['CRIM-2024-004', 'Manoj Tiwari', 'Murder', 'Wanted', null, 45, 'Male', 'Business', 'Thane, Maharashtra', 'Inspector Raj Sharma']
        ];
        for (const c of sampleCriminals) {
            await pool.execute(
                'INSERT INTO criminals (id, name, crime, status, sentenceEnd, age, gender, occupation, address, officer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                c
            );
        }
        console.log('✅ Sample criminals seeded');
    }

    console.log('✅ Database initialized successfully');
}

// ============================================
// API ROUTES
// ============================================

// --- LOGIN ---
app.post('/api/login', async (req, res) => {
    const { email, password, type } = req.body;

    try {
        if (type === 'admin') {
            const [rows] = await pool.execute(
                'SELECT * FROM admins WHERE email = ? AND password = ?',
                [email, password]
            );
            if (rows.length > 0) {
                res.json({ success: true, user: { name: rows[0].name, role: 'Admin', email: rows[0].email } });
            } else {
                res.json({ success: false, message: 'Invalid admin credentials' });
            }
        } else {
            const [rows] = await pool.execute(
                'SELECT * FROM officers WHERE email = ? AND password = ?',
                [email, password]
            );
            if (rows.length > 0) {
                const officer = rows[0];
                res.json({
                    success: true,
                    user: {
                        id: officer.id,
                        name: officer.name,
                        email: officer.email,
                        department: officer.department,
                        status: officer.status,
                        role: 'Officer'
                    }
                });
            } else {
                res.json({ success: false, message: 'Invalid officer credentials' });
            }
        }
    } catch (err) {
        console.error('Login error:', err);
        res.status(500).json({ success: false, message: 'Server error' });
    }
});

// --- CASES ---
app.get('/api/cases', async (req, res) => {
    try {
        const [rows] = await pool.execute('SELECT * FROM cases_tbl ORDER BY created_at DESC');
        res.json(rows);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to fetch cases' });
    }
});

app.post('/api/cases', async (req, res) => {
    const { id, type, complainer, criminal, date_reported, status } = req.body;
    try {
        await pool.execute(
            'INSERT INTO cases_tbl (id, type, complainer, criminal, date_reported, status) VALUES (?, ?, ?, ?, ?, ?)',
            [id, type, complainer, criminal || 'Unknown', date_reported, status || 'Pending']
        );
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to create case' });
    }
});

app.put('/api/cases/:id/status', async (req, res) => {
    const { status } = req.body;
    try {
        await pool.execute('UPDATE cases_tbl SET status = ? WHERE id = ?', [status, req.params.id]);
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to update case' });
    }
});

app.delete('/api/cases/:id', async (req, res) => {
    try {
        await pool.execute('DELETE FROM cases_tbl WHERE id = ?', [req.params.id]);
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to delete case' });
    }
});

// --- CRIMINALS ---
app.get('/api/criminals', async (req, res) => {
    try {
        const [rows] = await pool.execute('SELECT * FROM criminals ORDER BY created_at DESC');
        res.json(rows);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to fetch criminals' });
    }
});

app.post('/api/criminals', async (req, res) => {
    const { id, name, crime, status, sentenceEnd, age, gender, occupation, address, officer } = req.body;
    try {
        await pool.execute(
            'INSERT INTO criminals (id, name, crime, status, sentenceEnd, age, gender, occupation, address, officer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
            [id, name, crime, status || 'Wanted', sentenceEnd || null, age || null, gender || null, occupation || null, address || null, officer || 'Unknown']
        );
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to create criminal record' });
    }
});

app.delete('/api/criminals/:id', async (req, res) => {
    try {
        await pool.execute('DELETE FROM criminals WHERE id = ?', [req.params.id]);
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to delete criminal record' });
    }
});

// --- OFFICERS ---
app.get('/api/officers', async (req, res) => {
    try {
        const [rows] = await pool.execute('SELECT id, name, email, department, status, role FROM officers ORDER BY created_at DESC');
        res.json(rows);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to fetch officers' });
    }
});

app.post('/api/officers', async (req, res) => {
    const { id, name, email, password, department, status } = req.body;
    try {
        await pool.execute(
            'INSERT INTO officers (id, name, email, password, department, status) VALUES (?, ?, ?, ?, ?, ?)',
            [id, name, email, password, department || '', status || 'Active']
        );
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to create officer' });
    }
});

app.put('/api/officers/:id/status', async (req, res) => {
    const { status } = req.body;
    try {
        await pool.execute('UPDATE officers SET status = ? WHERE id = ?', [status, req.params.id]);
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to update officer status' });
    }
});

// --- LOGIN HISTORY ---
app.get('/api/login-history', async (req, res) => {
    try {
        const [rows] = await pool.execute('SELECT * FROM login_history ORDER BY created_at DESC LIMIT 50');
        res.json(rows);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to fetch login history' });
    }
});

app.post('/api/login-history', async (req, res) => {
    const { user, type, login, logout, ip, status } = req.body;
    try {
        await pool.execute(
            'INSERT INTO login_history (user, type, login, logout, ip, status) VALUES (?, ?, ?, ?, ?, ?)',
            [user, type, login, logout || 'N/A', ip || '127.0.0.1', status]
        );
        res.json({ success: true });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to log entry' });
    }
});

// --- Catch-all: serve index.html for SPA ---
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// ============================================
// START SERVER
// ============================================
async function startServer() {
    try {
        await initDatabase();
        app.listen(PORT, () => {
            console.log(`\n🚀 CRMS Server running at http://localhost:${PORT}`);
            console.log(`📂 Database: crms (MySQL localhost:3306)`);
            console.log(`\n🔑 Default Admin Credentials:`);
            console.log(`   Email:    admin@crms.gov`);
            console.log(`   Password: admin123\n`);
            console.log(`👮 Sample Officer Credentials:`);
            console.log(`   Email:    raj.sharma@crms.gov`);
            console.log(`   Password: officer123\n`);
        });
    } catch (err) {
        console.error('❌ Failed to start server:', err.message);
        console.error('\nMake sure MySQL is running on localhost:3306');
        process.exit(1);
    }
}

startServer();
