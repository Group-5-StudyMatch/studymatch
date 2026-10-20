# StudyMatch
StudyMatch is a full-stack software aimed at supporting formation of groups in academic settings and activities, matching students based on academic history, preferences, strengths, and personal goals instead of a randomized approach.


- Technology Stack:
     - Front-end: React(JavaScript)
     - Back-end: Java/SpringBoot
     - Persistence: MySQL
     - Build: Maven
 
- Prerequisites:
     - IDE: Visual Studio Code
     - Java Development Kit 17+
     - Node.js 20.19+ and npm
     - MySQL Server
     - XAMPP (Recommended for local testing/development)
       
 
  ----- INITIAL SETUP -----
  1. Clone the Repository
     ```
     git clone https://github.com/your-org/studymatch.git
     cd studymatch
     ```
  
  --- Back-End ---   
  1. Ensure MySQL Server/XAMPP is running and create a database named studymatch_db.
  2. Update the application properties with your database credentials to allow the connection.
  3. Build and run the project using Maven:
     ```
     mvn clean install
     mvn spring-boot:run
     ```


   --- Front-End ---

   Requirements:
   - Node.js 20.19+ (LTS recommended) and npm
   - The Back-End must be running on http://localhost:8080 for the application to load data. The front-end development server forwards every request starting with `/api` to this address (configured in `frontend/vite.config.js`).

   Steps:
   1. Open a new terminal and go to the front-end directory:
```
      cd frontend
```
   2. Install the dependencies (only needed the first time or when `package.json` changes):
```
      npm install
```
   3. Start the development server (Vite):
```
      npm run dev
```
   4. Open http://localhost:5173 in your browser.

   Other useful commands (run inside `/frontend`):

```
   npm run build     # create the production build in /dist
   npm run preview   # preview the production build locally
   npm run lint      # run ESLint to check code style
```

   Testing:
   - Automated front-end tests are not configured yet. For now, code quality is checked with `npm run lint`. A test framework (e.g. Vitest) can be added in a future sprint.

   Troubleshooting:
   - `npm install` fails: check your Node.js version with `node -v`.
   - Port 5173 is already in use: stop the other process, or Vite will suggest the next free port in the terminal.
   - No data is displayed or requests to `/api` fail: make sure the Back-End is running on port 8080.


  ----- DEVELOPMENT WORKFLOW -----
  All development work must be traced back to a specific issue. Direct commits to the main branch are not allowed.
  A team member cannot approve their own pull request. At least another team member must review the pull request for it to be merged into main. After merge, delete the branch.

  Branch Naming:
      - feature/issue#-short-description
      - bugfix/issue#-short-description


  ----- CODING STANDARDS & PRACTICES -----
  Language: Every code variable, comments, and documentation must be written in English.
  Design: SOLID principles are of crucial implementation as it will allow for a straight forward maintenance and introduction of new features in further development.
  Traceability: Always reference the issue number in your commit messages. Example: Fixes #12 - Added student onboarding form
