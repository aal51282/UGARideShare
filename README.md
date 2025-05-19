# Rideshare Mobile Application

A ride-sharing Android application designed for users to offer and request rides in their local area.

## Overview

RideShare is a community-driven platform that facilitates carpooling and ride sharing. The app uses a points-based system where users can earn or spend points when offering or requesting rides.

## Features

- **User Authentication**: Secure login and registration system using Firebase Authentication
- **Ride Offers**: Post rides you're offering to other users
- **Ride Requests**: Post requests for rides you need
- **Points System**: Earn points for offering rides and spend points when requesting rides
- **Ride Management**: Accept, update, or cancel rides
- **Navigation Drawer**: Easy navigation between different sections of the app

## Technical Stack

- **Language**: Java
- **Platform**: Android
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Authentication
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 35

## Project Structure

- **activities/**: Main activity screens such as Login, Register, and Main activities
- **fragments/**: UI components for different sections like Ride Offers, Ride Requests, etc.
- **models/**: Data models including User, RideOffer, RideRequest, and AcceptedRide
- **adapters/**: RecyclerView adapters for displaying lists of rides
- **utils/**: Utility classes for Firebase interactions and session management

## Getting Started

### Prerequisites

- Android Studio
- JDK 11 or higher
- An active Firebase project

### Installation

1. Clone the repository:

   ```
   git clone https://github.com/aal51282/UGARideShare.git
   ```

2. Open the project in Android Studio

3. Connect to your Firebase project:

   - Add your `google-services.json` file to the app directory
   - Ensure Firebase Authentication and Realtime Database are enabled in your Firebase console

4. Build and run the project on an emulator or physical device

## Usage

1. **Register/Login**: Create an account or log in with your credentials
2. **Offer a Ride**: Share your journey with others and earn points
3. **Request a Ride**: Find someone heading your way by spending points
4. **Manage Rides**: View and manage your accepted rides

## How the Points System Works

- New users start with 100 points
- Offering rides earns you points
- Requesting rides costs points
- The points system encourages active participation and fair usage

## License

This project is for educational purposes.
