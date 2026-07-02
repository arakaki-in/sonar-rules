import pytest
import pandas as pd
import numpy as np

@pytest.fixture
def large_dataframe():
    np.random.seed(42)
    rows = 2000
    df = pd.DataFrame({
        'A': np.random.randn(rows),
        'B': np.random.randn(rows)
    })
    return df

# Benchmark 1: Slow .iterrows() loop
def test_pandas_iterrows_loop(benchmark, large_dataframe):
    def run():
        res = []
        for idx, row in large_dataframe.iterrows():
            res.append(row['A'] + row['B'])
        return res
    benchmark(run)

# Benchmark 2: Faster .itertuples() loop
def test_pandas_itertuples_loop(benchmark, large_dataframe):
    def run():
        res = []
        for row in large_dataframe.itertuples(index=False):
            res.append(row.A + row.B)
        return res
    benchmark(run)

# Benchmark 3: Blazing fast vectorized pandas operation
def test_pandas_vectorized_op(benchmark, large_dataframe):
    def run():
        # Vectorized addition
        return (large_dataframe['A'] + large_dataframe['B']).tolist()
    benchmark(run)
