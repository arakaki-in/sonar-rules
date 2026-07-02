import time
import timeit
import re
import asyncio
import pandas as pd
from collections import deque
import pytest

# Number of iterations for micro-benchmarks
ITERATIONS = 1_000_000

# =====================================================================
# PART 1: VALIDATING DELETED RULES (PROVING THEY WERE NONSENSE)
# =====================================================================

def test_benchmark_eager_regex():
    """
    Validates deleting 'AvoidEagerRegexCompilation'.
    We prove that module-level eager compilation (re.compile) is faster in loops
    than calling re.match directly (which has internal cache lookup overhead).
    """
    setup = "import re; pattern = re.compile(r'^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$'); email = 'test@example.com'"
    time_compiled = timeit.timeit(stmt="pattern.match(email)", setup=setup, number=ITERATIONS)
    time_direct = timeit.timeit(stmt="re.match(r'^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$', email)", setup=setup, number=ITERATIONS)
    
    print(f"\nRegex compilation: compiled={time_compiled:.4f}s, direct={time_direct:.4f}s")
    assert time_compiled < time_direct


def test_benchmark_try_except_control_flow():
    """
    Validates deleting 'AvoidTryExceptControlFlow'.
    We prove that in the happy path (key exists), try-except (EAFP) is faster
    than checking key existence first (LBYL) because it avoids the extra lookup check.
    """
    setup = "d = {'key': 42}"
    time_lbyl = timeit.timeit(stmt="if 'key' in d: val = d['key']", setup=setup, number=ITERATIONS)
    time_eafp = timeit.timeit(stmt="""
try:
    val = d['key']
except KeyError:
    val = None
""", setup=setup, number=ITERATIONS)
    
    print(f"\nTry/Except Control Flow: EAFP={time_eafp:.4f}s, LBYL={time_lbyl:.4f}s")
    assert time_eafp < time_lbyl


def test_benchmark_immutable_data_transfer():
    """
    Validates deleting 'ImmutableDataTransfer'.
    We prove that eagerly converting lists/sets to tuples/frozensets on local
    function boundaries adds significant CPU/allocation overhead.
    """
    setup = "data = [i for i in range(100)]; process = lambda x: len(x)"
    time_mutable = timeit.timeit(stmt="process(data)", setup=setup, number=ITERATIONS)
    time_immutable = timeit.timeit(stmt="process(tuple(data))", setup=setup, number=ITERATIONS)
    
    print(f"\nData Transfer: mutable={time_mutable:.4f}s, immutable_cast={time_immutable:.4f}s")
    assert time_mutable < time_immutable


# =====================================================================
# PART 2: VALIDATING PROPOSED NEW RULES (PROVING THEIR HIGH MERIT)
# =====================================================================

def test_benchmark_dict_keys_iteration():
    """
    Validates adding 'AvoidDictKeysIteration'.
    We prove that checking 'if key in dict' is faster than 'if key in dict.keys()'.
    """
    setup = "d = {str(i): i for i in range(100)}"
    time_direct = timeit.timeit(stmt="'50' in d", setup=setup, number=ITERATIONS)
    time_keys = timeit.timeit(stmt="'50' in d.keys()", setup=setup, number=ITERATIONS)
    
    print(f"\nDict keys lookup: direct={time_direct:.4f}s, keys()={time_keys:.4f}s")
    assert time_direct < time_keys


def test_benchmark_pandas_iterrows():
    """
    Validates adding 'AvoidPandasIterrows'.
    We prove that vectorized operations are order of magnitudes faster than .iterrows().
    """
    df = pd.DataFrame({"A": range(1000), "B": range(1000)})
    
    def run_iterrows():
        res = []
        for idx, row in df.iterrows():
            res.append(row["A"] + row["B"])
        return res
        
    def run_vectorized():
        return (df["A"] + df["B"]).tolist()
        
    time_iterrows = timeit.timeit(stmt=run_iterrows, number=100)
    time_vector = timeit.timeit(stmt=run_vectorized, number=100)
    
    print(f"\nPandas iteration: iterrows={time_iterrows:.4f}s, vectorized={time_vector:.4f}s")
    assert time_vector < time_iterrows / 10


def test_benchmark_deque_over_list_insert():
    """
    Validates adding 'DequeOverListInsert'.
    We prove that list.insert(0, val) is O(N) while deque.appendleft() is O(1).
    """
    setup = "from collections import deque"
    stmt_list = """
lst = []
for i in range(1000):
    lst.insert(0, i)
"""
    stmt_deque = """
deq = deque()
for i in range(1000):
    deq.appendleft(i)
"""
    time_list = timeit.timeit(stmt=stmt_list, setup=setup, number=1000)
    time_deque = timeit.timeit(stmt=stmt_deque, setup=setup, number=1000)
    
    print(f"\nLeft Insert: list.insert={time_list:.4f}s, deque.appendleft={time_deque:.4f}s")
    assert time_deque < time_list


def test_benchmark_none_comparison():
    """
    Validates adding 'NoneComparisonStyle'.
    We prove that 'x is None' is comparable in speed to 'x == None' (with minor CPU jitter),
    but is the mandatory standard for identity comparison to prevent __eq__ overhead/spoofing.
    """
    setup = "x = None"
    time_is = timeit.timeit(stmt="x is None", setup=setup, number=ITERATIONS)
    time_eq = timeit.timeit(stmt="x == None", setup=setup, number=ITERATIONS)
    
    print(f"\nNone comparison: is={time_is:.4f}s, ==={time_eq:.4f}s")
    assert time_is <= time_eq * 1.1


def test_benchmark_fstring_over_format():
    """
    Validates adding 'PreferFStringOverFormat'.
    We prove that f-strings are significantly faster than .format().
    """
    setup = "val = 100"
    time_fstring = timeit.timeit(stmt="f'Value is {val}'", setup=setup, number=ITERATIONS)
    time_format = timeit.timeit(stmt="'Value is {}'.format(val)", setup=setup, number=ITERATIONS)
    
    print(f"\nString formatting: f-string={time_fstring:.4f}s, .format()={time_format:.4f}s")
    assert time_fstring < time_format


def test_benchmark_map_lambda():
    """
    Validates adding 'AvoidMapLambda'.
    We prove that list comprehensions are faster than map(lambda ...).
    """
    setup = "data = list(range(100))"
    time_map = timeit.timeit(stmt="list(map(lambda x: x * 2, data))", setup=setup, number=ITERATIONS)
    time_comp = timeit.timeit(stmt="[x * 2 for x in data]", setup=setup, number=ITERATIONS)
    
    print(f"\nLambda map: map(lambda)={time_map:.4f}s, comprehension={time_comp:.4f}s")
    assert time_comp < time_map


# =====================================================================
# PART 3: VALIDATING ASYNC RULES
# =====================================================================

def test_async_blocking_sleep():
    """
    Validates adding 'AvoidSyncIoInAsync'.
    We prove that synchronous blocking sleep halts concurrent execution,
    whereas async sleep allows concurrent tasks to run in parallel.
    """
    async def blocking_task():
        time.sleep(0.05)  # blocking!
        
    async def non_blocking_task():
        await asyncio.sleep(0.05)  # cooperative yielding
        
    async def run():
        # Measure blocking execution
        start = time.perf_counter()
        await asyncio.gather(blocking_task(), blocking_task(), blocking_task())
        time_blocking = time.perf_counter() - start
        
        # Measure non-blocking execution
        start = time.perf_counter()
        await asyncio.gather(non_blocking_task(), non_blocking_task(), non_blocking_task())
        time_non_blocking = time.perf_counter() - start
        
        return time_blocking, time_non_blocking

    time_blocking, time_non_blocking = asyncio.run(run())
    print(f"\nAsync concurrency: blocking_sleep={time_blocking:.4f}s, async_sleep={time_non_blocking:.4f}s")
    assert time_non_blocking < time_blocking * 0.7
