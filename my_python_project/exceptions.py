# Imports: FastAPI builds the app, Request describes an incoming HTTP request,
# JSONResponse lets us set a custom status code and JSON body, and BaseModel
# is the Pydantic base class used to validate request data.
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from pydantic import BaseModel

# Create the application object. The routes and exception handler below are
# registered on it through the @app decorators.
app = FastAPI()

# Module-level so tests can flip it with monkeypatch.setattr(exceptions, "model_loaded", ...).
# This would be determined by your actual model loading logic.
model_loaded = False


# Defines the JSON body that /predict accepts. FastAPI validates each request
# against these fields and returns a 422 error if the data doesn't fit.
# input_data is required; max_tokens is optional and defaults to 50.
class PredictionRequest(BaseModel):
    input_data: str
    max_tokens: int = 50


# Custom exception raised when a prediction is requested before the model is
# ready. It stores the model's name so the handler can use it, builds a
# readable message, and passes that message to the Exception base class so
# str(error) returns it.
class ModelNotLoadedError(Exception):
    def __init__(self, name: str):
        self.name = name
        self.message = f"Model '{self.name}' is not loaded."
        super().__init__(self.message)


# Tells FastAPI to run this function whenever ModelNotLoadedError goes
# uncaught in a route. FastAPI passes in the request and the exception, and
# the client gets a 503 (Service Unavailable) JSON response instead of a
# generic 500 error. Note that this builds its own text from exc.name rather
# than using exc.message.
@app.exception_handler(ModelNotLoadedError)
async def model_not_loaded_exception_handler(
    request: Request, exc: ModelNotLoadedError
):
    return JSONResponse(
        status_code=503,
        content={"message": f"{exc.name} isn't loaded yet."},
    )


# POST /predict route. FastAPI validates the JSON body into a
# PredictionRequest (so "request" here is the body, not the Request class
# used above). If the model isn't loaded, raising the exception stops the
# route and hands control to the handler above; otherwise the returned dict
# is sent back to the client as JSON with a 200 status.
@app.post("/predict")
async def predict(request: PredictionRequest):
    if not model_loaded:
        raise ModelNotLoadedError(name="MyModel")

    # If the model is loaded, proceed with prediction (this is just a placeholder)
    prediction_result = {"prediction": "some_prediction_based_on_input_data"}

    return prediction_result
