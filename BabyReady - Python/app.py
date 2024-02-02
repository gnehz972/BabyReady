from typing import Annotated

from fastapi import FastAPI, UploadFile

import test_model
import shutil


app = FastAPI()


@app.get("/api/ping")
async def ping():
    return {"status": "OK"}


@app.post("/api/predict")
async def predict(file: UploadFile):
    with open("recording.wav", "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
    result = test_model.main()
    return {"predict": result}