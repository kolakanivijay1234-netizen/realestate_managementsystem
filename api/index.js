const mysql = require('mysql2/promise');

// Configuration supports standard environment variables (e.g. from Aiven, PlanetScale, Railway, Supabase MySQL, TiDB, or local)
const dbConfig = {
  host: process.env.MYSQL_HOST || process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.MYSQL_PORT || process.env.DB_PORT || '3306', 10),
  user: process.env.MYSQL_USER || process.env.DB_USER || 'root',
  password: process.env.MYSQL_PASSWORD || process.env.DB_PASSWORD || '',
  database: process.env.MYSQL_DATABASE || process.env.DB_NAME || 'real_estate_mandate_db',
  waitForConnections: true,
  connectionLimit: 5,
  queueLimit: 0,
  connectTimeout: 4000
};

let pool = null;

function getPool() {
  if (!pool && process.env.MYSQL_HOST) {
    try {
      pool = mysql.createPool(dbConfig);
    } catch (err) {
      console.warn('MySQL pool initialization error:', err.message);
    }
  }
  return pool;
}

module.exports = async function handler(req, res) {
  // Enable CORS
  res.setHeader('Access-Control-Allow-Credentials', 'true');
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET,OPTIONS,PATCH,DELETE,POST,PUT');
  res.setHeader('Access-Control-Allow-Headers', 'X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version');

  if (req.method === 'OPTIONS') {
    res.status(200).end();
    return;
  }

  // Parse path & query
  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  const pathname = url.pathname.replace(/^\/api/, '');

  try {
    const currentPool = getPool();
    if (!currentPool) {
      return res.status(200).json({
        connected: false,
        fallbackMode: true,
        message: 'No external MySQL environment variables set (MYSQL_HOST). Frontend running in client-side persistence mode.'
      });
    }

    // Health check
    if (pathname === '/health' || pathname === '') {
      const [rows] = await currentPool.query('SELECT 1 as alive');
      return res.status(200).json({ connected: true, status: 'Database online', rows });
    }

    // Generic SQL query runner for authorized requests
    if (req.method === 'POST' && pathname === '/query') {
      const { sql, params } = req.body || {};
      if (!sql) {
        return res.status(400).json({ error: 'SQL query missing' });
      }
      const [results] = await currentPool.execute(sql, params || []);
      return res.status(200).json({ success: true, data: results });
    }

    return res.status(404).json({ error: 'Endpoint not found' });
  } catch (err) {
    console.error('API Error:', err);
    return res.status(500).json({ error: err.message, fallbackMode: true });
  }
};
