# 1. Purpose and Scope

This document defines StudyMatch's authentication and authorization decisions before security code is implemented. It specifies user roles, login and logout, password storage, JWT rules, the association between user accounts and student records, and access permissions for existing and planned API endpoints.

Permissions must be enforced by the backend. Hiding a button or page in the frontend is not a substitute for server-side authorization.

## 1.1 Sources of the Decisions

- **D3 (#45) requirements:** `ADMIN`, `TEACHER`, and `STUDENT` roles; email/password login; BCrypt password hashing; JWT expiration after eight hours; a `studentId` field linking student accounts to student records; initial administrator credentials provided by `ADMIN_EMAIL` and `ADMIN_PASSWORD`; JWT storage in `localStorage` as a known limitation; and a permission matrix covering all endpoints.
- **Agreed proposals:** only administrators create accounts; students edit non-academic personal information, goals, and preferences; group members may see shared competencies but not grades; goals are private by default and shared by student choice; access to a former group member ends when there is no longer a shared active group; teachers may read necessary academic data but not change grades; logout removes the local token without immediate server-side revocation; and the additional security rules described below.
- **Subject to team approval:** restricting student-to-student profile access to members of the same active group, configurable goal visibility, and the exact paths of new endpoints. These decisions extend or clarify the original issue.

# 2. User Roles and Responsibilities

## 2.1 ADMIN

An administrator manages user accounts, student records, courses, course units, associations between course units and skills, and academic records. Administrators may access information necessary for platform administration and perform all operations available to teachers, including group formation management and dashboard access.

Only `ADMIN` may create user accounts, assign roles, and link a `STUDENT` account to a student record. A student must not be able to change their role or `studentId` through a personal-profile update.

## 2.2 TEACHER

A teacher may read student records, profiles, and academic information necessary for teaching and group formation, but may not change grades. Teachers may create, view, and adjust group formations and access the dashboard.

Teachers may not create accounts, assign roles, administer courses, or modify academic records unless a later team decision explicitly grants these permissions.

## 2.3 STUDENT

A student may view their own profile, academic history, competencies, and portfolio; submit their own onboarding questionnaire; and manage their own goals, preferences, and non-academic personal information.

Students may view another student's shared profile and explicitly shared goals **only when both students belong to at least one active group in common**. They may not access another student's detailed grades, private academic history, or individual onboarding responses. They may not modify another student's information, grades, or system-calculated competencies.

A student without an active group can still access and manage their own information, but cannot access other students' shared profiles through this feature.

# 3. User Accounts and Student Records

Each user account contains at least an internal identifier, a unique email address, a password hash, and a role (`ADMIN`, `TEACHER`, or `STUDENT`).

A `STUDENT` account has a `studentId` field referencing an existing student record. The backend establishes and validates this association when an administrator creates or manages the account. The `studentId` is required for `STUDENT` accounts and, under this proposal, is empty for `ADMIN` and `TEACHER` accounts.

The authenticated student's identity is determined from the account and its associated `studentId`, never solely from an identifier supplied by the frontend. When a route accepts `{studentId}` or `{id}`, the backend checks resource ownership or the applicable permission before responding.

The account-to-student association should be unique: two active `STUDENT` accounts must not reference the same student record unless a future decision explicitly permits it.

# 4. Email and Password Authentication

1. The user submits an email address and password to the login endpoint.
2. The backend finds the account by email and verifies the password using BCrypt.
3. If authentication fails, the backend returns a generic error without revealing whether the email address exists.
4. If authentication succeeds, the backend issues a signed JWT valid for eight hours.
5. The frontend stores the token in `localStorage` and sends it with protected requests using the `Authorization: Bearer <token>` header.
6. The backend validates the token's signature, expiration, user identity, and permissions before performing protected operations.

Public account registration is not supported at this stage. Administrators create user accounts.

# 5. Password Storage

Passwords must never be stored in plaintext. The backend uses BCrypt with a salt and appropriate work-factor settings through the security library selected for the project.

Passwords must not appear in API responses, application logs, error messages, version-controlled configuration files, or examples containing real credentials. During login, the submitted password is checked against the stored hash; the hash is not decrypted.

# 6. JWT and Session Management

- **Lifetime:** eight hours from issuance.
- **Minimum claims:** user account identifier, role, issuance time, and expiration time. The backend may retrieve `studentId` from the current account record rather than relying on potentially outdated token claims.
- **Signature:** created using a strong secret provided through environment configuration, never committed to the repository.
- **Validation:** every protected request requires a valid token. The backend must also check that the account remains active and has the necessary permissions.
- **Expiration:** after eight hours, the user must log in again. No refresh-token mechanism is included in this decision.
- **Logout (Option A):** the frontend removes the JWT from `localStorage` and clears local authentication state. The backend does not immediately revoke the token. A previously copied token may remain usable until expiration. Immediate revocation may be considered in a future iteration.

## 6.1 Frontend Token Storage — Known Limitation

The JWT is stored in `localStorage`, as specified by D3. This supports persistence across page reloads, but **exposes the token to malicious JavaScript if an XSS vulnerability exists**. The frontend must avoid injecting untrusted content, placing tokens in URLs, or logging tokens. HTTPS must be used in production. `HttpOnly` cookies may be considered in the future if the team revises this decision.

# 7. Initial Administrator Creation

The first `ADMIN` account is created using the `ADMIN_EMAIL` and `ADMIN_PASSWORD` environment variables during controlled application startup.

- Initial credentials must not be hardcoded or committed to version control.
- The backend validates the required environment variables and stores **only the BCrypt hash** of the password.
- Creation must be idempotent: restarting the application must not duplicate the initial account or automatically overwrite an existing account's password.
- The process must not print the password or JWT in logs.
- The team must define how an environment without these variables behaves, without creating a default-credential account.

After initial setup, authenticated administrators manage all other accounts.

# 8. Profiles, Competencies, Goals, and Groups

## 8.1 Own Profile and Editing

Students may read their own records and update only non-academic personal fields, such as preferences and goals. They may not change their role, `studentId`, grades, academic records, or competencies calculated from grades.

Because the existing `PUT /api/students/{id}` endpoint accepts a student update request, authorization must prevent `STUDENT` users from modifying academic fields, preferably through dedicated request DTOs or a separate personal-profile endpoint. **Allowing the entire `PUT` request merely because the student owns the ID is not sufficient.**

## 8.2 Shared Profiles

Students may view a peer's shared profile only if they belong to at least one active group in common. The backend checks this relationship for every request. Access ends when they no longer share an active group, unless they still share another active group.

A shared profile may contain the student's name, competencies approved for sharing, public portfolio, and goals marked as shared by the owner. It must not expose detailed grades, private academic history, or individual onboarding responses.

The shared-profile endpoint must return only permitted fields. The frontend must not receive private information and merely hide it visually.

## 8.3 Goals and Preferences

Goals are **private by default**. Students may create, edit, and delete their own goals and choose individually which goals to share with members of the same group. Other students may read shared goals but may not modify them.

Students may edit their own preferences, but preferences are not automatically visible to peers. Sharing preferences would require a separate decision identifying which fields may be disclosed.

## 8.4 Active Group Definition

For authorization purposes, a shared group must belong to a formation that the application recognizes as active. The data model and formation lifecycle must define this state consistently. The access check must use current group membership rather than a cached or historical association.

# 9. Permission Matrix — Existing Endpoints

The following **17 HTTP endpoints** were identified in the current StudyMatch controllers. The table describes the intended authorization policy, **not security behavior already implemented**.

**Legend:** ✓ = allowed; — = denied; **Own** = only when the requested student ID matches the authenticated account's `studentId`; **Limited** = only approved non-academic fields; **Public** = no login required, provided no sensitive data is exposed.

| Method | Existing endpoint | ADMIN | TEACHER | STUDENT | Additional rule |
|---|---|:---:|:---:|:---:|---|
| POST | `/api/students` | ✓ | — | — | Creates a student record, not a login account |
| GET | `/api/students` | ✓ | ✓ | — | Administrative/educational listing; no private bulk listing for students |
| GET | `/api/students/{id}` | ✓ | ✓ | Own | Student access only when `id == studentId` |
| PUT | `/api/students/{id}` | ✓ | — | Limited | Students may update only their own non-academic personal fields; validate fields server-side |
| POST | `/api/students/{studentId}/academic-records` | ✓ | — | — | Academic-record changes are reserved for administrators |
| GET | `/api/students/{studentId}/academic-records` | ✓ | ✓ | Own | Teacher access is limited to authorized educational purposes |
| GET | `/api/students/{studentId}/competencies` | ✓ | ✓ | Own | Peers' shared competencies must be served through a restricted shared-profile response |
| POST | `/api/students/{studentId}/onboarding` | — | — | Own | A student may submit only their own onboarding questionnaire |
| POST | `/api/courses` | ✓ | — | — | Course administration |
| GET | `/api/courses` | ✓ | ✓ | — | Educational reading; student access requires a separate decision |
| POST | `/api/course-units` | ✓ | — | — | Course-unit administration |
| GET | `/api/course-units` | ✓ | ✓ | — | Educational reading |
| POST | `/api/course-units/{courseUnitId}/skills` | ✓ | — | — | Manage course-unit-to-skill associations |
| GET | `/api/course-units/{courseUnitId}/skills` | ✓ | ✓ | — | Educational reading |
| GET | `/api/skills` | ✓ | ✓ | ✓ | Skill catalog without private student information |
| GET | `/api/health` | Public | Public | Public | Technical health information only; no secrets |
| GET | `/api/status` | Public | Public | Public | Non-sensitive status information only |

**Implementation note:** existing `GET /api/students/{id}` and `PUT /api/students/{id}` may require distinct response/request DTOs, field-level validation, or separate routes to enforce this matrix. The matrix does not imply that the current controllers already support these restrictions.

# 10. Permission Matrix — Planned or Proposed Endpoints

The paths below are **proposed API contracts for planning**. Planned features include authentication, account management, goals, statistics, group formations, groups, and portfolios, but the exact HTTP paths are not necessarily fixed by the backlog. Final paths must be agreed with the implementation issues and the team.

**Legend:** ✓ = allowed; — = denied; **Group** = allowed only if the authenticated student and the profile owner share at least one active group; **Public** = no authentication required.

| Method | Planned/proposed endpoint | ADMIN | TEACHER | STUDENT | Additional rule |
|---|---|:---:|:---:|:---:|---|
| POST | `/api/auth/login` | Public | Public | Public | Accepts credentials and returns a JWT on success |
| GET | `/api/auth/me` | ✓ | ✓ | ✓ | Returns minimal authenticated account information |
| POST | `/api/users` | ✓ | — | — | Only administrators create accounts |
| GET | `/api/users` | ✓ | — | — | List accounts |
| GET | `/api/users/{id}` | ✓ | — | — | Administrative account lookup |
| PUT | `/api/users/{id}` | ✓ | — | — | Administrative management; never return password hashes |
| DELETE | `/api/users/{id}` | ✓ | — | — | Prefer deactivation when historical records must be retained |
| GET | `/api/students/me` | — | — | ✓ | Own profile identified through the token |
| PATCH | `/api/students/me` | — | — | ✓ | Non-academic personal fields only |
| GET | `/api/students/{id}/public-profile` | ✓ | ✓ | Group | For students, require a shared active group and return only shared fields |
| GET | `/api/students/me/portfolio` | — | — | ✓ | Own portfolio |
| GET | `/api/students/{id}/public-portfolio` | ✓ | ✓ | Group | Shared content only; require a common active group for students |
| GET | `/api/students/me/portfolio/export` | — | — | ✓ | Export only the authenticated student's portfolio |
| GET | `/api/students/me/goals` | — | — | ✓ | All own goals, including private goals |
| POST | `/api/students/me/goals` | — | — | ✓ | Create an own goal, private by default |
| PATCH | `/api/students/me/goals/{goalId}` | — | — | ✓ | Update own goal content or visibility |
| DELETE | `/api/students/me/goals/{goalId}` | — | — | ✓ | Delete an own goal only |
| GET | `/api/students/me/preferences` | — | — | ✓ | Read own preferences |
| PATCH | `/api/students/me/preferences` | — | — | ✓ | Update own preferences |
| GET | `/api/students/{id}/shared-goals` | ✓ | ✓ | Group | Students may read only shared goals of a peer in a common active group |
| POST | `/api/formations` | ✓ | ✓ | — | Create a group formation |
| GET | `/api/formations` | ✓ | ✓ | — | List formations for management |
| GET | `/api/formations/{id}` | ✓ | ✓ | — | Read formation details for management |
| PATCH | `/api/formations/{id}` | ✓ | ✓ | — | Adjust a formation according to business rules |
| POST | `/api/formations/{id}/generate` | ✓ | ✓ | — | Generate groups using the strategy defined in D2 |
| GET | `/api/formations/{id}/groups` | ✓ | ✓ | — | Read groups in a formation for management |
| PATCH | `/api/formations/{id}/groups/{groupId}` | ✓ | ✓ | — | Adjust group membership; validate business rules |
| GET | `/api/students/me/groups` | — | — | ✓ | Read only the authenticated student's groups |
| GET | `/api/dashboard` | ✓ | ✓ | — | Administration and teaching dashboard |
| GET | `/api/stats` | ✓ | ✓ | — | Aggregated statistics without improper disclosure of personal data |

**Meaning of “Group”:** a `STUDENT` may access the shared resource only if the authenticated student and its owner belong to at least one active group in common. Teachers and administrators are not subject to this group-membership condition, but may access only information appropriate to their authorized purpose.

**Planning note:** this table combines endpoints associated with planned Sprint 3 features and additional endpoints proposed to support the agreed decisions. It does not claim that every path is explicitly required by the backlog or must be implemented in this sprint. If the team chooses different paths or combines operations, the matrix must be updated before implementation.

# 11. Authorization Rules and HTTP Responses

1. **Unauthenticated:** a request to a protected endpoint without a valid token returns `401 Unauthorized`.
2. **Forbidden:** an authenticated user lacking the required role or relationship receives `403 Forbidden`. When appropriate to avoid revealing the existence of a private resource, the API may consistently use `404 Not Found` instead.
3. **Ownership:** endpoints for personal data derive `studentId` from the authenticated account. An identifier in the URL is never proof of ownership.
4. **Shared group:** access to shared profiles and goals is checked server-side on every request and requires a common active group.
5. **Allowed fields:** student updates must not permit `STUDENT` users to change grades, roles, `studentId`, or academic fields.
6. **Least privilege:** new endpoints deny access to roles that are not explicitly authorized; the matrix must be updated whenever the API contract changes.
7. **Response data:** endpoints return only fields required for the operation and must not disclose private information by default.

# 12. Planned Acceptance Tests

- Correct credentials produce a JWT valid for eight hours; incorrect credentials are rejected.
- Passwords are stored only as BCrypt hashes and do not appear in API responses or logs.
- Protected requests without a token, with an invalid signature, or with an expired token are rejected.
- `STUDENT` cannot create accounts, edit grades, or view another student's private academic history.
- `STUDENT` can read and update their own permitted personal information, goals, and preferences.
- A new goal is private by default and appears to peers only when shared and when an active group is common to both students.
- A student can view a peer's shared profile within the same active group but not a peer's profile without a shared active group.
- Access to a shared profile is denied when no active group remains in common.
- `TEACHER` can read necessary academic information and manage formations but cannot edit grades or create accounts.
- `ADMIN` can manage accounts, student records, courses, course units, and academic records.
- The initial administrator is created from environment variables without hardcoded credentials and is not duplicated on restart.
- Logout removes the local token; the absence of immediate revocation is documented as a limitation.

# 13. Known Limitations and Items for Validation

1. **`localStorage`:** JWT theft is possible if an XSS vulnerability occurs; this storage choice is retained because D3 requires it.
2. **Logout without revocation:** a copied JWT may remain valid for up to eight hours after issuance.
3. **Active groups:** the data model must clearly identify which formations/groups grant access to shared profiles.
4. **Shared profiles:** dedicated responses are needed to avoid exposing private academic fields.
5. **Personal editing:** the current `PUT /api/students/{id}` must be protected against updates to disallowed fields.
6. **New endpoints:** proposed paths must be aligned with implementation issues A1–A9, S1–S7, and G1–G12, and with navigation decisions.
7. **Approval:** D3 should be considered complete only after review and approval by the other three team members, according to the backlog.

# 14. Decision Status

This document defines the intended security policy for future implementation. **It does not mean that authentication, authorization, route protection, account management, or group-membership checks have already been implemented.**

After team approval, this English document should be saved as `docs/auth.md` on a dedicated branch and submitted through a pull request associated with issue #45.
