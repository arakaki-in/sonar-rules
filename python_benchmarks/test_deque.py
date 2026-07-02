import time
from collections import deque

OP_COUNT = 3000


def list_left_ops(n):
    lst = []
    for i in range(n):
        lst.insert(0, i)
    for _ in range(n):
        lst.pop(0)
    return lst


def deque_left_ops(n):
    deq = deque()
    for i in range(n):
        deq.appendleft(i)
    for _ in range(n):
        deq.popleft()
    return deq


def test_deque_faster_than_list():
    """Prove deque O(1) operations are faster than list O(n) operations."""
    rounds = 5

    start = time.perf_counter()
    for _ in range(rounds):
        list_left_ops(OP_COUNT)
    t_list = time.perf_counter() - start

    start = time.perf_counter()
    for _ in range(rounds):
        deque_left_ops(OP_COUNT)
    t_deque = time.perf_counter() - start

    assert t_deque < t_list, (
        f'Deque ({t_deque:.6f}s) should be faster than '
        f'list ({t_list:.6f}s)'
    )
