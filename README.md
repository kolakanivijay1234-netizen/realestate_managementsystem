# Real Estate Agent Mandate Management System

A web and cloud-deployable management system for real estate agencies, agents, geographical coverage areas, and mandates with full MySQL database schema parity.

---

## 🚀 Live Cloud Deployment Options

### 1. Deploying to **Vercel** (One-Click)

1. Push this project to your GitHub repository:
   ```bash
   git add .
   git commit -m "Deploy to Vercel and GitHub Pages"
   git push origin main
   ```
2. Go to [Vercel](https://vercel.com/) and click **Add New Project**.
3. Import your GitHub repository.
4. **Vercel Settings**:
   - **Framework Preset**: `Other`
   - **Root Directory**: `./` (Default)
   - *Everything is pre-configured via `vercel.json` and `package.json`.*
5. Click **Deploy**. Your application will be live immediately!
6. *(Optional)* If you connect an online MySQL database (e.g., Aiven, PlanetScale, Supabase, TiDB, or Railway), configure these Environment Variables in your Vercel Dashboard:
   - `MYSQL_HOST`
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
   - `MYSQL_DATABASE`
   - `MYSQL_PORT` (default 3306)

---

### 2. Deploying to **GitHub Pages**

1. Go to your GitHub repository -> **Settings** -> **Pages**.
2. Under **Build and deployment**:
   - **Source**: `Deploy from a branch`
   - **Branch**: `main`, Folder: `/public` (or copy contents of `public/` to root or use GitHub Actions).
3. Click **Save**.
4. Your application will be live at `https://<your-username>.github.io/<repo-name>/`.

---

## 💻 Running Locally on Your Computer

### Option A: Using Node.js (Already Installed)
From this directory, run:
```bash
node server.js
```
Then open your browser and go to:
👉 **`http://localhost:3000`**

### Option B: Running with VS Code "Live Server"
1. Right-click [`public/index.html`](public/index.html).
2. Click **Open with Live Server**.

### Option C: The Java Swing Desktop Version
The Java source code is preserved in `src/`. To run the Swing application:
- Open the project in VS Code with the Java Extension Pack and press **F5**, or run `mvn exec:java`.

---

## 🏛️ Architecture & Features

- **OOP Hierarchy**: Implements `Agency` base class with polymorphism in `IndependentAgency` (overrides `getAgencyDescription()` with owner info) and `FranchiseAgency` (overrides with brand info).
- **Date Validation**: Strict validation ensures `End Date > Start Date`.
- **Database Schema**: Full compatibility with MySQL schema defined in [`database_schema.sql`](database_schema.sql).
- **15 Analytical Reports**:
  1. Display all agents
  2. Display all agencies with polymorphic description
  3. Display all geographical areas
  4. Display active mandates on a given date (with live date picker)
  5. Total number of agents count
  6. Total number of agencies count
  7. Total number of mandates count
  8. Mandates handled by each agent
  9. Agents with at least one mandate
  10. Agencies that have issued mandates
  11. Agent handling maximum mandates
  12. Active and expired mandates breakdown
  13. Mandates grouped by mandate type (SALE, RENT, PURCHASE, LEASE)
  14. Mandates grouped by agency
  15. Mandates grouped by geographical area
- **Exporting**: Supports one-click CSV export of reports and JSON backup of the entire database.
