from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from pydantic import BaseModel

app = FastAPI()

# Module-level so tests can flip it with monkeypatch.setattr(exceptions, "model_loaded", ...).
# This would be determined by your actual model loading logic.
model_loaded = False


class PredictionRequest(BaseModel):
    input_data: str
    max_tokens: int = 50


class ModelNotLoadedError(Exception):
    def __init__(self, name: str):
        self.name = name
        self.message = f"Model '{self.name}' is not loaded."
        super().__init__(self.message)


@app.exception_handler(ModelNotLoadedError)
async def model_not_loaded_exception_handler(
    request: Request, exc: ModelNotLoadedError
):
    return JSONResponse(
        status_code=503,
        content={"message": f"{exc.name} isn't loaded yet."},
    )


@app.post("/predict")
async def predict(request: PredictionRequest):
    if not model_loaded:
        raise ModelNotLoadedError(name="MyModel")

    # If the model is loaded, proceed with prediction (this is just a placeholder)
    prediction_result = {"prediction": "some_prediction_based_on_input_data"}

    return prediction_result
