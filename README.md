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
     mvn spring-boot: run
     ```


    --- Front-End ---
  1. Open the "/front-end" directory in a new terminal.
  2. Install the dependencies and start the development server:
  ```
  npm install
  npm start
  ```


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
