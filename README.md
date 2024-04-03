# Simple HTTP Server

 A basic HTTP server built on top `ServerSocket` in java.
 
## Features
- Handles GET requests
- Serves static files
- Basic HTTP errors handling
- Logging features
  - In the `stderr`
  - In a `logs.txt` file
- Multithreading
- Interprets and returns Python scripts output (stored on the server)

## Installation
Clone project, and compile the source code using a Java compiler.

## Usage
By  default, the servers listens on port **80** and serves files in the `resources` directory.
Edit configuration in `utils.Config`