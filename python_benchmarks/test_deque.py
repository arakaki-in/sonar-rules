import pytest
from collections import deque

@pytest.fixture
def op_count():
    return 3000

# Benchmark 1: List insert(0, x) and pop(0) - O(N) overhead per operation
def test_list_left_insert_pop(benchmark, op_count):
    def run():
        lst = []
        for i in range(op_count):
            lst.insert(0, i)
        for _ in range(op_count):
            lst.pop(0)
    benchmark(run)

# Benchmark 2: Deque appendleft(x) and popleft() - O(1) overhead per operation
def test_deque_left_append_pop(benchmark, op_count):
    def run():
        deq = deque()
        for i in range(op_count):
            deq.appendleft(i)
        for _ in range(op_count):
            deq.popleft()
    benchmark(run)
