package com.demo;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PredictionServiceTest{
    @Test
    public void testModelLoadsAndPredicts(){
        try{
            PredictionService service = new PredictionService();
            Assertions.assertNotNull(service, "Service instantiation failed");
            service.initialize();//load the model

            float[] dummyInput = new float[]{5.1f, 3.5f, 1.4f, 0.2f};//sample dummy - iris setosa

            long result = service.predict(dummyInput);//prediction

            Assertions.assertTrue(result >= 0 && result <= 2, 
                "Prediction should be a valid class index (0-2). Got: " + result);
            System.out.println("Input: " + Arrays.toString(dummyInput));
            System.out.println("Output Prediction Class Index: " + result);
            System.out.println("Predicted Species: " + service.getSpeciesName(result));
            service.close();

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Prediction failed: " + e.getMessage());
        }
    }
}