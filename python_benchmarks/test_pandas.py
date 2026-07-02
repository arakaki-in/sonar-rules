import time
import pytest
import pandas as pd
import numpy as np

ROWS = 2000


@pytest.fixture
def large_dataframe():
    np.random.seed(42)
    return pd.DataFrame({
        'A': np.random.randn(ROWS),
        'B': np.random.randn(ROWS),
    })


def iterrows_approach(df):
    res = []
    for _, row in df.iterrows():
        res.append(row['A'] + row['B'])
    return res


def itertuples_approach(df):
    res = []
    for row in df.itertuples(index=False):
        res.append(row.A + row.B)
    return res


def vectorized_approach(df):
    return (df['A'] + df['B']).tolist()


def test_pandas_vectorized_fastest(large_dataframe):
    """Prove ordering: vectorized < itertuples < iterrows."""
    rounds = 3

    start = time.perf_counter()
    for _ in range(rounds):
        iterrows_approach(large_dataframe)
    t_iterrows = time.perf_counter() - start

    start = time.perf_counter()
    for _ in range(rounds):
        itertuples_approach(large_dataframe)
    t_itertuples = time.perf_counter() - start

    start = time.perf_counter()
    for _ in range(rounds):
        vectorized_approach(large_dataframe)
    t_vectorized = time.perf_counter() - start

    assert t_vectorized < t_itertuples, (
        f'Vectorized ({t_vectorized:.6f}s) should be faster than '
        f'itertuples ({t_itertuples:.6f}s)'
    )
    assert t_itertuples < t_iterrows, (
        f'itertuples ({t_itertuples:.6f}s) should be faster than '
        f'iterrows ({t_iterrows:.6f}s)'
    )
