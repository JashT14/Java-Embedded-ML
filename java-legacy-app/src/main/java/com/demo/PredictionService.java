package com.demo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;

public class PredictionService {
    private Predictor<float[], Long> predictor;
    private ZooModel<float[], Long> model;
    //iris species mapping
    private static final String[] SPECIES = {
        "Iris-setosa",//0
        "Iris-versicolor",//1
        "Iris-virginica"//2
    };
    
    public void initialize() throws IOException, ModelNotFoundException, MalformedModelException{
        System.out.println("loading embedded onnx model.......");
        try{
        InputStream modelStream=getClass().getResourceAsStream("/model.onnx");//getting model as input-stream
        if (modelStream == null) {
            throw new IOException("Model file 'model.onnx' not found in resources!");
        }
        
        Path tempFile=Files.createTempFile("model", ".onnx");//copy stream to a temp filepath
        tempFile.toFile().deleteOnExit();//cleanups
        Files.copy(modelStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        modelStream.close();
        
        //criteria for loading the ONNX model
        Criteria<float[], Long> criteria = Criteria.builder()
                .setTypes(float[].class, Long.class)//input as float, output as long
                .optModelPath(tempFile)
                .optEngine("OnnxRuntime")
                .optTranslator(new SimpleOnnxTranslator())
                .build();
        this.model=criteria.loadModel();//load model
        this.predictor=model.newPredictor();//build the predictor
        
        System.out.println("model loaded & ready for predictions.");
    }
    catch(Exception e){
        e.printStackTrace();
    }
    }
    
    public long predict(float[] input) throws TranslateException{
        if(predictor==null){
            throw new IllegalStateException("model not initialized. call initialize() first.");
        }
        
        if(input.length!=4){
            throw new IllegalArgumentException("input should have exactly 4 features");
        }
        return predictor.predict(input);//returns 0,1,2 - class labels
    }
    
    public String getSpeciesName(long label){
        if (label>=0 && label<SPECIES.length){
            return SPECIES[(int) label];//return species name based on class label
        }
        return "Unknown";
    }
    
    public void close() {
        if(predictor!=null){
            predictor.close();
        }
        if(model != null){
            model.close();
        }
        System.out.println("service closed.");
    }
}