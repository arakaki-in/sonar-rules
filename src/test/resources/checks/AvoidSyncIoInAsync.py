"""
Avoid Sync I/O in Async
=======================
Blocking or synchronous calls inside an async function halt the event loop,
preventing concurrent execution of other coroutines. Always use async equivalents.
"""

# Compliant
def normal_func():
    time.sleep(1)
    open("data.txt")
    requests.get("https://example.com")

async def async_func_compliant():
    await asyncio.sleep(1)

# Non-compliant
async def async_func_noncompliant():
    time.sleep(1)  # Noncompliant {{Avoid blocking/synchronous operations inside async functions. Use async counterparts instead.}}
    open("data.txt")  # Noncompliant {{Avoid blocking/synchronous operations inside async functions. Use async counterparts instead.}}
    requests.get("https://example.com")  # Noncompliant {{Avoid blocking/synchronous operations inside async functions. Use async counterparts instead.}}
    urllib.request.urlopen("https://example.com")  # Noncompliant {{Avoid blocking/synchronous operations inside async functions. Use async counterparts instead.}}
