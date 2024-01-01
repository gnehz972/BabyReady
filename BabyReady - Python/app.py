from typing import Annotated

from fastapi import FastAPI, UploadFile

import test_model
import shutil


app = FastAPI()


@app.get("/ping/")
async def ping():
    return {"status": "OK"}


@app.post("/predict/")
async def predict(file: UploadFile):
    with open("recording.wav", "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
    result = test_model.main()
    return {"predict": result}