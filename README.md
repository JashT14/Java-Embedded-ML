# Java Embedded ML

> **Zero-Overhead AI Inference in Legacy Java Applications**

## 🎯 Project Overview

This project demonstrates how to embed a Python-trained machine learning model directly into a monolithic Java application, eliminating all network and microservice overhead. 

**The Big Idea:** Why deploy a separate ML server and add network latency when you can run AI inference *inside* your existing Java monolith? This is the ultimate low-overhead solution for adding AI capabilities to legacy systems.

### Key Benefits

- ✅ **Zero Network Latency** - Direct Java method calls, no HTTP/gRPC overhead
- ✅ **Zero Architectural Overhead** - No new microservices to deploy or manage
- ✅ **Minimal Dependencies** - ML as a simple library, not a new stack
- ✅ **High Performance** - Powered by ONNX Runtime's C++ backend
- ✅ **Clean MLOps Handoff** - Data scientists train in Python, developers serve in Java

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│         Legacy Java Monolith (JDK 11)                   │
│                                                           │
│  ┌──────────────┐      ┌─────────────────┐             │
│  │   Business   │──────▶│  Prediction     │             │
│  │    Logic     │       │   Service       │             │
│  │  (Javalin)   │       │   (DJL +        │             │
│  │              │       │  ONNX Runtime)  │             │
│  └──────────────┘      └─────────────────┘             │
│         │                       │                        │
│         │                       ▼                        │
│         │              ┌─────────────────┐              │
│         │              │   model.onnx    │              │
│         │              │ (Embedded in    │              │
│         │              │  JAR resources) │              │
│         │              └─────────────────┘              │
└─────────────────────────────────────────────────────────┘
         │
         ▼
    Direct Method Call
    (No network latency!)
```

---

## 🚀 Quick Start

### Prerequisites

- **Java JDK 11** (emulates legacy Java systems)
- **Maven 3.6+**
- **Python 3.7+** (for model training only)
- **Postman or cURL** (for testing the API)

### Step 1: Train and Export the Model

```bash
cd model-training
pip install scikit-learn onnx skl2onnx
python create_model.py
```

This creates `model.onnx` in the `java-legacy-app/src/main/resources/` directory.

### Step 2: Build the Application

```bash
cd ../java-legacy-app
mvn clean package
```

This compiles the Java code and packages `model.onnx` inside the final JAR file.

### Step 3: Run the Application

```bash
java -jar target/embedded-ml-monolith-1.0-SNAPSHOT.jar
```

The server starts on `http://localhost:7070`

---

## 🧪 Testing the Prediction Service

Use **Postman**, **cURL**, or any API testing tool to test the `/predict` endpoint.

### Example Request (cURL)

```bash
curl -X POST http://localhost:7070/predict \
  -H "Content-Type: application/json" \
  -d '[1.5, 2.3, 3.1, 4.0]'
```

### Example Request (Postman)

- **Method:** POST
- **URL:** `http://localhost:7070/predict`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
  ```json
  [1.5, 2.3, 3.1, 4.0]
  ```

### Expected Response

```json
[0.7234, 0.2766]
```

The response is a probability distribution (or prediction output) from the embedded ML model.

---

## 📂 Project Structure

```
embedded-ml-monolith/
├── model-training/
│   └── create_model.py          # Python script to train and export model
├── java-legacy-app/
│   ├── pom.xml                  # Maven dependencies (DJL, ONNX, Javalin)
│   └── src/main/
│       ├── java/com/demo/
│       │   ├── App.java                    # Main class, starts server
│       │   ├── PredictionService.java      # Loads and manages ML model
│       │   └── PredictionHandler.java      # Handles HTTP requests
│       └── resources/
│           └── model.onnx       # ML model embedded in JAR
└── README.md
```

---

## 🔧 Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **ML Inference Engine** | Deep Java Library (DJL) + ONNX Runtime | High-performance model execution in Java |
| **Legacy App Simulator** | Javalin | Lightweight web framework (no Spring overhead) |
| **Build Tool** | Apache Maven | Dependency management and JAR packaging |
| **Model Format** | ONNX | Universal ML model format |
| **Target Runtime** | Java JDK 11 | Emulates legacy enterprise Java systems |

---

## 🎯 How It Works

### Workflow

1. **Offline Training (Python)**
   - Data scientist trains model using scikit-learn
   - Model is exported to `model.onnx` using `skl2onnx`

2. **Build & Embedding (Maven)**
   - `model.onnx` placed in `src/main/resources`
   - Maven packages it inside the application JAR

3. **Runtime Initialization (Java)**
   - On startup, `PredictionService` loads `model.onnx` from JAR resources
   - DJL creates a reusable `Predictor` object

4. **Live Inference (Java)**
   - Business logic calls `predictionService.predict(data)`
   - **Direct method call** - no network, no latency
   - Returns prediction instantly

### Key Implementation: Zero-Latency Inference

```java
// In App.java - Direct, in-process method call
Javalin.create().start(7070)
    .post("/predict", ctx -> {
        float[] input = ctx.bodyAsClass(float[].class);
        
        // No HTTP, no gRPC, no network overhead
        float[] result = predictionService.predict(input);
        
        ctx.json(result);
    });
```

---

## 💡 Use Cases

This pattern is ideal for:

- **Legacy Modernization** - Add AI to existing Java monoliths without rewriting
- **Low-Latency Requirements** - Sub-millisecond inference times
- **Embedded Systems** - Run AI on edge devices with limited connectivity
- **Cost Optimization** - Eliminate separate ML infrastructure
- **Compliance** - Keep sensitive data within existing security boundaries

---

## 📊 Performance Characteristics

- **Inference Latency**: < 1ms (direct method call)
- **Startup Time**: ~2-3 seconds (model loading)
- **Memory Overhead**: ~50-200MB (depends on model size)
- **Dependencies**: Only DJL and ONNX Runtime JARs

---

## 🤝 Target Audience

- **Systems Engineers** - Interested in low-latency architecture
- **MLOps Engineers** - Exploring deployment patterns
- **Tech Recruiters** - Evaluating full-stack ML capabilities
- **Legacy App Maintainers** - Adding modern AI to old systems

---

## 📝 License

MIT License - Feel free to use this pattern in your projects!

---

## 🙋 Questions?

This project demonstrates a practical approach to embedding AI in legacy systems. The core principle: **sometimes the best architecture is the simplest one** - no microservices needed, just a JAR dependency and a model file.

**Built with ❤️ to show that legacy Java apps can run modern AI without the overhead.**