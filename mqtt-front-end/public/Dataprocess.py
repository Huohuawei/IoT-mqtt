
from flask import Flask, jsonify
from flask_cors import CORS
import pandas as pd
import numpy as np
from scipy.optimize import curve_fit
import chardet
import os

app = Flask(__name__)
CORS(app)

# 文件路径
FILE_PATH = r"D:\temporary-deposit\temperature.txt"

# 自动检测文件编码
def detect_file_encoding(filepath):
    with open(filepath, "rb") as f:
        raw_data = f.read()
    result = chardet.detect(raw_data)
    return result["encoding"]

# 清洗数据：剔除低于5°C的点
def clean_temperature_data(df):
    df = df[df["avg_temp"] >= 5]  # 剔除低于5°C的点
    clean_data = []
    for i in range(len(df)):
        curr_temp = df.iloc[i]["avg_temp"]
        if i == 0 or i == len(df) - 1:
            clean_data.append(df.iloc[i])
        else:
            prev_temp = df.iloc[i - 1]["avg_temp"]
            next_temp = df.iloc[i + 1]["avg_temp"]
            if abs(curr_temp - prev_temp) > 2 and abs(curr_temp - next_temp) > 2:
                continue
            clean_data.append(df.iloc[i])
    return pd.DataFrame(clean_data)

# 读取和处理 temperature.txt 文件
def load_and_process_temperature_data(filepath):
    file_encoding = detect_file_encoding(filepath)
    print(f"Detected encoding: {file_encoding}")

    combined_data = []
    with open(filepath, "r", encoding=file_encoding) as file:
        for line in file:
            try:
                data = eval(line.strip())
                combined_data.append(data)
            except Exception as e:
                print("Error parsing line:", e)

    records = []
    for data_dict in combined_data:
        for time, temp in data_dict.items():
            try:
                if not temp.strip():
                    continue
                records.append({"time": time, "temp": float(temp)})
            except Exception as e:
                print(f"Skipping invalid value: {temp}, error: {e}")

    df = pd.DataFrame(records)
    df["time_only"] = pd.to_datetime(df["time"]).dt.strftime("%H:%M")
    aggregated_data = df.groupby("time_only")["temp"].mean().reset_index()
    aggregated_data = aggregated_data.rename(columns={"time_only": "time", "temp": "avg_temp"})

    aggregated_data = clean_temperature_data(aggregated_data)
    return aggregated_data

# 正弦波拟合函数
def sinusoidal_fit(x, a, b, c, d):
    return a * np.sin(b * x + c) + d

@app.route("/predict", methods=["GET"])
def predict():
    try:
        if not os.path.exists(FILE_PATH):
            raise FileNotFoundError(f"File not found: {FILE_PATH}")

        data = load_and_process_temperature_data(FILE_PATH)

        # 构建时间序列
        data["time_minutes"] = range(len(data))
        x_data = data["time_minutes"].to_numpy()
        y_data = data["avg_temp"].to_numpy()

        # 使用正弦波拟合
        params, _ = curve_fit(sinusoidal_fit, x_data, y_data, p0=[5, 2 * np.pi / len(x_data), 0, np.mean(y_data)])
        a, b, c, d = params

        # 生成未来预测
        future_minutes = np.arange(len(x_data) + 72)  # 未来72个点
        predictions = sinusoidal_fit(future_minutes, a, b, c, d)

        # 返回结果
        cleaned_data = data[["time", "avg_temp"]].rename(columns={"avg_temp": "temp"}).to_dict(orient="records")
        prediction_times = pd.date_range(start=data["time"].iloc[0], periods=len(predictions), freq="10min").strftime('%H:%M').tolist()
        predictions = [{"time": prediction_times[i], "temp": predictions[i]} for i in range(len(predictions))]

        return jsonify({"cleaned_data": cleaned_data, "predictions": predictions})
    except Exception as e:
        print("Error occurred:", e)
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(debug=True)
