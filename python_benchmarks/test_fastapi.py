import asyncio
import time
import httpx
from fastapi import FastAPI
from fastapi.testclient import TestClient

CONCURRENT_REQUESTS = 15

app = FastAPI()


@app.get("/async-io")
async def async_io():
    await asyncio.sleep(0.002)
    return {"status": "ok"}


@app.get("/sync-io")
def sync_io():
    time.sleep(0.002)
    return {"status": "ok"}


def run_sync_blocking():
    responses = []
    with TestClient(app) as client:
        for _ in range(CONCURRENT_REQUESTS):
            responses.append(client.get("/sync-io"))
    return responses


def run_async_nonblocking():
    async def _run():
        async with httpx.AsyncClient(
            transport=httpx.ASGITransport(app=app), base_url="http://test"
        ) as client:
            tasks = [client.get("/async-io") for _ in range(CONCURRENT_REQUESTS)]
            return await asyncio.gather(*tasks)
    return asyncio.run(_run())


def test_async_faster_than_sync():
    """Prove concurrent async beats sequential sync for I/O-bound requests."""
    rounds = 3

    start = time.perf_counter()
    for _ in range(rounds):
        run_sync_blocking()
    t_sync = time.perf_counter() - start

    start = time.perf_counter()
    for _ in range(rounds):
        run_async_nonblocking()
    t_async = time.perf_counter() - start

    assert t_async < t_sync, (
        f'Async ({t_async:.6f}s) should be faster than '
        f'sync ({t_sync:.6f}s)'
    )
