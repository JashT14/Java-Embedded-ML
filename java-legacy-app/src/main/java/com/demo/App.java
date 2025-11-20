package com.demo;
import java.util.HashMap;
import java.util.Map;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class App{
    private static PredictionService predictionService;
    public static void main(String[] args) {
        predictionService = new PredictionService();//initialize service
        try{
            predictionService.initialize();
        }
        catch (Exception e) {
            System.err.println("Failed to initialize prediction service: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7070);//javalin start
        
        System.out.println("\nServer on http://localhost:7070");

        //routes defined
        app.post("/predict",App::handlePredict);
        app.get("/health",App::handleHealth);
        app.get("/test",App::handleTest);
    }

    private static void handlePredict(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);//input from body
            
            float sepalLength=((Number) body.get("sepal_length")).floatValue();
            float sepalWidth=((Number) body.get("sepal_width")).floatValue();
            float petalLength=((Number) body.get("petal_length")).floatValue();
            float petalWidth=((Number) body.get("petal_width")).floatValue();
            
            float[] input=new float[]{sepalLength, sepalWidth, petalLength, petalWidth};
            
            long startTime = System.nanoTime();
            long predictedClass = predictionService.predict(input);//inprocess method call
            long endTime = System.nanoTime();
            
            double latencyMs=(endTime-startTime)/1_000_000.0;//time calculation
            String species=predictionService.getSpeciesName(predictedClass);

            Map<String, Object> response = new HashMap<>();
            response.put("input", Map.of(
                "sepal_length", sepalLength,
                "sepal_width", sepalWidth,
                "petal_length", petalLength,
                "petal_width", petalWidth
            ));
            response.put("predicted_class",predictedClass);
            response.put("species",species);
            response.put("latency_ms",String.format("%.3f", latencyMs));
            
            ctx.json(response);
            
            System.out.printf("Prediction: [%.1f, %.1f, %.1f, %.1f] → %s (%.3f ms)\n", 
                sepalLength, sepalWidth, petalLength, petalWidth, species, latencyMs);
        } catch(Exception e){
            ctx.status(500).json(Map.of("error", e.getMessage()));
            e.printStackTrace();
        }
    }

    //route just for checking if server is running
    private static void handleHealth(Context ctx) {
        ctx.json(Map.of(
            "status", "healthy",
            "service", "java embedded ml - iris classifier",
            "model", "RandomForest with StandardScaler",
            "classes", new String[]{"Iris-setosa", "Iris-versicolor", "Iris-virginica"}
        ));
    }
    //test route for api endpoint
    private static void handleTest(Context ctx) {
        try{
            float[][] testSamples = {
                {5.1f, 3.5f, 1.4f, 0.2f},//Iris-setosa
                {6.7f, 3.0f, 5.0f, 1.7f},//Iris-versicolor
                {6.3f, 2.9f, 5.6f, 1.8f} //Iris-virginica
            };
            String[] expectedSpecies = {
                "Iris-setosa",
                "Iris-versicolor",
                "Iris-virginica"
            };
            
            Map<String, Object> results = new HashMap<>();
            boolean allPassed = true;
            
            for (int i = 0; i < testSamples.length; i++) {
                long predictedClass=predictionService.predict(testSamples[i]);
                String species=predictionService.getSpeciesName(predictedClass);
                boolean passed=species.equals(expectedSpecies[i]);
                allPassed = allPassed && passed;
                results.put("test_" + (i + 1), Map.of(
                    "input", testSamples[i],
                    "expected", expectedSpecies[i],
                    "predicted", species,
                    "status", passed ? "PASS" : "FAIL"
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("overall_status", allPassed ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
            response.put("tests", results);
            
            ctx.json(response);
            
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
            e.printStackTrace();
        }
    }
}