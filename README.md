# Java Embedded ML

**Zero-Overhead AI Inference in Legacy Java Applications**

## Overview

This project demonstrates embedding a Python-trained machine learning model directly into a Java 11 monolithic application. The model runs in-process with sub-millisecond inference latency, eliminating network overhead and external service dependencies.

### Key Features

- **Sub-millisecond Inference** - Direct Java method calls with latency under 1ms
- **Zero Network Overhead** - No HTTP/gRPC calls, no microservices
- **Embedded Model** - ONNX model packaged inside the application JAR
- **Legacy Compatible** - Runs on Java 11, suitable for legacy enterprise systems
- **Simple Integration** - Model as a resource, loaded at startup

---

## Prerequisites

- Java JDK 11 (or above)
- Apache Maven 3.6+
- Python 3.7+ (for model training only)
- API testing tool (Postman, cURL, or similar)

---

## Build and Run

### 1. Train and Export Model

```bash
python create_model.py
```

Generates `model.onnx` and places it in `java-legacy-app/src/main/resources/`.

### 2. Build Application

```bash
cd java-legacy-app
mvn clean package
```

### 3. Run Application

```bash
java -jar target/java-embedded-ml-1.0-SNAPSHOT.jar
```

Server starts at `http://localhost:7070`

---

## API Endpoints

### POST `/predict`

Classify iris flower based on measurements.

**Request Body:**
```json
{
  "sepal_length": 5.1,
  "sepal_width": 3.5,
  "petal_length": 1.4,
  "petal_width": 0.2
}
```

**Expected Response:**
```json
{
  "input": {
    "sepal_length": 5.1,
    "sepal_width": 3.5,
    "petal_length": 1.4,
    "petal_width": 0.2
  },
  "predicted_class": 0,
  "species": "Iris-setosa",
  "latency_ms": "0.847"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:7070/predict \
  -H "Content-Type: application/json" \
  -d '{"sepal_length":5.1,"sepal_width":3.5,"petal_length":1.4,"petal_width":0.2}'
```

---

## GET `/health`
Check service status and model information.

## GET `/test`
Run test with known samples.


---

## Testing with Postman

1. **Create POST Request**
   - URL: `http://localhost:7070/predict`
   - Method: POST
   - Headers: `Content-Type: application/json`

2. **Set Request Body**
   ```json
   {
     "sepal_length": 6.7,
     "sepal_width": 3.0,
     "petal_length": 5.0,
     "petal_width": 1.7
   }
   ```

3. **Send Request**
   - Expected: `"species": "Iris-versicolor"`

4. **Test Health Endpoint**
   - URL: `http://localhost:7070/health`
   - Method: GET

---

## Project Structure

```
JAVA_EMBEDDED_ML/
├── java-legacy-app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/demo/
│   │   │   │   ├── App.java                    # Main application, HTTP routes
│   │   │   │   ├── PredictionService.java      # Model loader and predictor
│   │   │   │   └── SimpleOnnxTranslator.java   # DJL translator for ONNX
│   │   │   └── resources/
│   │   │       └── model.onnx                  # Embedded ML model
│   │   └── test/java/com/demo/
│   │       └── PredictionServiceTest.java      # Unit tests
│   └── pom.xml                                 # Maven dependencies
├── create_model.py                             # Python training script
├── IRIS.csv                                    # Training dataset
├── README.md
├── requirements.txt
└── .gitignore
```

---

## Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Inference Engine** | Deep Java Library (DJL) | Java ML framework |
| **Runtime** | ONNX Runtime | High-performance C++ inference backend |
| **Web Server** | Javalin | Lightweight HTTP server |
| **Build Tool** | Maven | Dependency management and packaging |
| **Model Format** | ONNX | Universal ML model format |
| **Training** | Python + scikit-learn | Model development |

---

## Performance Metrics

- **Inference Latency:** < 1ms (measured via `System.nanoTime()`)
- **Startup Time:** 2-3 seconds (includes model loading)
- **Memory Overhead:** ~50-150MB (DJL + ONNX Runtime + model)
- **Model Size:** ~50KB (RandomForest classifier)

---

## Development

### Running Tests

```bash
cd java-legacy-app
mvn test
```

### Retrain Model

```bash
python create_model.py
mvn clean package
java -jar target/java-legacy-app-1.0-SNAPSHOT.jar
```

### Using Custom Models

1. Train your model in Python (scikit-learn, PyTorch, TensorFlow, etc.)
2. Export to ONNX format
3. Replace `model.onnx` in `src/main/resources/`
4. Update `SimpleOnnxTranslator.java` input/output types if needed
5. Update `PredictionService.java` class mappings

---

## Use Cases

- **Legacy System Modernization** - Add AI to existing Java applications
- **Low-Latency Requirements** - Real-time inference with minimal overhead
- **Edge Deployment** - Run ML on devices with limited connectivity
- **Cost Optimization** - Eliminate separate ML infrastructure
- **Regulatory Compliance** - Keep sensitive data within existing boundaries

---

## Workflow

1. **Training Phase (Python)**
   - Data scientist trains model using scikit-learn
   - Model exported to `model.onnx` using `skl2onnx`

2. **Build Phase (Maven)**
   - `model.onnx` placed in `src/main/resources/`
   - Maven packages model inside JAR

3. **Runtime Phase (Java)**
   - `PredictionService` loads model from JAR resources on startup
   - DJL creates reusable `Predictor` object

4. **Inference Phase (Java)**
   - HTTP endpoint receives request
   - Calls `predictionService.predict()` as direct method
   - Returns result with sub-millisecond latency

---

## License

MIT License

---

## Summary

This project demonstrates practical ML inference in legacy Java systems. The approach prioritizes simplicity: no microservices, no network calls, no operational overhead. Just a JAR dependency and an embedded model file delivering sub-millisecond predictions.

This project demonstrates practical ML inference in legacy Java systems. The approach prioritizes simplicity: no microservices, no network calls, no operational overhead. Just a JAR dependency and an embedded model file delivering sub-millisecond predictions.
