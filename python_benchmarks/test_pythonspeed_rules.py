"""Performance benchmarks proving the 7 new optimization rules detect real slowdowns."""

import time

N = 10_000


# ---------------------------------------------------------------------------
# 1. PreferSetMembership -- list O(n) vs set O(1) membership in loops
# ---------------------------------------------------------------------------
def test_set_membership_faster(benchmark):
    data = list(range(N))
    lookups = list(range(N))
    data_set = set(data)

    def list_membership():
        return sum(1 for x in lookups if x in data)

    def set_membership():
        return sum(1 for x in lookups if x in data_set)

    # Correctness: both approaches give same result
    assert list_membership() == set_membership()

    # Quick timing comparison for CI assertion
    t0 = time.perf_counter()
    _ = list_membership()
    t_list = time.perf_counter() - t0
    t0 = time.perf_counter()
    _ = set_membership()
    t_set = time.perf_counter() - t0
    assert t_set < t_list, (
        f"Set membership ({t_set:.6f}s) should be faster than "
        f"list membership ({t_list:.6f}s)"
    )

    # Statistical benchmark recording
    benchmark(set_membership)


# ---------------------------------------------------------------------------
# 2. AvoidRangeLenIteration -- range(len()) vs direct iteration
# ---------------------------------------------------------------------------
def test_direct_iteration_faster_than_range_len(benchmark):
    data = list(range(N * 10))
    rounds = 30

    def range_len_iter():
        acc = 0
        for i in range(len(data)):
            acc += data[i]
        return acc

    def direct_iter():
        acc = 0
        for val in data:
            acc += val
        return acc

    # Correctness: both approaches give same total
    total_rl = sum(range_len_iter() for _ in range(rounds))
    total_direct = sum(direct_iter() for _ in range(rounds))
    assert total_rl == total_direct, (
        f"range(len()) sum {total_rl} != direct iteration sum {total_direct}"
    )

    # Quick timing comparison for CI assertion
    t0 = time.perf_counter()
    for _ in range(rounds):
        _ = range_len_iter()
    t_rl = time.perf_counter() - t0
    t0 = time.perf_counter()
    for _ in range(rounds):
        _ = direct_iter()
    t_direct = time.perf_counter() - t0
    assert t_direct < t_rl, (
        f"Direct iteration ({t_direct:.6f}s) should be faster than "
        f"range(len()) ({t_rl:.6f}s)"
    )

    # Statistical benchmark recording
    benchmark(direct_iter)


# ---------------------------------------------------------------------------
# 3. PreferIsinstanceOverTypeEquality -- isinstance() handles subclasses
# ---------------------------------------------------------------------------
def test_isinstance_handles_subclasses():
    """isinstance() correctly handles inheritance; type() == does not."""

    class MyInt(int):
        pass

    value = MyInt(42)

    # type() == -- FAILS for subclasses (incorrect)
    type_check = type(value) == int  # Noncompliant
    assert type_check is False, (
        "type() == returns False for subclass -- silently incorrect!"
    )

    # isinstance() -- PASSES for subclasses (correct)
    isinstance_check = isinstance(value, int)  # Compliant
    assert isinstance_check is True, (
        "isinstance() correctly returns True for subclass"
    )


# ---------------------------------------------------------------------------
# 4. PreferDirectTruthiness -- len() > 0 vs truthiness
# ---------------------------------------------------------------------------
def test_truthiness_faster_than_len(benchmark):
    data = list(range(N))

    def len_check():
        return len(data) > 0

    def truthiness():
        return bool(data)

    # Correctness: both approaches give same result
    assert len_check() == truthiness()

    # Quick timing comparison for CI assertion
    rounds = 500_000
    t0 = time.perf_counter()
    for _ in range(rounds):
        _ = len_check()
    t_len = time.perf_counter() - t0
    t0 = time.perf_counter()
    for _ in range(rounds):
        _ = truthiness()
    t_truth = time.perf_counter() - t0
    assert t_truth < t_len, (
        f"Direct truthiness ({t_truth:.6f}s) should be faster than "
        f"len() > 0 ({t_len:.6f}s)"
    )

    # Statistical benchmark recording
    benchmark(truthiness)


# ---------------------------------------------------------------------------
# 5. PreferSetComprehension -- set([lc]) vs {lc}
# ---------------------------------------------------------------------------
def test_set_comprehension_faster(benchmark):
    data = list(range(N * 10))

    def set_from_listcomp():
        return set([x * 2 for x in data])

    def set_comp():
        return {x * 2 for x in data}

    # Correctness: both produce the same set
    assert set_from_listcomp() == set_comp()

    # Quick timing comparison for CI assertion
    rounds = 20
    t0 = time.perf_counter()
    for _ in range(rounds):
        _ = set_from_listcomp()
    t_listcomp = time.perf_counter() - t0
    t0 = time.perf_counter()
    for _ in range(rounds):
        _ = set_comp()
    t_setcomp = time.perf_counter() - t0
    assert t_setcomp < t_listcomp, (
        f"Set comprehension ({t_setcomp:.6f}s) should be faster than "
        f"set([list comprehension]) ({t_listcomp:.6f}s)"
    )

    # Statistical benchmark recording
    benchmark(set_comp)


# ---------------------------------------------------------------------------
# 6. AvoidListAdditionInLoop -- list + [x] vs list.append()
# ---------------------------------------------------------------------------
def test_append_faster_than_list_addition(benchmark):
    n = 3000

    def list_addition():
        result = []
        for x in range(n):
            result = result + [x]
        return result

    def list_append():
        result = []
        for x in range(n):
            result.append(x)
        return result

    # Correctness: both approaches produce same result
    assert list_addition() == list_append()

    # Quick timing comparison for CI assertion
    t0 = time.perf_counter()
    _ = list_addition()
    t_plus = time.perf_counter() - t0
    t0 = time.perf_counter()
    _ = list_append()
    t_append = time.perf_counter() - t0
    assert t_append < t_plus, (
        f"list.append() ({t_append:.6f}s) should be faster than "
        f"list + [x] ({t_plus:.6f}s)"
    )

    # Statistical benchmark recording
    benchmark(list_append)
