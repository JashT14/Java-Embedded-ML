import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report
from sklearn.pipeline import Pipeline
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType

df=pd.read_csv('IRIS.csv')
print(df.shape)
print(f"unique classes: {df['species'].unique()}")
print(f"\nClass distribution:\n{df['species'].value_counts()}")

X=df[['sepal_length', 'sepal_width', 'petal_length', 'petal_width']].values
y=df['species'].values
label_encoder=LabelEncoder()#label encoder - string labels to int
y_encoded=label_encoder.fit_transform(y)#class labels to species
print(f"Label mapping: {dict(enumerate(label_encoder.classes_))}")

X_train,X_test,y_train,y_test=train_test_split(X, y_encoded,test_size=0.2,random_state=42,stratify=y_encoded)#tstratify to maintain proportions
print(f"Training samples:{len(X_train)}")
print(f"Testing samples:{len(X_test)}")
#creating pipeline
pipeline = Pipeline([
    ('scaler', StandardScaler()),
    ('classifier', RandomForestClassifier(n_estimators=100, random_state=42, max_depth=5))
])#standard scalar to standize the data, and random forest as classifier

pipeline.fit(X_train, y_train)

y_pred=pipeline.predict(X_test)
accuracy=accuracy_score(y_test,y_pred)
print(f"Test Accuracy: {accuracy * 100:.2f}%")
print("\nClassification Report:")
print(classification_report(y_test, y_pred, target_names=label_encoder.classes_))

print("\n[Sample Predictions]")
test_samples = [
    [5.1, 3.5, 1.4, 0.2],#Iris-setosa
    [6.7, 3.0, 5.0, 1.7],#Iris-versicolor
    [6.3, 2.9, 5.6, 1.8],#Iris-virginica
]

for sample in test_samples:
    pred = pipeline.predict([sample])[0]
    species = label_encoder.classes_[pred]
    print(f"Input: {sample} \n Predicted: {species}\n\n")

#converting pipeline to onnx model
initial_type = [('float_input', FloatTensorType([None, 4]))]#float-tensor to convert normal float to multi-dim array
onnx_model=convert_sklearn(
    pipeline,
    initial_types=initial_type,
    target_opset=12                 #for compatability
)

output_path = "D:/caffine/java_embedded_ml/java-legacy-app/src/main/resources/model.onnx"
with open(output_path, "wb") as f:
    f.write(onnx_model.SerializeToString())