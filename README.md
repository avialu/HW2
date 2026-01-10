# HW2 – Android Arcade Game (Buttons / Sensors) + High Scores & Map

## Overview

This project is the continuation of HW1.
It is an Android arcade-style game where the player controls a car on a **5-lane road**, avoids obstacles, collects coins, and achieves the highest possible score.

The app is implemented using **Android Views (XML)** with **Activities** and **Fragments** (no Jetpack Compose).

---

## Game Features

* **5-lane road**
* **Buttons control mode** (Left/Right buttons)
* **Sensors control mode** (Accelerometer tilt to move between lanes)
* **Multiple obstacles** can appear simultaneously:

  * Each spawn creates **1–3 obstacles**
  * Obstacles can spawn every few ticks, so multiple rows may exist on screen at the same time
* **Multiple coins** can appear simultaneously (more than one on screen at a time)
* **Lives system** (3 lives)
* **Score and distance counter**
* **Sound feedback**

  * Different sound for **coin collection** and **crash**
* **Vibration feedback**

  * Vibration on crash (device dependent)

---

## Menu Screen

The menu allows:

* Start game in **Buttons – Slow**
* Start game in **Buttons – Fast**
* Start game in **Sensors**
* Open the **High Scores** screen

---

## Speed Modes (Buttons)

When playing with buttons, the user can choose between:

* **Slow**
* **Fast**

Speed affects:

* Game tick interval (movement speed)
* Spawn frequency of obstacles/coins (difficulty)

---

## High Scores Screen (Two Fragments)

The High Scores screen is implemented with **two separate fragments**:

1. **TopScoresFragment** – displays a list of the **Top 10 scores** since installation
2. **ScoresMapFragment** – displays a map with markers of the locations where those scores were achieved

Behavior:

* Selecting a score from the list updates the map camera and marker to the selected score location.

---

## Data Persistence

* High scores are saved locally so they remain available between app launches.
* Each saved record includes:

  * Score
  * Distance
  * Timestamp
  * Location (Latitude/Longitude)

---

## Technologies Used

* Kotlin
* Android SDK
* XML Layouts (Views)
* Activities & Fragments
* RecyclerView
* ViewModel / LiveData (shared between fragments)
* Google Maps SDK

---

## Demo Video

A demo video is included in the submission, showing:

* Menu navigation
* Buttons mode (Slow/Fast)
* Sensors mode
* Coin collection and obstacle collisions (sound feedback)
* Transition to High Scores screen
* Selecting a score and updating the map location

---

## Author

Name: **AVIA LURIE**
