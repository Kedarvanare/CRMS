const express = require('express');
const sqlite3 = require('sqlite3').verbose();
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// Database initialization
const db = new sqlite3.Database('./database.sqlite', (err) => {
    if (err) {
        console.error('Error opening database', err.message);
    } else {
        console.log('Connected to the SQLite database.');
        createTables();
    }
});

function createTables() {
    db.serialize(() => {
        // Cases
        db.run(`CREATE TABLE IF NOT EXISTS cases (
            id TEXT PRIMARY KEY,
            type TEXT,
            complainer TEXT,
            criminal TEXT,
            date_reported TEXT,
            status TEXT
        )`);

        // Criminals
        db.run(`CREATE TABLE IF NOT EXISTS criminals (
            id TEXT PRIMARY KEY,
            name TEXT,
            crime TEXT,
            status TEXT,
            sentenceEnd TEXT,
            age INTEGER,
            gender TEXT,
            occupation TEXT,
            address TEXT,
            officer TEXT
        )`);

        // Officers
        db.run(`CREATE TABLE IF NOT EXISTS officers (
            id TEXT PRIMARY KEY,
            name TEXT,
            email TEXT,
            password TEXT,
            department TEXT,
            status TEXT,
            role TEXT
        )`);

        // Login History
        db.run(`CREATE TABLE IF NOT EXISTS login_history (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user TEXT,
            type TEXT,
            login TEXT,
            logout TEXT,
            ip TEXT,
            status TEXT
        )`);

        // Seed initial data if empty (Cases)
        db.get("SELECT COUNT(*) as count FROM cases", (err, row) => {
            if (row && row.count === 0) {
                const stmt = db.prepare("INSERT INTO cases VALUES (?, ?, ?, ?, ?, ?)");
                stmt.run("CR-2023-001", "Robbery", "Rahul Sharma", "Unknown", "2023-10-15", "Solved");
                stmt.run("CR-2023-002", "Theft", "Priya Patel", "Rohan Verma", "2023-10-18", "Pending");
                stmt.finalize();
            }
        });

        // Seed default Admin
        db.get("SELECT COUNT(*) as count FROM officers WHERE role='Admin'", (err, row) => {
            if (row && row.count === 0) {
                // simple plaintext password for now, as requested. (In real-world use bcrypt)
                db.run("INSERT INTO officers (id, name, email, password, department, status, role) VALUES ('ADM-001', 'Admin User', 'admin@crms.gov', 'admin123', 'HQ', 'Active', 'Admin')");
                db.run("INSERT INTO officers (id, name, email, password, department, status, role) VALUES ('OFC-001', 'Officer John', 'officer@crms.gov', 'officer123', 'Patrol', 'Active', 'Officer')");
            }
        });
    });
}

// API Routes

// ---- Cases ----
app.get('/api/cases', (req, res) => {
    db.all("SELECT * FROM cases ORDER BY id DESC", [], (err, rows) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(rows);
    });
});

app.post('/api/cases', (req, res) => {
    const { id, type, complainer, criminal, date_reported, status } = req.body;
    db.run("INSERT INTO cases (id, type, complainer, criminal, date_reported, status) VALUES (?, ?, ?, ?, ?, ?)",
        [id, type, complainer, criminal, date_reported, status],
        function (err) {
            if (err) return res.status(500).json({ error: err.message });
            res.json({ success: true, message: "Case added" });
        }
    );
});

app.put('/api/cases/:id/status', (req, res) => {
    const { status } = req.body;
    db.run("UPDATE cases SET status = ? WHERE id = ?", [status, req.params.id], function (err) {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ success: true, message: "Case updated" });
    });
});

app.delete('/api/cases/:id', (req, res) => {
    db.run("DELETE FROM cases WHERE id = ?", [req.params.id], function (err) {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ success: true, message: "Case deleted" });
    });
});

// ---- Criminals ----
app.get('/api/criminals', (req, res) => {
    db.all("SELECT * FROM criminals ORDER BY id DESC", [], (err, rows) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(rows);
    });
});

app.post('/api/criminals', (req, res) => {
    const { id, name, crime, status, sentenceEnd, age, gender, occupation, address, officer } = req.body;
    db.run(`INSERT INTO criminals (id, name, crime, status, sentenceEnd, age, gender, occupation, address, officer)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [id, name, crime, status, sentenceEnd, age, gender, occupation, address, officer],
        function (err) {
            if (err) return res.status(500).json({ error: err.message });
            res.json({ success: true, message: "Criminal added" });
        }
    );
});

app.delete('/api/criminals/:id', (req, res) => {
    db.run("DELETE FROM criminals WHERE id = ?", [req.params.id], function (err) {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ success: true, message: "Criminal deleted" });
    });
});

// ---- Officers ----
app.get('/api/officers', (req, res) => {
    db.all("SELECT id, name, email, department, status, role FROM officers ORDER BY id DESC", [], (err, rows) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(rows);
    });
});

app.post('/api/officers', (req, res) => {
    const { id, name, email, password, department, status, role } = req.body;
    db.run(`INSERT INTO officers (id, name, email, password, department, status, role)
            VALUES (?, ?, ?, ?, ?, ?, ?)`,
        [id, name, email, password, department, status, role],
        function (err) {
            if (err) return res.status(500).json({ error: err.message });
            res.json({ success: true, message: "Officer added" });
        }
    );
});

app.put('/api/officers/:id/status', (req, res) => {
    const { status } = req.body;
    db.run("UPDATE officers SET status = ? WHERE id = ?", [status, req.params.id], function (err) {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ success: true, message: "Officer updated" });
    });
});

// ---- Login History ----
app.get('/api/login-history', (req, res) => {
    db.all("SELECT * FROM login_history ORDER BY id DESC", [], (err, rows) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(rows);
    });
});

app.post('/api/login-history', (req, res) => {
    const { user, type, login, logout, ip, status } = req.body;
    db.run(`INSERT INTO login_history (user, type, login, logout, ip, status)
            VALUES (?, ?, ?, ?, ?, ?)`,
        [user, type, login, logout, ip, status],
        function (err) {
            if (err) return res.status(500).json({ error: err.message });
            res.json({ success: true, message: "Login history added" });
        }
    );
});

// ---- Auth ----
app.post('/api/login', (req, res) => {
    const { email, password, type } = req.body;
    let query = "SELECT * FROM officers WHERE email = ? AND password = ?";
    let params = [email, password];

    // If it's an admin login, let's just make sure they have the Admin role
    if (type === 'admin') {
        query += " AND role = 'Admin'";
    } else if (type === 'officer') {
        query += " AND role = 'Officer'";
    }

    db.get(query, params, (err, row) => {
        if (err) return res.status(500).json({ error: err.message });
        if (!row) return res.status(401).json({ success: false, message: "Invalid credentials" });

        // Remove password before sending user data
        delete row.password;
        res.json({ success: true, user: row });
    });
});

// Default route for SPA
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
    console.log("Server is running on http://localhost:" + PORT);
});
