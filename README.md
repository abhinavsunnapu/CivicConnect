<<<<<<< HEAD
# CivicConnect

**An AI-powered smart civic issue reporting and resolution platform.**

CivicConnect links citizens and local authorities. Citizens report civic problems such as potholes, garbage dumps, broken streetlights, waterlogging and damaged public property by uploading a photo and pinning the location on a map. An AI model looks at the image, detects and classifies the issue, and sends it to the right department. Authorities can then track, prioritise and resolve reports openly, and citizens get updates at every stage.

> **Status:** 🚧 Early development. The project structure is set up; application code has not been written yet.

---

## Table of Contents

- [Problem Statement](#problem-statement)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Problem Statement

Reporting civic problems is often slow and opaque, and the process is split across offices. Citizens rarely know where to file a complaint or whether anyone acted on it. Authorities get duplicate, vague or wrongly routed reports. CivicConnect addresses this with:

- A single, simple channel for reporting issues with photo and location evidence.
- AI-based detection and classification, which reduces manual sorting.
- Transparent tracking of every issue from submission to resolution.
- Map-based insight into problem hotspots across a city.

## Key Features

| Feature | Description |
|---|---|
| 📸 **Photo-based reporting** | Citizens upload an image of the issue along with a short description. |
| 🤖 **AI issue detection** | A YOLOv8 model detects and classifies the issue type from the image. |
| 🗺️ **Interactive maps** | Issues are pinned and shown on OpenStreetMap using Leaflet. |
| 🔐 **Secure authentication** | Role-based access (Citizen, Authority, Admin) using Spring Security and JWT. |
| 📊 **Status tracking** | Lifecycle tracking: Reported → Verified → In Progress → Resolved. |
| 🏛️ **Authority dashboard** | Departments view, assign, prioritise and resolve issues. |
| 🔁 **Duplicate awareness** | Nearby reports of the same issue type can be grouped. |

## Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React + Vite |
| **Styling** | Tailwind CSS |
| **Backend** | Java 21 + Spring Boot |
| **Database** | PostgreSQL |
| **Authentication** | Spring Security + JWT |
| **AI / ML** | Python + YOLOv8 |
| **AI API** | FastAPI |
| **Maps** | Leaflet + OpenStreetMap |

## Architecture Overview

```
┌──────────────────────┐        REST / JSON        ┌──────────────────────────┐
│   Frontend (React)   │ ───────────────────────▶  │  Backend (Spring Boot)   │
│  Vite + Tailwind CSS │ ◀───────────────────────  │  Spring Security + JWT   │
│  Leaflet + OSM maps  │                           └────────────┬─────────────┘
└──────────────────────┘                                        │
                                                   ┌────────────┴─────────────┐
                                                   │                          │
                                                   ▼                          ▼
                                       ┌──────────────────────┐  ┌──────────────────────┐
                                       │     PostgreSQL       │  │  AI Service (FastAPI)│
                                       │   (persistent data)  │  │   YOLOv8 detection   │
                                       └──────────────────────┘  └──────────────────────┘
```

1. The **frontend** collects reports (image, description, location) and shows issues on a map.
2. The **backend** authenticates users, applies business rules, stores data and coordinates the other services.
3. The **AI service** receives images from the backend and returns the detected issue type and a confidence score.
4. **PostgreSQL** stores users, issues, statuses and related records.

## Project Structure

```
CivicConnect/
├── frontend/      # React + Vite + Tailwind CSS client application
├── backend/       # Java 21 + Spring Boot REST API (security, business logic)
├── ai-service/    # Python FastAPI service hosting the YOLOv8 model
├── database/      # PostgreSQL schema, migrations and seed scripts
├── docs/          # Project documentation, diagrams and design notes
├── .gitignore
└── README.md
```

## Getting Started

> Setup instructions will be added as each module is implemented.

### Prerequisites (planned)

- **Node.js** 20+ and npm (frontend)
- **Java** 21 (JDK) and Maven or Gradle (backend)
- **Python** 3.10+ (AI service)
- **PostgreSQL** 15+ (database)
- **Git**

## Roadmap

- [x] **Step 1:** Initialise project structure, README and `.gitignore`
- [ ] Set up frontend scaffold (React + Vite + Tailwind CSS)
- [ ] Set up backend scaffold (Spring Boot, Java 21)
- [ ] Design the database schema (PostgreSQL)
- [ ] Implement authentication (Spring Security + JWT)
- [ ] Build issue reporting APIs and UI
- [ ] Integrate maps (Leaflet + OpenStreetMap)
- [ ] Build the AI service (FastAPI + YOLOv8)
- [ ] Connect the AI service to the backend
- [ ] Build the authority dashboard and status tracking
- [ ] Testing, documentation and deployment

## Contributing

Contribution guidelines will be added once the core modules are in place.

## License

A license has not been chosen yet.
=======
# CivicConnect
>>>>>>> ee9ba17751b0e6925d20e4eac59de202d653822d
