# exceptions_test.py
# --------------------------------------------------------------------------------------
# Tests for the custom exceptions defined in exceptions.py
# Run from the repo root with:  python -m pytest -v
# (-v prints each test name so you can see what passed.)

import pytest  # for parametrize and raises xfail
from fastapi.testclient import (
    TestClient,
)  # non-server way to send fake requests to the FastAPI app

import exceptions  # my exceptions.py file, which defines the FastAPI app and custom exceptions
from exceptions import app, ModelNotLoadedError, PredictionRequest

client = TestClient(app)  # Fake browser tied to app


# SCENARIO 1: MODEL NOT LOADED = Error 503
def test_503_without_tokens(monkeypatch):
    # monkeypatch = temporary change, auto-undone by pytest when the test ends.
    monkeypatch.setattr(
        exceptions, "model_loaded", False
    )  # force model to be "not loaded"
    response = client.post("/predict", json={"input_data": "Shalom!"})
    assert response.status_code == 503
    assert response.json() == {"message": "MyModel isn't loaded yet."}


# SCENARIO 2: MODEL NOT LOADED = Error 503 even with explicitly indicating tokens
def test_503_with_tokens(monkeypatch):
    monkeypatch.setattr(exceptions, "model_loaded", False)
    response = client.post(
        "/predict",
        json={"input_data": "Shalom!", "max_tokens": 10},
    )
    assert response.status_code == 503
    assert response.json() == {"message": "MyModel isn't loaded yet."}


# SCENARIO 3: MODEL LOADED = Error 422 for missing input_data, wrong types, and empty bodies


# 3.1 - missing input_data
def test_missing_input_data_returns_422():
    # input_data is required, so Pydantic rejects this before predict() runs
    response = client.post("/predict", json={"max_tokens": 10})
    assert response.status_code == 422


# 3.2 - bad input_data types (int, list, str instead of int) and bad max_tokens type (str instead of int)
@pytest.mark.parametrize(  # runs the test once per bad body below
    "bad_body",
    [
        {"input_data": 123},  # int, not str
        {"input_data": ["a", "b"]},  # list, not str
        {"input_data": "hi", "max_tokens": "lots"},  # str, not int
    ],
)
def test_wrong_types_return_422(bad_body):
    response = client.post("/predict", json=bad_body)
    assert response.status_code == 422


# 3.3 - empty body
def test_empty_json_object_returns_422():
    assert client.post("/predict", json={}).status_code == 422


def test_no_body_at_all_returns_422():
    assert client.post("/predict").status_code == 422


# SCENARIO 4: Error 405 (route exists but only for POST, not GET)
def test_get_on_predict_returns_405():
    assert client.get("/predict").status_code == 405


# SCENARIO 5: Error 404 (route doesn't exist)
def test_unknown_route_returns_404():
    assert client.get("/does-not-exist").status_code == 404


# SCENARIO 6: max_tokens defaults to 50 (tests the Pydantic model directly, no HTTP)
def test_max_tokens_defaults_to_50():
    assert PredictionRequest(input_data="hi").max_tokens == 50


# SCENARIO 7: the exception keeps its name and message
def test_model_not_loaded_error_keeps_name_and_message():
    err = ModelNotLoadedError(name="MyModel")
    assert err.name == "MyModel"
    assert err.message == "Model 'MyModel' is not loaded."
    assert str(err) == "Model 'MyModel' is not loaded."  # from super().__init__(...)


# SCENARIO 8: Model loaded -> 200 with a "prediction" key in the response
def test_loaded_returns_200_with_prediction_key(monkeypatch):
    monkeypatch.setattr(exceptions, "model_loaded", True)
    response = client.post("/predict", json={"input_data": "hello"})
    assert response.status_code == 200
    assert "prediction" in response.json()
