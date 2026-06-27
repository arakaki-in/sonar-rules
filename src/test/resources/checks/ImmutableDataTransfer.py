import threading
import multiprocessing
import queue

def worker(data):
    pass

q = queue.Queue()

# Compliant cases
t1 = threading.Thread(target=worker, args=(1, "hello", (3, 4)))
p1 = multiprocessing.Process(target=worker, args=(5, "world"))
q.put((1, 2, 3))
q.put("message")

# Non-compliant cases
t2 = threading.Thread(target=worker, args=([1, 2],)) # Noncompliant {{Ensure data sent across thread or process boundaries is immutable (avoid sending mutable lists, dicts, or sets).}}
t3 = threading.Thread(target=worker, kwargs={"data": {"key": "value"}}) # Noncompliant {{Ensure data sent across thread or process boundaries is immutable (avoid sending mutable lists, dicts, or sets).}}
p2 = multiprocessing.Process(target=worker, args=({"a", "b"},)) # Noncompliant {{Ensure data sent across thread or process boundaries is immutable (avoid sending mutable lists, dicts, or sets).}}
q.put([1, 2]) # Noncompliant {{Ensure data sent across thread or process boundaries is immutable (avoid sending mutable lists, dicts, or sets).}}
q.put_nowait({"x": 1}) # Noncompliant {{Ensure data sent across thread or process boundaries is immutable (avoid sending mutable lists, dicts, or sets).}}

# Gaps: The following are mutable data transfers but are NOT flagged by the rule
import concurrent.futures
t4 = threading.Thread(None, worker, "thread-4", [1, 2])
my_list = [1, 2]
q.put(my_list)
with concurrent.futures.ThreadPoolExecutor() as executor:
    executor.submit(worker, [1, 2])

