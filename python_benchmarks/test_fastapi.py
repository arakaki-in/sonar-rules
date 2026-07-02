import asyncio
import time
import pytest
import httpx
from fastapi import FastAPI
from fastapi.testclient import TestClient

app = FastAPI()

@app.get("/async-io")
async def async_io():
    # Simulate non-blocking async sleep
    await asyncio.sleep(0.002)
    return {"status": "ok"}

@app.get("/sync-io")
def sync_io():
    # Simulate blocking sync sleep
    time.sleep(0.002)
    return {"status": "ok"}

def run_async(func):
    return asyncio.run(func())

# Benchmark 1: Concurrent non-blocking async requests
def test_async_nonblocking_concurrency(benchmark):
    async def run():
        async with httpx.AsyncClient(app=app, base_url="http://test") as client:
            # Fire 15 concurrent requests using asyncio.gather
            tasks = [client.get("/async-io") for _ in range(15)]
            await asyncio.gather(*tasks)
            
    benchmark(run_async, run)

# Benchmark 2: Sequential blocking requests
def test_sync_blocking_sequential(benchmark):
    def run():
        with TestClient(app) as client:
            # Make 15 sequential requests
            for _ in range(15):
                client.get("/sync-io")
                
    benchmark(run)
