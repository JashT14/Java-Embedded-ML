package com.demo;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

//translator for converting java float arrays to djl ndarrays
//handles data conversion for ONNX model inference

public class SimpleOnnxTranslator implements Translator<float[], Long> {
    @Override
    public NDList processInput(TranslatorContext ctx, float[] input){
        NDManager manager=ctx.getNDManager();
        NDArray array=manager.create(input, new Shape(1, 4));//convert float to ndarray
        return new NDList(array);
    }
    @Override
    public Long processOutput(TranslatorContext ctx, NDList list){
        NDArray output = list.get(0);  // Get the label output
        return output.toLongArray()[0];  // Return the predicted class
    }
    
    @Override
    public Batchifier getBatchifier() { //used for converting unbatched ndarrays to batched ndarrays for DL
        return null;                    //not used in this simple project
    }
}