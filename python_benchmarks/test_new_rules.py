"""Performance benchmarks proving the 7 new optimization rules detect real slowdowns."""

import time
from collections import deque

N = 10_000


# ---------------------------------------------------------------------------
# 1. PreferSetMembership — list O(n) vs set O(1) membership in loops
# ---------------------------------------------------------------------------
def test_set_membership_faster():
    data = list(range(N))
    lookups = list(range(N))

    # O(n*m): list membership
    start = time.perf_counter()
    found_list = 0
    for x in lookups:
        if x in data:  # Noncompliant: O(n) each iteration
            found_list += 1
    t_list = time.perf_counter() - start

    # O(m): set membership
    data_set = set(data)
    start = time.perf_counter()
    found_set = 0
    for x in lookups:
        if x in data_set:  # Compliant: O(1)
            found_set += 1
    t_set = time.perf_counter() - start

    assert found_list == found_set
    assert t_set < t_list, (
        f'Set membership ({t_set:.6f}s) should be faster than '
        f'list membership ({t_list:.6f}s)'
    )


# ---------------------------------------------------------------------------
# 2. AvoidRangeLenIteration — range(len()) vs enumerate()
# ---------------------------------------------------------------------------
def test_direct_iteration_faster_than_range_len():
    data = list(range(N * 10))
    rounds = 30

    # range(len()) with index access — __getitem__ per iteration
    start = time.perf_counter()
    total_rl = 0
    for _ in range(rounds):
        acc = 0
        for i in range(len(data)):  # Noncompliant
            acc += data[i]
        total_rl += acc
    t_rl = time.perf_counter() - start

    # Direct iteration — no indexing overhead, no tuple creation
    start = time.perf_counter()
    total_direct = 0
    for _ in range(rounds):
        acc = 0
        for val in data:  # Compliant
            acc += val
        total_direct += acc
    t_direct = time.perf_counter() - start

    assert total_rl == total_direct
    assert t_direct < t_rl, (
        f'Direct iteration ({t_direct:.6f}s) should be faster than '
        f'range(len()) ({t_rl:.6f}s)'
    )


# ---------------------------------------------------------------------------
# 3. PreferIsinstanceOverTypeEquality — isinstance() handles subclasses correctly
# ---------------------------------------------------------------------------
def test_isinstance_handles_subclasses():
    """isinstance() correctly handles inheritance; type() == does not."""

    class MyInt(int):
        pass

    value = MyInt(42)

    # type() == — FAILS for subclasses (incorrect)
    type_check = type(value) == int  # Noncompliant
    assert type_check is False, (
        'type() == returns False for subclass — silently incorrect!'
    )

    # isinstance() — PASSES for subclasses (correct)
    isinstance_check = isinstance(value, int)  # Compliant
    assert isinstance_check is True, (
        'isinstance() correctly returns True for subclass'
    )


# ---------------------------------------------------------------------------
# 4. PreferDirectTruthiness — len() > 0 vs truthiness
# ---------------------------------------------------------------------------
def test_truthiness_faster_than_len():
    data = list(range(N))
    rounds = 500_000

    # len() > 0
    start = time.perf_counter()
    count_len = 0
    for _ in range(rounds):
        if len(data) > 0:  # Noncompliant
            count_len += 1
    t_len = time.perf_counter() - start

    # truthiness
    start = time.perf_counter()
    count_truth = 0
    for _ in range(rounds):
        if data:  # Compliant
            count_truth += 1
    t_truth = time.perf_counter() - start

    assert count_len == count_truth
    assert t_truth < t_len, (
        f'Direct truthiness ({t_truth:.6f}s) should be faster than '
        f'len() > 0 ({t_len:.6f}s)'
    )


# ---------------------------------------------------------------------------
# 5. PreferSetComprehension — set([lc]) vs {lc}
# ---------------------------------------------------------------------------
def test_set_comprehension_faster():
    data = list(range(N * 10))
    rounds = 20

    # set([list comprehension]) — allocates intermediate list
    start = time.perf_counter()
    for _ in range(rounds):
        s1 = set([x * 2 for x in data])  # Noncompliant
    t_listcomp = time.perf_counter() - start

    # set comprehension directly — no intermediate list
    start = time.perf_counter()
    for _ in range(rounds):
        s2 = {x * 2 for x in data}  # Compliant
    t_setcomp = time.perf_counter() - start

    assert s1 == s2
    assert t_setcomp < t_listcomp, (
        f'Set comprehension ({t_setcomp:.6f}s) should be faster than '
        f'set([list comprehension]) ({t_listcomp:.6f}s)'
    )


# ---------------------------------------------------------------------------
# 6. PreferReversedOverSlice — seq[::-1] vs reversed()
# ---------------------------------------------------------------------------
def test_reversed_faster_than_slice():
    data = list(range(N * 10))

    # [::-1] — creates full copy
    start = time.perf_counter()
    total_slice = 0
    for item in data[::-1]:  # Noncompliant
        total_slice += item
    t_slice = time.perf_counter() - start

    # reversed() — O(1) iterator, no copy
    start = time.perf_counter()
    total_rev = 0
    for item in reversed(data):  # Compliant
        total_rev += item
    t_rev = time.perf_counter() - start

    assert total_slice == total_rev
    assert t_rev < t_slice, (
        f'reversed() ({t_rev:.6f}s) should be faster than '
        f'[::-1] slice ({t_slice:.6f}s)'
    )


# ---------------------------------------------------------------------------
# 7. AvoidListAdditionInLoop — list + [x] vs list.append()
# ---------------------------------------------------------------------------
def test_append_faster_than_list_addition():
    n = 3000

    # list + [x] — creates new list each time, O(n^2)
    start = time.perf_counter()
    result_plus = []
    for x in range(n):
        result_plus = result_plus + [x]  # Noncompliant
    t_plus = time.perf_counter() - start

    # list.append() — O(1) amortized
    start = time.perf_counter()
    result_append = []
    for x in range(n):
        result_append.append(x)  # Compliant
    t_append = time.perf_counter() - start

    assert result_plus == result_append
    assert t_append < t_plus, (
        f'list.append() ({t_append:.6f}s) should be faster than '
        f'list + [x] ({t_plus:.6f}s)'
    )
