<p align="center">
  <img src="res/logo_transparent_bg.png" alt="PocketGuru Logo" width="300"/>
</p>

<h1 align="center">PocketGuru</h1>

<p align="center">
  An interactive Android learning app that teaches primary school students photosynthesis through multimedia, gamification, and real-world exploration.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android"/>
  <img src="https://img.shields.io/badge/Language-Java-orange?style=flat-square&logo=java"/>
  <img src="https://img.shields.io/badge/Backend-Supabase-3ECF8E?style=flat-square&logo=supabase"/>
  <img src="https://img.shields.io/badge/CV-OpenCV-5C3EE8?style=flat-square&logo=opencv"/>
  <img src="https://img.shields.io/badge/Min%20SDK-API%2024-blue?style=flat-square"/>
</p>

---

## Overview

PocketGuru is a mobile application built for primary school students to learn how plants produce food through an engaging, level-based experience. Rather than relying on static text, every level features a unique interactive activity — from drawing the water path through a plant's stem to completing the photosynthesis equation via drag-and-drop. A Quick Visualisation Hub uses OpenCV to scan a real leaf from the student's environment and display it alongside an interactive scientific diagram, grounding illustrated content in the physical world.

Built as a solo alternative assessment project for WIG3003 Multimedia Programming at Universiti Malaya.

---

## Features

- **6 Interactive Levels** — each with a unique interaction (drawing, flipping, dragging, tapping)
- **Quick Visualisation Hub** — scan a real leaf via camera, validated by OpenCV HSV thresholding and contour detection
- **Flashcards** — tap-to-flip keyword review with 9 cards
- **Mix-and-Match** — drag-and-connect mini-game with animated connecting lines
- **Final Assessment** — 8 questions with immediate feedback, review mode, and unlimited retries
- **Keywords List** — save, pronounce, and delete key science terms
- **Lottie Animations** — tree growing, fruit appearing, breathing cycle
- **Sound Effects** — contextual audio for drawing, flipping, correct answers, and level completion
- **Supabase Backend** — cloud-synced level progress and personal keywords list
- **Session Persistence** — stays logged in across app restarts

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java (primary), Kotlin (Supabase SDK) |
| IDE | Android Studio |
| Minimum SDK | API 24 (Android 7.0) |
| Navigation | Jetpack Navigation Component |
| Backend | Supabase (Auth + PostgreSQL via PostgREST) |
| Computer Vision | OpenCV Android SDK 4.9.0 |
| Animation | Lottie 6.4.0 |
| Image Loading | Glide 4.16.0 |
| Audio | Android SoundPool |
| Text-to-Speech | Android TextToSpeech API |

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android device or emulator running API 24+
- A Supabase project with the schema set up (see below)

### Installation

1. Clone the repository
```bash
   git clone https://github.com/yourusername/pocketguru.git
```

2. Open the project in Android Studio

3. Set up your Supabase credentials in `SupabaseManager.kt`:
```kotlin
   private const val SUPABASE_URL = "your_supabase_url"
   private const val SUPABASE_ANON_KEY = "your_anon_key"
```

4. Run the SQL schema in your Supabase SQL editor:
```sql
   create table users (
     id uuid primary key references auth.users,
     username text unique not null,
     created_at timestamp default now()
   );

   create table level_progress (
     id uuid primary key default gen_random_uuid(),
     user_id uuid references users(id),
     current_level int default 1,
     updated_at timestamp default now()
   );

   create table keywords (
     id uuid primary key default gen_random_uuid(),
     user_id uuid references users(id),
     word text not null,
     definition text not null,
     created_at timestamp default now()
   );
```

5. Set up Row Level Security policies:
```sql
   alter table users enable row level security;
   alter table level_progress enable row level security;
   alter table keywords enable row level security;

   create policy "Allow all for authenticated users" on users
     for all using (auth.uid() = id);
   create policy "Allow all for authenticated users" on level_progress
     for all using (auth.uid() = user_id);
   create policy "Allow all for authenticated users" on keywords
     for all using (auth.uid() = user_id);
```

6. Build and run the app on your device

---

## Project Structure

app/src/main/

├── assets/                  # Lottie JSON animation files

├── java/com/example/pocketguru/

│   ├── auth/                # Register, Login, Splash, Welcome fragments

│   ├── levels/              # Level 1–6 fragments and LevelCompleteFragment

│   ├── minigames/           # Flashcards, MixAndMatch, Assessment fragments

│   ├── hub/                 # QuickVisualizationFragment, VideoPlayerFragment

│   ├── keywords/            # KeywordsListFragment, KeywordsAdapter

│   ├── models/              # KeywordItem, Question data classes

│   ├── supabase/            # SupabaseManager.kt

│   ├── utils/               # SessionManager, DataPreloader, LevelProgressManager,

│   │                        # KeywordTooltipHelper, SoundManager, ToastHelper

│   └── views/               # DrawingView, AnnotationLineView, LineOverlayView

└── res/

├── anim/                # Fade in/out transition animations

├── drawable/            # Icons, backgrounds, node drawables

├── font/                # Josefin Sans font family

├── layout/              # All fragment and item XML layouts

├── navigation/          # nav_graph.xml

└── raw/                 # Sound effects and photosynthesis video

---

## Screenshots

| Splash | Level Map | Level 2 | Quick Vis Hub |
|---|---|---|---|
| ![](assets/screenshots/splash.png) | ![](assets/screenshots/level_map.png) | ![](assets/screenshots/level2.png) | ![](assets/screenshots/hub.png) |

| Flashcards | Mix and Match | Assessment | Keywords |
|---|---|---|---|
| ![](assets/screenshots/flashcards.png) | ![](assets/screenshots/mix_match.png) | ![](assets/screenshots/assessment.png) | ![](assets/screenshots/keywords.png) |

---

## Known Limitations

- Password reset is not currently supported — users who forget their password must register a new account
- The Quick Visualisation Hub leaf scan requires a well-lit environment and a healthy green leaf for reliable detection
- The app is not published on the Google Play Store and must be installed via APK sideloading

---

## Acknowledgements

- [Supabase](https://supabase.com) — open source backend
- [OpenCV](https://opencv.org) — computer vision library
- [Lottie by Airbnb](https://airbnb.io/lottie) — animation library
- [Glide](https://github.com/bumptech/glide) — image and GIF loading
- Universiti Malaya — WIG3003 Multimedia Programming

---

## License

This project was developed as an academic assessment submission for Universiti Malaya. Not licensed for commercial use.
